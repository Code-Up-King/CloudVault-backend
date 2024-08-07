package org.chad.cloudvault.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.chad.cloudvault.domain.po.UserInfo;

@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
