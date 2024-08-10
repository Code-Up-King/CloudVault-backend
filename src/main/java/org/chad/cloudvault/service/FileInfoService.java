package org.chad.cloudvault.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.chad.cloudvault.domain.dto.FileUploadDTO;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.po.FileInfo;
import org.chad.cloudvault.domain.vo.FileExistVO;
import org.chad.cloudvault.domain.vo.FileInfoPageVO;
import org.chad.cloudvault.domain.vo.FileUploadVO;

public interface FileInfoService extends IService<FileInfo> {
    Result<IPage<FileInfoPageVO>> fileList(Integer pageNo);

    Result<FileUploadVO> upload(FileUploadDTO requestParm);

    Result<FileExistVO> checkFileExist(String identifier);

    Result<Void> checkUserSpace(Long useSpace);

    Result<Void> getUrl(Long fileId);
}
