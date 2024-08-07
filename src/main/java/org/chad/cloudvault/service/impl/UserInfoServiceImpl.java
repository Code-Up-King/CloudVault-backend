package org.chad.cloudvault.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.chad.cloudvault.domain.po.UserInfo;
import org.chad.cloudvault.mapper.UserInfoMapper;
import org.chad.cloudvault.service.UserInfoService;
import org.springframework.stereotype.Service;

@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {
}
