package org.chad.cloudvault.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.chad.cloudvault.common.user.UserDTO;
import org.chad.cloudvault.common.user.UserHolder;
import org.chad.cloudvault.domain.dto.UserLoginDTO;
import org.chad.cloudvault.domain.dto.UserRegisterDTO;
import org.chad.cloudvault.domain.dto.UserUpdateDTO;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.po.User;
import org.chad.cloudvault.mapper.UserMapper;
import org.chad.cloudvault.service.UserService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.chad.cloudvault.common.constant.RedisConstants.LOGIN_USER_KEY;
import static org.chad.cloudvault.common.constant.RedisConstants.LOGIN_USER_TTL;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Result<Void> register(UserRegisterDTO userRegisterDTO) {
        if(!userRegisterDTO.getPassword().equals(userRegisterDTO.getConfirmPassword())){
            return Result.error("密码两次输入不一致");
        }
        User one = getUserByUsername(userRegisterDTO.getUsername());
        if(BeanUtil.isNotEmpty(one)){
            return Result.error("用户名已经被使用");
        }
        String password = BCrypt.hashpw(userRegisterDTO.getPassword(), BCrypt.gensalt());
        User user = User.builder()
                .username(userRegisterDTO.getUsername())
                .password(password)
                .icon("https://avatars.githubusercontent.com/u/80665075?v=4")
                .build();
        save(user);
        return Result.success();
    }

    @Override
    public Result<String> login(UserLoginDTO userLoginDTO) {
        User user = getUserByUsername(userLoginDTO.getUsername());
        if(BeanUtil.isEmpty(user)){
            return Result.error(String.format("用户%s不存在", userLoginDTO.getUsername()));
        }
        if(!BCrypt.checkpw(userLoginDTO.getPassword(), user.getPassword())){
            return Result.error("密码错误");
        }
        String token = UUID.randomUUID().toString();
        //将User对象转为HashMap存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        getUserMap(token, userDTO);
        return Result.success(token);
    }

    @Override
    public Result<String> update(UserUpdateDTO requestparm, String token) {
        LambdaUpdateWrapper<User> updateWrapper = Wrappers.lambdaUpdate(User.class)
                .set(requestparm.getUsername()!=null, User::getUsername, requestparm.getUsername())
                .set(requestparm.getPassword()!=null, User::getPassword, BCrypt.hashpw(requestparm.getPassword(), BCrypt.gensalt()))
                .eq(User::getId, UserHolder.getUser().getId())
                .eq(User::getDelFlag, 0);
        update(updateWrapper);
        UserDTO user = UserHolder.getUser();
        if(requestparm.getUsername()!=null){
            user.setUsername(requestparm.getUsername());
        }
        getUserMap(token, user);
        return Result.success("修改成功");
    }

    private void getUserMap(String token, UserDTO user) {
        Map<String, Object> userMap = BeanUtil.beanToMap(user, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        String tokenKey = LOGIN_USER_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);
    }

    private User getUserByUsername(String username){
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getUsername, username);
        User user = getOne(queryWrapper);
        return user;
    }
}
