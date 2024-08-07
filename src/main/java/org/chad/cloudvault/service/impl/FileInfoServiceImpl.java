package org.chad.cloudvault.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.chad.cloudvault.common.user.UserHolder;
import org.chad.cloudvault.domain.dto.FileUploadDTO;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.po.FileInfo;
import org.chad.cloudvault.domain.vo.FileInfoPageVO;
import org.chad.cloudvault.domain.vo.FileUploadVO;
import org.chad.cloudvault.mapper.FileInfoMapper;
import org.chad.cloudvault.service.FileInfoService;
import org.chad.cloudvault.utils.RedisIdWorker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.chad.cloudvault.common.constant.FileConstant.PAN_PATH;
import static org.chad.cloudvault.common.constant.FileConstant.UPLOAD_SUCCESS;
import static org.chad.cloudvault.common.constant.RedisConstants.FILE_UPLOAD_KEY;
import static org.chad.cloudvault.common.constant.RedisConstants.USERINFO_FREESPACE_KEY;

@Service
@RequiredArgsConstructor
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {
    
    private final StringRedisTemplate stringRedisTemplate;

    private final RedisIdWorker redisIdWorker;
    
    @Value("${file.shard-size}")
    private Long shardSize;
    @Override
    public Result<IPage<FileInfoPageVO>> fileList(Integer pageNo) {
        LambdaQueryWrapper<FileInfo> queryWrapper = Wrappers.lambdaQuery(FileInfo.class)
                .eq(FileInfo::getUserId, UserHolder.getUser().getId())
                .eq(FileInfo::getDelFlag, 0)
                .orderByDesc(FileInfo::getUpdateTime);
        Page<FileInfo> resultPage = page(new Page<>(pageNo, 15), queryWrapper);
        IPage<FileInfoPageVO> convert = resultPage.convert(each -> BeanUtil.copyProperties(each, FileInfoPageVO.class));
        return Result.success(convert);
    }

    @Override
    public Result<FileUploadVO> upload(FileUploadDTO requestParm) {
        FileUploadVO fileUploadVO = new FileUploadVO();
        long fileId = -1;
        if(BeanUtil.isEmpty(requestParm.getFileId())){
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
        //1.检验剩余空间大小
        String str = stringRedisTemplate.opsForValue().get(USERINFO_FREESPACE_KEY + userId);
        if(BeanUtil.isEmpty(str)){
            return Result.error("用户云盘空间出现异常，请检查登陆状态");
        }
        long freeSize = Long.parseLong(str);
        if(freeSize < uploadFile.getSize()){
            return Result.error("云盘空间不足");
        }
        //2.通过MD5来判断是否已经保存了相同的文件
        LambdaQueryWrapper<FileInfo> queryWrapper = Wrappers.lambdaQuery(FileInfo.class)
                .eq(FileInfo::getFileMd5, requestParm.getFileMD5())
                .ne(FileInfo::getDelFlag, 2);
        //虽然是软删除，但不能把这个文件当作复用有效文件
        List<FileInfo> fileInfoList = list(queryWrapper);
        if(fileInfoList.size() > 0){
            //2.1有相同文件被上传过
            FileInfo fileInfo = fileInfoList.get(0);
            fileInfo.setUserId(userId);
            save(fileInfo);
            fileUploadVO.setStatus(UPLOAD_SUCCESS);
            return Result.success(new FileUploadVO(filePid, 1));
        }
        //3.没有相同文件上传过
        if(requestParm.getChunkIndex() == 0){
            //4.没分片，小文件秒传
            try{
                FileInfo fileInfo = getById(filePid);
                String filePath = PAN_PATH + userId;
                if(BeanUtil.isEmpty(fileInfo)){
                    //没有上级目录，就是在用户盘文件夹下
                    File dir = new File(filePath);
                    if(!dir.exists()){
                        //创建用户的盘
                        dir.mkdir();
                    }
                }
                filePath = fileInfo.getFilePath();
                File file = new File(filePath + uploadFile.getOriginalFilename());
                uploadFile.transferTo(file);
                //todo:构建文件信息实体，插入数据库
//                buildFileInfo(fileId == -1?requestParm.getFileId():fileId,
//                        userId, DigestUtil.md5Hex(file), filePid, uploadFile.getSize(),
//                        uploadFile.getName(), );
                return Result.successMsg("上传成功");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //5.分片
        return null;
    }

    private void buildFileInfo(Long fileId, Long userId,
                               String fileMd5,
                               Long filePid, Long fileSize,
                               String fileName, String fileCover,
                               String filePath,
                               Byte folderType, Byte fileCategory,
                               Byte fileType, Byte status){
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
