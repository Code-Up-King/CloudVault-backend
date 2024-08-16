package org.chad.cloudvault.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.chad.cloudvault.domain.dto.ShareCreateDTO;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.vo.FileInfoPageVO;
import org.chad.cloudvault.domain.vo.FileSharePageVO;
import org.chad.cloudvault.domain.vo.ShareCreateVO;
import org.chad.cloudvault.domain.vo.ShareInfoVO;
import org.chad.cloudvault.service.FileShareService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/share")
@RequiredArgsConstructor
@CrossOrigin
public class FileShareController {
    private final FileShareService fileShareService;

    @GetMapping("/list/{pageNo}")
    public Result<IPage<FileSharePageVO>> list(@PathVariable Integer pageNo, @RequestParam Integer pageSize){
        return fileShareService.list(pageNo, pageSize);
    }

    @PostMapping("/add")
    public Result<ShareCreateVO> add(@RequestBody ShareCreateDTO requestparm){
        return fileShareService.add(requestparm);
    }

    @DeleteMapping("/cancel")
    public Result<Void> cancel(@RequestParam Long shareId){
        return fileShareService.cancel(shareId);
    }

    @GetMapping("/{shareId}")
    public Result<ShareInfoVO> getShareInfo(@PathVariable Long shareId){
        return fileShareService.getShareInfo(shareId);
    }

    @GetMapping("/checkCode")
    public Result<Void> checkCode(@RequestParam Long shareId, @RequestParam String code){
        return fileShareService.checkCode(shareId, code);
    }

    @GetMapping("/getShareLoginInfo")
    public Result<ShareInfoVO> getShareLoginInfo(@RequestParam Long shareId){
        return fileShareService.getShareInfo(shareId);
    }

    @GetMapping("/shareList/{pageNo}/{fileId}")
    public Result<IPage<FileInfoPageVO>> shareList(@PathVariable Long fileId, @PathVariable Integer pageNo, @RequestParam Integer pageSize, @RequestParam Long shareId){
        return fileShareService.shareList(pageNo, fileId, pageSize, shareId);
    }
}
