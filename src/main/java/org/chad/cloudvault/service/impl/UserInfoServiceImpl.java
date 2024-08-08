package org.chad.cloudvault.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.chad.cloudvault.common.user.UserHolder;
import org.chad.cloudvault.domain.po.UserInfo;
import org.chad.cloudvault.mapper.UserInfoMapper;
import org.chad.cloudvault.service.UserInfoService;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
    @Override
    public void updateSpace(Long freeSize) {
        LambdaUpdateWrapper<UserInfo> updateWrapper = Wrappers.lambdaUpdate(UserInfo.class)
                .eq(UserInfo::getUserId, UserHolder.getUser().getId())
                .set(UserInfo::getFreeSize, freeSize);
        update(updateWrapper);
    }
}
