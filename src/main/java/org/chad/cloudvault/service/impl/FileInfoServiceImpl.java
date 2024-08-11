package org.chad.cloudvault.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.chad.cloudvault.common.user.UserHolder;
import org.chad.cloudvault.config.MinioConfig;
import org.chad.cloudvault.domain.dto.FileUploadDTO;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.po.FileInfo;
import org.chad.cloudvault.domain.vo.FileExistVO;
import org.chad.cloudvault.domain.vo.FileInfoPageVO;
import org.chad.cloudvault.domain.vo.FileUploadVO;
import org.chad.cloudvault.mapper.FileInfoMapper;
import org.chad.cloudvault.service.FileInfoService;
import org.chad.cloudvault.service.UserInfoService;
import org.chad.cloudvault.utils.FileUtils;
import org.chad.cloudvault.utils.MinioUtil;
import org.chad.cloudvault.utils.RedisIdWorker;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static org.chad.cloudvault.common.constant.FileConstant.FILE_DEL_FLAG_NORMAL;
import static org.chad.cloudvault.common.constant.FileConstant.FILE_FOLDER_TYPE_FILE;
import static org.chad.cloudvault.common.constant.RedisConstants.FILE_UPLOAD_KEY;
import static org.chad.cloudvault.common.constant.RedisConstants.USERINFO_FREESPACE_KEY;

@Service
@RequiredArgsConstructor
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {
    
    private final StringRedisTemplate stringRedisTemplate;

    private final RedisIdWorker redisIdWorker;

    private final UserInfoService userInfoService;

    private final MinioConfig minioConfig;

    private final MinioUtil minioUtil;

    @Override
    public Result<IPage<FileInfoPageVO>> fileList(Integer pageNo, Long filePid) {
        LambdaQueryWrapper<FileInfo> queryWrapper = Wrappers.lambdaQuery(FileInfo.class)
                .eq(FileInfo::getUserId, UserHolder.getUser().getId())
                .eq(FileInfo::getFilePid, filePid)
                .eq(FileInfo::getDelFlag, 0)
                .orderByDesc(FileInfo::getUpdateTime);
        Page<FileInfo> resultPage = page(new Page<>(pageNo, 15), queryWrapper);
        IPage<FileInfoPageVO> convert = resultPage.convert(each -> BeanUtil.copyProperties(each, FileInfoPageVO.class));
        return Result.success(convert);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<FileUploadVO> upload(FileUploadDTO requestParm) {
        FileUploadVO fileUploadVO = new FileUploadVO();
        //1.文件id生成
        Long fileId = requestParm.getFileId();
        if(fileId == null || fileId == 0){
            //第一个分片或者没分片的文件
            fileId = redisIdWorker.nextID(FILE_UPLOAD_KEY);
            fileUploadVO.setFileId(fileId);
        }
        MultipartFile uploadFile = requestParm.getFile();
        if(uploadFile.isEmpty()){
            return Result.error("文件未添加");
        }
        Long userId = UserHolder.getUser().getId();
        Long filePid = requestParm.getFilePid();
        String uploadFileName = uploadFile.getOriginalFilename();
        String fileName = requestParm.getFileName();
        Integer chunkIndex = requestParm.getChunkIndex();
        long uploadFileSize = uploadFile.getSize();
        if(StrUtil.isBlank(uploadFileName)){
            return Result.error("不能上传空文件名的文件");
        }
        //3.没有相同文件上传过
        if(chunkIndex == 0){
            //4.没分片，小文件秒传
            try {
                FileInfo fileInfo = getById(filePid);
                String objectName;
                if(BeanUtil.isEmpty(fileInfo)){
                    objectName = UserHolder.getUser().getId().toString() + "/" + fileName;
                }else{
                    objectName = fileInfo.getFilePath() + "/" + fileName;
                }
                minioUtil.uploadFile(minioConfig.getBucketName(), uploadFile, objectName, uploadFile.getContentType());
                buildFileInfo(fileId, userId, DigestUtil.md5Hex(uploadFile.getInputStream()), filePid,
                        uploadFileSize, fileName, "",
                        objectName,
                        FILE_FOLDER_TYPE_FILE, FileUtils.getFileCategory(fileName),
                        FileUtils.getFileType(fileName), FILE_DEL_FLAG_NORMAL);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return Result.success("上传成功");
        }
        //5.分片
        return null;
    }

    @Override
    public Result<FileExistVO> checkFileExist(String identifier) {
        LambdaQueryWrapper<FileInfo> queryWrapper = Wrappers.lambdaQuery(FileInfo.class)
                .eq(FileInfo::getFileMd5, identifier)
                .ne(FileInfo::getDelFlag, 2);
        //虽然是软删除，但不能把这个文件当作复用有效文件
        List<FileInfo> fileInfoList = list(queryWrapper);
        if(fileInfoList.size() > 0){
            //2.1有相同文件被上传过
            FileInfo fileInfo = fileInfoList.get(0);
            Long userId = UserHolder.getUser().getId();
            if(!Objects.equals(fileInfo.getUserId(), userId)){
                //2.2别人上传的，直接拿来引用即可
                fileInfo.setUserId(userId);
                save(fileInfo);
            }else{
                //2.3自己上传的，需要对本次记录进行改名
                //TODO:万一是不同名字相同内容的呢？
                String fileName = fileInfo.getFileName() + '(' + fileInfoList.size() + ')';
                fileInfo.setFileName(fileName);
                save(fileInfo);
            }
            return Result.success(new FileExistVO(true));
        }
        return Result.success(new FileExistVO(false));
    }

    @Override
    public Result<Void> checkUserSpace(Long useSpace) {
        Long userId = UserHolder.getUser().getId();
        String str = stringRedisTemplate.opsForValue().get(USERINFO_FREESPACE_KEY + userId);
        if(StrUtil.isEmpty(str)){
            return Result.error("用户云盘空间出现异常，请检查登陆状态");
        }
        long freeSize = Long.parseLong(str);
        if(freeSize < useSpace){
            return Result.error("云盘空间不足");
        }
        return Result.success("用户内存足够");
    }

    @Override
    public Result<Void> getUrl(Long fileId) {
        FileInfo fileInfo = getById(fileId);
        String filePath = fileInfo.getFilePath();
        String url = minioUtil.getPresignedObjectUrl(minioConfig.getBucketName(), filePath);
        return Result.success(url);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Void> deleteByFileIds(List<Long> ids) {
        LambdaUpdateWrapper<FileInfo> updateWrapper = Wrappers.lambdaUpdate(FileInfo.class)
                .eq(FileInfo::getUserId, UserHolder.getUser().getId())
                .in(FileInfo::getFileId, ids)
                .set(FileInfo::getDelFlag, 1)
                .set(FileInfo::getRecoveryTime, new Date());
        boolean update = update(updateWrapper);
        if(update){
            LambdaQueryWrapper<FileInfo> queryWrapper = Wrappers.lambdaQuery(FileInfo.class)
                    .eq(FileInfo::getUserId, UserHolder.getUser().getId())
                    .in(FileInfo::getFileId, ids)
                    .eq(FileInfo::getDelFlag, 1);
            List<FileInfo> list = list(queryWrapper);
            Long delSize = list.stream()
                    .mapToLong(FileInfo::getFileSize)
                    .sum();
            updateUserSpace(UserHolder.getUser().getId(), delSize, true);
            list.forEach(fileInfo -> {
                if(fileInfo.getFolderType() == 1){
                    //文件夹需要递归删除
                    minioUtil.removeDir(minioConfig.getBucketName(), fileInfo.getFilePath());
                }else{
                    minioUtil.removeFile(minioConfig.getBucketName(), fileInfo.getFilePath());
                }
            });
        }
        return Result.success("删除成功");
    }

    private void updateUserSpace(Long userId, Long size, boolean add){
        //add:true代表相加，false相减
        String s = stringRedisTemplate.opsForValue().get(USERINFO_FREESPACE_KEY + userId);
        if(StrUtil.isBlank(s)){
            throw new RuntimeException("用户空间异常");
        }
        long curSize = Long.parseLong(s);
        long updatedSize = add ? curSize + size : curSize - size;
        userInfoService.updateSpace(updatedSize);
        stringRedisTemplate.opsForValue().set(USERINFO_FREESPACE_KEY + userId, String.valueOf(updatedSize));
    }

    private void buildFileInfo(Long fileId, Long userId,
                               String fileMd5,
                               Long filePid, Long fileSize,
                               String fileName, String fileCover,
                               String filePath,Byte folderType,
                               Byte fileCategory,Byte fileType,
                               Byte status){
        FileInfo fileInfo = FileInfo.builder()
                .fileId(fileId)
                .userId(userId)
                .fileMd5(fileMd5)
                .filePid(filePid)
                .fileSize(fileSize)
                .fileName(fileName)
                .fileCover(fileCover)
                .filePath(filePath)
                .folderType(folderType)
                .fileCategory(fileCategory)
                .fileType(fileType)
                .status(status)
                .build();
        save(fileInfo);
    }
}
