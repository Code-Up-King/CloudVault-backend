package org.chad.cloudvault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.chad.cloudvault.domain.dto.UserLoginDTO;
import org.chad.cloudvault.domain.dto.UserRegisterDTO;
import org.chad.cloudvault.domain.dto.UserUpdateDTO;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.po.User;
import org.chad.cloudvault.domain.vo.UserInfoVO;
import org.chad.cloudvault.domain.vo.UserLoginVO;

public interface UserService extends IService<User> {
    Result<Void> register(UserRegisterDTO userRegisterDTO);

    Result<UserLoginVO> login(UserLoginDTO userLoginDTO);

    Result<Void> update(UserUpdateDTO requestparm, String token);

    Result<Void> logout(String token);

    Result<UserInfoVO> getUserInfo();
}
