package org.chad.cloudvault.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.chad.cloudvault.domain.dto.FileUploadDTO;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.po.FileInfo;
import org.chad.cloudvault.domain.vo.*;
import org.chad.cloudvault.service.FileInfoService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
@CrossOrigin
public class FileInfoController {

    private final FileInfoService fileInfoService;

    @GetMapping("/list/{pageNo}/{filePid}")
    public Result<IPage<FileInfoPageVO>> fileList(@PathVariable Integer pageNo, @PathVariable Long filePid, @RequestParam Integer category, @RequestParam Integer pageSize){
        return fileInfoService.fileListByCategory(pageNo, filePid, category, pageSize);
    }

    @PostMapping("/upload")
    public Result<FileUploadVO> upload(FileUploadDTO requestParm){
        return fileInfoService.upload(requestParm);
    }

    @PostMapping("/download")
    public Result<Void> download(@RequestBody List<Long> ids, HttpServletResponse response){
        return fileInfoService.download(ids, response);
    }

    @GetMapping("/{identifier}/{fileName}")
    public Result<FileExistVO> checkFileExist (@PathVariable String identifier, @PathVariable String fileName) {
        return fileInfoService.checkFileExist(identifier, fileName);
    }

    @GetMapping("/checkSpace/{useSpace}")
    public Result<Void> checkUserSpace(@PathVariable Long useSpace){
        return fileInfoService.checkUserSpace(useSpace);
    }

    @GetMapping("/getUrl")
    public Result<FileUrlVO> getUrl(@RequestBody List<Long> ids){
        return fileInfoService.getUrl(ids);
    }

    @PutMapping("/update/{fileId}")
    public Result<Void> updateByFileId(@PathVariable Long fileId, @RequestParam String name){
        return fileInfoService.updateByFileId(fileId, name);
    }

    @PutMapping("/mkdir")
    public Result<Void> createDir(@RequestParam String name, @RequestParam Long fileId){
        return fileInfoService.createDir(name, fileId);
    }

    @GetMapping("/queryByFilename")
    public Result<List<FileInfoPageVO>> queryByFilename(@RequestParam String match){
        return fileInfoService.queryByFilename(match);
    }

    @PutMapping("/move/{fileId}")
    public Result<Void> moveFiles(@RequestBody List<Long> ids, @PathVariable Long fileId){
        return fileInfoService.moveFiles(ids, fileId);
    }

    @GetMapping("/getAllDir/{fileId}")
    public Result<List<FileInfoPageVO>> getAllDir(@PathVariable Long fileId){
        return fileInfoService.getAllDir(fileId);
    }
}
