package org.chad.cloudvault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.chad.cloudvault.domain.po.UserInfo;

public interface UserInfoService extends IService<UserInfo> {
    void updateSpace(Long freeSize);
}
