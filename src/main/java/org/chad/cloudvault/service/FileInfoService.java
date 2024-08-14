package org.chad.cloudvault.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.chad.cloudvault.domain.dto.FileUploadDTO;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.po.FileInfo;
import org.chad.cloudvault.domain.vo.FileExistVO;
import org.chad.cloudvault.domain.vo.FileInfoPageVO;
import org.chad.cloudvault.domain.vo.FileUploadVO;
import org.chad.cloudvault.domain.vo.FileUrlVO;

import java.util.List;

public interface FileInfoService extends IService<FileInfo> {

    Result<FileUploadVO> upload(FileUploadDTO requestParm);

    Result<FileExistVO> checkFileExist(String identifier, String fileName);

    Result<Void> checkUserSpace(Long useSpace);

    Result<FileUrlVO> getUrl(List<Long> fileId);

    Result<Void> deleteByFileIds(List<Long> ids);

    Result<Void> updateByFileId(Long fileId, String name);

    Result<IPage<FileInfoPageVO>> recycleFileList(Integer pageNo, Long filePid);

    Result<Void> addRecycleFile(List<Long> ids);

    Result<Void> recovery(List<Long> ids);

    Result<IPage<FileInfoPageVO>> fileListByCategory(Integer pageNo, Long filePid, Integer category, Integer pageSize);

    Result<Void> createDir(String name, Long fileId);
}
