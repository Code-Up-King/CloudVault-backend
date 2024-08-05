package org.chad.cloudvault.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.chad.cloudvault.domain.dto.UserRegisterDTO;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.po.User;
import org.chad.cloudvault.domain.vo.UserVO;
import org.chad.cloudvault.mapper.UserMapper;
import org.chad.cloudvault.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private String sault = "chad";

    @Override
    public Result<Void> register(UserRegisterDTO userRegisterDTO) {
        if(!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())){
            return Result.error("密码两次输入不一致");
        }
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getUsername, userRegisterDTO.getUsername());
        User one = getOne(queryWrapper);
        if(BeanUtil.isNotEmpty(one)){
            return Result.error("用户名已经被使用");
        }
        String str = userRegisterDTO.getPassword() + sault;
        String password = Arrays.toString(MD5.create().digest(str));
        User user = User.builder()
                .username(userRegisterDTO.getUsername())
                .password(password)
                .icon("https://avatars.githubusercontent.com/u/80665075?v=4")
                .build();
        save(user);
        return Result.success();
    }
}
