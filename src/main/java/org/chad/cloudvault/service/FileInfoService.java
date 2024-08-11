package org.chad.cloudvault.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.chad.cloudvault.domain.dto.FileUploadDTO;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.po.FileInfo;
import org.chad.cloudvault.domain.vo.FileExistVO;
import org.chad.cloudvault.domain.vo.FileInfoPageVO;
import org.chad.cloudvault.domain.vo.FileUploadVO;

import java.util.List;

public interface FileInfoService extends IService<FileInfo> {
    Result<IPage<FileInfoPageVO>> fileList(Integer pageNo, Long filePid);

    Result<FileUploadVO> upload(FileUploadDTO requestParm);

    Result<FileExistVO> checkFileExist(String identifier, String fileName);

    Result<Void> checkUserSpace(Long useSpace);

    Result<Void> getUrl(Long fileId);

    Result<Void> deleteByFileIds(List<Long> ids);
}
