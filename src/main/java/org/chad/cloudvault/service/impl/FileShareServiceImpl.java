package org.chad.cloudvault.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.chad.cloudvault.common.user.UserHolder;
import org.chad.cloudvault.domain.dto.ShareCreateDTO;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.po.FileInfo;
import org.chad.cloudvault.domain.po.FileShare;
import org.chad.cloudvault.domain.po.User;
import org.chad.cloudvault.domain.vo.FileInfoPageVO;
import org.chad.cloudvault.domain.vo.FileSharePageVO;
import org.chad.cloudvault.domain.vo.ShareCreateVO;
import org.chad.cloudvault.domain.vo.ShareInfoVO;
import org.chad.cloudvault.mapper.FileShareMapper;
import org.chad.cloudvault.service.FileInfoService;
import org.chad.cloudvault.service.FileShareService;
import org.chad.cloudvault.service.UserService;
import org.chad.cloudvault.utils.RandomStringGenerator;
import org.chad.cloudvault.utils.RedisIdWorker;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

import static org.chad.cloudvault.common.constant.RedisConstants.SHARE_ADD_KEY;
import static org.chad.cloudvault.common.constant.RedisConstants.SHARE_HAS_KEY;
import static org.chad.cloudvault.common.constant.ShareConstant.SHARE_LINK_PREFIX;

@Service
@RequiredArgsConstructor
public class FileShareServiceImpl extends ServiceImpl<FileShareMapper, FileShare> implements FileShareService {

    private final StringRedisTemplate stringRedisTemplate;

    private final RedisIdWorker redisIdWorker;

    private final FileInfoService fileInfoService;

    private final UserService userService;
    @Override
    public Result<IPage<FileSharePageVO>> list(Integer pageNo, Integer pageSize) {
        LambdaQueryWrapper<FileShare> queryWrapper = Wrappers.lambdaQuery(FileShare.class)
                .eq(FileShare::getUserId, UserHolder.getUser().getId())
                .eq(FileShare::getDelFlag, 0)
                .and(wrapper->
                    wrapper.ge(FileShare::getExpireTime, LocalDateTime.now())
                            .or()
                            .eq(FileShare::getValidType, true)
                )
                .orderByDesc(FileShare::getCreateTime);
        Page<FileShare> resultPage = page(new Page<>(pageNo, pageSize), queryWrapper);
        IPage<FileSharePageVO> convert = resultPage.convert(each -> {
            FileInfo fileInfo = fileInfoService.getById(each.getFileId());
            FileSharePageVO fileSharePageVO = BeanUtil.copyProperties(fileInfo, FileSharePageVO.class);
            fileSharePageVO.setShareId(each.getShareId());
            fileSharePageVO.setCreateTime(each.getCreateTime());
            fileSharePageVO.setExpireTime(each.getExpireTime());
            fileSharePageVO.setShowCount(each.getShowCount());
            fileSharePageVO.setValidType(each.getValidType());
            fileSharePageVO.setCode(each.getCode());
            return fileSharePageVO;
        });
        return Result.success(convert);
    }

    @Override
    public Result<ShareCreateVO> add(ShareCreateDTO requestparm) {
        FileShare fileShare = new FileShare();
        ShareCreateVO shareCreateVO = new ShareCreateVO();
        fileShare.setFileId(requestparm.getFileId());
        fileShare.setUserId(UserHolder.getUser().getId());
        fileShare.setShareId(redisIdWorker.nextID(SHARE_ADD_KEY));
        if(requestparm.getExpireTime() == -1){
            fileShare.setValidType(true);
        }else{
            fileShare.setValidType(false);
            fileShare.setExpireTime(LocalDateTime.now().plusDays(requestparm.getExpireTime()));
        }
        if(StrUtil.isBlank(requestparm.getCode())){
            fileShare.setCode(RandomStringGenerator.generateRandomString(6));
        }else{
            fileShare.setCode(requestparm.getCode());
        }
        shareCreateVO.setCode(fileShare.getCode());
        shareCreateVO.setShareLink(SHARE_LINK_PREFIX + fileShare.getShareId());
        stringRedisTemplate.opsForZSet().add(SHARE_HAS_KEY + fileShare.getShareId(), UserHolder.getUser().getId().toString(), System.currentTimeMillis());
        save(fileShare);
        return Result.success(shareCreateVO, "分享成功");
    }

    @Override
    public Result<Void> cancel(Long shareId) {
        LambdaUpdateWrapper<FileShare> updateWrapper = Wrappers.lambdaUpdate(FileShare.class)
                .eq(FileShare::getShareId, shareId)
                .eq(FileShare::getUserId, UserHolder.getUser().getId())
                .set(FileShare::getDelFlag, 1);
        update(updateWrapper);
        stringRedisTemplate.delete(SHARE_HAS_KEY + shareId);
        return Result.success("取消成功");
    }

    @Override
    public Result<ShareInfoVO> getShareInfo(Long shareId) {
//        Double score = stringRedisTemplate.opsForZSet().score(SHARE_HAS_KEY + shareId, UserHolder.getUser().getId().toString());
//        if(BeanUtil.isEmpty(score)){
//            return Result.error("请重新输入提取码");
//        }
        FileShare share = getById(shareId);
        if(!share.getValidType() && share.getExpireTime().isBefore(LocalDateTime.now())){
            //已经过期了
            stringRedisTemplate.delete(SHARE_HAS_KEY + shareId);
            return Result.error("分享已经过期了");
        }
        ShareInfoVO shareInfoVO = BeanUtil.copyProperties(share, ShareInfoVO.class);
        if(BeanUtil.isNotEmpty(UserHolder.getUser())){
            shareInfoVO.setCurrentUser(Objects.equals(UserHolder.getUser().getId(), share.getUserId()));
        }
        User shareUser = userService.getById(share.getUserId());
        shareInfoVO.setUsername(shareUser.getUsername());
        shareInfoVO.setHeadImg(shareUser.getIcon());
        FileInfo shareFile = fileInfoService.getById(share.getFileId());
        shareInfoVO.setFilename(shareFile.getFileName());
        return Result.success(shareInfoVO, "success");
    }

    @Override
    public Result<Void> checkCode(Long shareId, String code) {
        FileShare share = getById(shareId);
        if(!share.getCode().equals(code)){
            return Result.error("提取码错误");
        }
        if(BeanUtil.isNotEmpty(UserHolder.getUser())){
            stringRedisTemplate.opsForZSet().add(SHARE_HAS_KEY + shareId, UserHolder.getUser().getId().toString(), System.currentTimeMillis());
        }
        return Result.success("提取码正确");
    }

    @Override
    public Result<IPage<FileInfoPageVO>> shareList(Integer pageNo, Long fileId, Integer pageSize, Long shareId) {
        Double score = stringRedisTemplate.opsForZSet().score(SHARE_HAS_KEY + shareId, UserHolder.getUser().getId().toString());
        if(BeanUtil.isEmpty(score)){
            return Result.error("请重新输入提取码");
        }
        FileShare fileShare = getById(shareId);
        if(fileId == 0){
            LambdaQueryWrapper<FileInfo> queryWrapper = Wrappers.lambdaQuery(FileInfo.class)
                    .eq(FileInfo::getFileId, fileId)
                    .eq(FileInfo::getUserId, fileShare.getUserId())
                    .eq(FileInfo::getDelFlag, 0)
                    .orderByDesc(FileInfo::getUpdateTime);;
            Page<FileInfo> page = fileInfoService.page(new Page<>(pageNo, pageSize), queryWrapper);
            IPage<FileInfoPageVO> convert = page.convert(each -> BeanUtil.copyProperties(each, FileInfoPageVO.class));
            return Result.success(convert);
        }
        LambdaQueryWrapper<FileInfo> queryWrapper = Wrappers.lambdaQuery(FileInfo.class)
                .eq(FileInfo::getFilePid, fileId)
                .eq(FileInfo::getUserId, fileShare.getUserId())
                .eq(FileInfo::getDelFlag, 0)
                .orderByDesc(FileInfo::getUpdateTime);;
        Page<FileInfo> page = fileInfoService.page(new Page<>(pageNo, pageSize), queryWrapper);
        IPage<FileInfoPageVO> convert = page.convert(each -> BeanUtil.copyProperties(each, FileInfoPageVO.class));
        return Result.success(convert);
    }
}
