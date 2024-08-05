package org.chad.cloudvault.controller;

import lombok.RequiredArgsConstructor;
import org.chad.cloudvault.domain.dto.UserRegisterDTO;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.vo.UserVO;
import org.chad.cloudvault.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    @PostMapping("/register")
    public Result<Void> register(@RequestBody UserRegisterDTO userRegisterDTO){
        return userService.register(userRegisterDTO);
    }
}
