package org.chad.cloudvault.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.vo.FileInfoPageVO;
import org.chad.cloudvault.service.FileInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileInfoController {

    private final FileInfoService fileInfoService;

    @GetMapping("/list/{page}")
    public Result<IPage<FileInfoPageVO>> fileList(@PathVariable Integer page){
        return fileInfoService.fileList(page);
    }
}
