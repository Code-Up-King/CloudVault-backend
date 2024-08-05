package org.chad.cloudvault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.chad.cloudvault.domain.dto.UserRegisterDTO;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.po.User;
import org.chad.cloudvault.domain.vo.UserVO;

public interface UserService extends IService<User> {
    Result<Void> register(UserRegisterDTO userRegisterDTO);
}
