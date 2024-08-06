package org.chad.cloudvault.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.chad.cloudvault.common.user.UserHolder;
import org.chad.cloudvault.domain.dto.FileUploadDTO;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.po.FileInfo;
import org.chad.cloudvault.domain.vo.FileInfoPageVO;
import org.chad.cloudvault.domain.vo.FileUploadVO;
import org.chad.cloudvault.mapper.FileInfoMapper;
import org.chad.cloudvault.service.FileInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {
    @Value("file.ShardSize")
    private Integer ShardSize;
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
        if(requestParm.getFile().isEmpty()){
            return Result.error("文件未添加");
        }
        //1.检验剩余空间大小
        //TODO:增加用户剩余空间的判断
        //2.通过MD5来判断是否已经保存了相同的文件
        LambdaQueryWrapper<FileInfo> queryWrapper = Wrappers.lambdaQuery(FileInfo.class)
                .eq(FileInfo::getFileMd5, requestParm.getFileMD5())
                .ne(FileInfo::getDelFlag, 2);
        //虽然是软删除，但不能把这个文件当作复用有效文件
        List<FileInfo> fileInfoList = list(queryWrapper);
        if(fileInfoList.size() > 0){
            //2.1有相同文件被上传过
            FileInfo fileInfo = fileInfoList.get(0);
            fileInfo.setUserId(UserHolder.getUser().getId());
            save(fileInfo);
            return Result.success(new FileUploadVO(requestParm.getFilePid(), 1));
        }
        //3.没有相同文件上传过
        if(requestParm.getChunkIndex() == 0){
            //4.没分片，小文件秒传

        }
        //5.分片
        return null;
    }
}
