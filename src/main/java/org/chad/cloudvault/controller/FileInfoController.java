package org.chad.cloudvault.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.chad.cloudvault.domain.dto.FileUploadDTO;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.vo.FileExistVO;
import org.chad.cloudvault.domain.vo.FileInfoPageVO;
import org.chad.cloudvault.domain.vo.FileUploadVO;
import org.chad.cloudvault.domain.vo.TaskInfoVO;
import org.chad.cloudvault.service.FileInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@CrossOrigin
public class FileInfoController {

    private final FileInfoService fileInfoService;

    @GetMapping("/list/{page}/{filePid}")
    public Result<IPage<FileInfoPageVO>> fileList(@PathVariable Integer page, @PathVariable Long filePid){
        return fileInfoService.fileList(page, filePid);
    }

    @PostMapping("/upload")
    public Result<FileUploadVO> upload(FileUploadDTO requestParm){
        return fileInfoService.upload(requestParm);
    }

    @GetMapping("/{identifier}")
    public Result<FileExistVO> checkFileExist (@PathVariable String identifier) {
        return fileInfoService.checkFileExist(identifier);
    }

    @GetMapping("/checkSpace/{useSpace}")
    public Result<Void> checkUserSpace(@PathVariable Long useSpace){
        return fileInfoService.checkUserSpace(useSpace);
    }

    @GetMapping("/getUrl/{fileId}")
    public Result<Void> getUrl(@PathVariable Long fileId){
        return fileInfoService.getUrl(fileId);
    }

    @DeleteMapping("/delete/")
    public Result<Void> deleteByFileIds(@RequestBody List<Long> ids){
        return fileInfoService.deleteByFileIds(ids);
    }
}
