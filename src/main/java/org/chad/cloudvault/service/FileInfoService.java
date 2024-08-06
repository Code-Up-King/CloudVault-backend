package org.chad.cloudvault.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.po.FileInfo;
import org.chad.cloudvault.domain.vo.FileInfoPageVO;

public interface FileInfoService extends IService<FileInfo> {
    Result<IPage<FileInfoPageVO>> fileList(Integer pageNo);
}
