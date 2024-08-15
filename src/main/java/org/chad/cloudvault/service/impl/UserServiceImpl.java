package org.chad.cloudvault.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.chad.cloudvault.common.user.UserDTO;
import org.chad.cloudvault.common.user.UserHolder;
import org.chad.cloudvault.config.MinioConfig;
import org.chad.cloudvault.domain.dto.UserChangePdDTO;
import org.chad.cloudvault.domain.dto.UserLoginDTO;
import org.chad.cloudvault.domain.dto.UserRegisterDTO;
import org.chad.cloudvault.domain.dto.UserUpdateDTO;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.po.User;
import org.chad.cloudvault.domain.po.UserInfo;
import org.chad.cloudvault.domain.vo.HeadImgUploadVO;
import org.chad.cloudvault.domain.vo.UserInfoVO;
import org.chad.cloudvault.domain.vo.UserLoginVO;
import org.chad.cloudvault.domain.vo.UserSpaceVO;
import org.chad.cloudvault.mapper.UserMapper;
import org.chad.cloudvault.service.UserInfoService;
import org.chad.cloudvault.service.UserService;
import org.chad.cloudvault.utils.MinioUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.chad.cloudvault.common.constant.RedisConstants.LOGIN_USER_KEY;
import static org.chad.cloudvault.common.constant.RedisConstants.USERINFO_FREESPACE_KEY;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final StringRedisTemplate stringRedisTemplate;

    private final UserInfoService userInfoService;

    private final MinioUtil minioUtil;

    private final MinioConfig minioConfig;
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
        minioUtil.createDir(minioConfig.getBucketName(), "pan/" + user.getId().toString() + "/");
        return Result.success("注册成功");
    }

    @Override
    public Result<UserLoginVO> login(UserLoginDTO userLoginDTO) {
        User user = getUserByUsername(userLoginDTO.getUsername());
        if(BeanUtil.isEmpty(user)){
            return Result.error(String.format("用户%s不存在", userLoginDTO.getUsername()));
        }
        if(!BCrypt.checkpw(userLoginDTO.getPassword(), user.getPassword())){
            return Result.error("密码错误");
        }
        String url = minioUtil.getPresignedObjectUrl(minioConfig.getBucketName(), user.getIcon());
        user.setIcon(url);
        String token = UUID.randomUUID().toString();
        //将User对象转为HashMap存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        getUserMap(token, userDTO);
        UserInfo userInfo = userInfoService.getById(user.getId());
        stringRedisTemplate.opsForValue().set(USERINFO_FREESPACE_KEY + user.getId(), userInfo.getFreeSize().toString());
        return Result.success(new UserLoginVO(token), "登录成功");
    }

    @Override
    public Result<Void> update(UserUpdateDTO requestparm, String token) {
        //http://116.198.242.154:9090/cloudvault/headImg/1
        // ?X-Amz-Algorithm=AWS4-HMAC-SHA256
        // &X-Amz-Credential=minio%2F20240814
        // %2Fus-east-1%2Fs3
        // %2Faws4_request&X-Amz-Date=20240814T064804Z
        // &X-Amz-Expires=604800&X-Amz-SignedHeaders=host
        // &X-Amz-Signature=95789091bd9ad6ad8b89c5cba8257da829b14005f44110e3469d68530d14f108
        LambdaUpdateWrapper<User> updateWrapper = Wrappers.lambdaUpdate(User.class)
                .set(requestparm.getUsername()!=null, User::getUsername, requestparm.getUsername())
                .set(requestparm.getHeadImg()!=null, User::getIcon, urlExtractor(requestparm.getHeadImg()))
                .eq(User::getId, UserHolder.getUser().getId())
                .eq(User::getDelFlag, 0);
        update(updateWrapper);
        UserDTO user = UserHolder.getUser();
        if(requestparm.getUsername()!=null){
            user.setUsername(requestparm.getUsername());
        }
        if(requestparm.getHeadImg()!=null){
            minioUtil.removeFile(minioConfig.getBucketName(), urlExtractor(requestparm.getHeadImg()));
            user.setIcon(requestparm.getHeadImg());
        }
        getUserMap(token, user);
        return Result.success("修改成功");
    }

    @Override
    public Result<Void> logout(String token) {
        stringRedisTemplate.delete(LOGIN_USER_KEY + token);
        stringRedisTemplate.delete(USERINFO_FREESPACE_KEY + UserHolder.getUser().getId());
        return Result.success("成功退出登录");
    }

    @Override
    public Result<UserInfoVO> getUserInfo() {
        UserInfoVO userInfoVO = BeanUtil.copyProperties(UserHolder.getUser(), UserInfoVO.class);
        UserInfo userInfo = userInfoService.getById(userInfoVO.getId());
        userInfoVO.setTotalSize(userInfo.getTotalSize());
        userInfoVO.setFreeSize(userInfo.getFreeSize());
        return Result.success(userInfoVO, "成功获取");
    }

    @Override
    public Result<HeadImgUploadVO> upload(MultipartFile file) {
        if(file.isEmpty()){
            return Result.error("不能上传空文件");
        }
        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf('.'));
        String imgName = "headImg/" + UserHolder.getUser().getId().toString() + suffix;
        minioUtil.uploadFile(minioConfig.getBucketName(), file, imgName, file.getContentType());
        String url = minioUtil.getPresignedObjectUrl(minioConfig.getBucketName(), imgName);
        return Result.success(new HeadImgUploadVO(url));
    }

    @Override
    public Result<Void> changePd(UserChangePdDTO requestparm) {
        User user = getById(UserHolder.getUser().getId());
        if(!BCrypt.checkpw(requestparm.getOldPassword(), user.getPassword())){
            return Result.error("旧密码错误");
        }
        if(!requestparm.getNewPassword().equals(requestparm.getConfirmPassword())){
            return Result.error("两次密码不一致");
        }
        String password = BCrypt.hashpw(requestparm.getNewPassword(), BCrypt.gensalt());
        user.setPassword(password);
        updateById(user);
        return Result.success("修改成功");
    }

    @Override
    public Result<UserSpaceVO> getUseSpace() {
        String s = stringRedisTemplate.opsForValue().get(USERINFO_FREESPACE_KEY + UserHolder.getUser().getId());
        if(StrUtil.isEmpty(s)){
            return Result.error("用户的登录状态异常");
        }
        return Result.success(new UserSpaceVO(initSize, Long.parseLong(s)));
    }

    private void getUserMap(String token, UserDTO user) {
        Map<String, Object> userMap = BeanUtil.beanToMap(user, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        String tokenKey = LOGIN_USER_KEY + token;
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        //TODO:实际上线的时候需要把对token限时加上
//        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);
    }

    private User getUserByUsername(String username){
        LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getUsername, username);
        return getOne(queryWrapper);
    }

    private String urlExtractor(String url){
        Pattern pattern = Pattern.compile("/cloudvault/(headImg/[^?]+)");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return url.substring(0, url.indexOf('?'));
        }
    }
}
