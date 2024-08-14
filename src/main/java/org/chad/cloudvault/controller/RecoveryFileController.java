package org.chad.cloudvault.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.RequiredArgsConstructor;
import org.chad.cloudvault.domain.entity.Result;
import org.chad.cloudvault.domain.vo.FileInfoPageVO;
import org.chad.cloudvault.service.FileInfoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/recycle")
@CrossOrigin
public class RecoveryFileController {
    private final FileInfoService fileInfoService;

    @GetMapping("/list/{pageNo}/{filePid}")
    public Result<IPage<FileInfoPageVO>> recycleFileList(@PathVariable Integer pageNo, @PathVariable Long filePid){
        return fileInfoService.recycleFileList(pageNo, filePid);
    }

    @DeleteMapping("/add")
    public Result<Void> addRecycleFile(@RequestBody List<Long> ids){
        return fileInfoService.addRecycleFile(ids);
    }

    @PutMapping("/recovery")
    public Result<Void> recovery(@RequestBody List<Long> ids){
        return fileInfoService.recovery(ids);
    }

    @DeleteMapping("/delete")
    public Result<Void> deleteByFileIds(@RequestBody List<Long> ids){
        return fileInfoService.deleteByFileIds(ids);
    }
}
