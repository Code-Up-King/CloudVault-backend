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
import org.chad.cloudvault.domain.po.UserInfo;
import org.chad.cloudvault.domain.vo.UserInfoVO;
import org.chad.cloudvault.mapper.UserMapper;
import org.chad.cloudvault.service.UserInfoService;
import org.chad.cloudvault.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.chad.cloudvault.common.constant.RedisConstants.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final StringRedisTemplate stringRedisTemplate;

    private final UserInfoService userInfoService;
    @Value("${file.init-size}")
    private Long initSize;

    @Override
    @Transactional(rollbackFor = Exception.class)
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
        UserInfo userInfo = UserInfo.builder()
                .userId(user.getId())
                .totalSize(initSize)
                .freeSize(initSize)
                .build();
        userInfoService.save(userInfo);
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
        UserInfo userInfo = userInfoService.getById(user.getId());
        stringRedisTemplate.opsForValue().set(USERINFO_FREESPACE_KEY + user.getId(), userInfo.getFreeSize().toString());
        return Result.success(token);
    }

    @Override
    public Result<Void> update(UserUpdateDTO requestparm, String token) {
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
        return Result.successMsg("修改成功");
    }

    @Override
    public Result<Void> logout(String token) {
        stringRedisTemplate.delete(LOGIN_USER_KEY + token);
        stringRedisTemplate.delete(USERINFO_FREESPACE_KEY + UserHolder.getUser().getId());
        return Result.successMsg("成功退出登录");
    }

    @Override
    public Result<UserInfoVO> getUserInfo() {
        UserInfoVO userInfoVO = BeanUtil.copyProperties(UserHolder.getUser(), UserInfoVO.class);
        UserInfo userInfo = userInfoService.getById(userInfoVO.getId());
        userInfoVO.setTotalSize(userInfo.getTotalSize());
        userInfoVO.setFreeSize(userInfo.getFreeSize());
        return Result.success(userInfoVO);
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
