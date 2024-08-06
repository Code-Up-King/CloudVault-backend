package org.chad.cloudvault.controller;

import cn.hutool.core.bean.BeanUtil;
import lombok.RequiredArgsConstructor;
import org.chad.cloudvault.common.user.UserDTO;
import org.chad.cloudvault.common.user.UserHolder;
import org.chad.cloudvault.domain.dto.UserLoginDTO;
import org.chad.cloudvault.domain.dto.UserRegisterDTO;
import org.chad.cloudvault.domain.dto.UserUpdateDTO;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.vo.UserVO;
import org.chad.cloudvault.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @PostMapping("/register")
    public Result<Void> register(@RequestBody UserRegisterDTO userRegisterDTO){
        return userService.register(userRegisterDTO);
    }

    @PostMapping("/login")
    public Result<String> login(@RequestBody UserLoginDTO userLoginDTO){
        return userService.login(userLoginDTO);
    }

    @DeleteMapping("/logout")
    public Result<Void> logout(@RequestHeader("authorization")String token){
        return userService.logout(token);
    }

    @PostMapping("/update")
    public Result<Void> update(@RequestBody UserUpdateDTO requestparm, @RequestHeader("authorization")String token){
        return userService.update(requestparm, token);
    }

    @GetMapping("/userInfo")
    public Result<UserDTO> getUserInfo(){
        return Result.successDataAndMsg(UserHolder.getUser(), "获取成功");
    }
}
