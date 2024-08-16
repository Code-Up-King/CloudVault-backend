package org.chad.cloudvault.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.chad.cloudvault.domain.dto.ShareCreateDTO;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.po.FileShare;
import org.chad.cloudvault.domain.vo.FileInfoPageVO;
import org.chad.cloudvault.domain.vo.FileSharePageVO;
import org.chad.cloudvault.domain.vo.ShareCreateVO;
import org.chad.cloudvault.domain.vo.ShareInfoVO;

public interface FileShareService extends IService<FileShare> {
    Result<IPage<FileSharePageVO>> list(Integer pageNo, Integer pageSize);

    Result<ShareCreateVO> add(ShareCreateDTO requestparm);

    Result<Void> cancel(Long shareId);

    Result<ShareInfoVO> getShareInfo(Long shareId);

    Result<Void> checkCode(Long shareId, String code);

    Result<IPage<FileInfoPageVO>> shareList(Integer pageNo, Long fileId, Integer pageSize, Long shareId);
}
