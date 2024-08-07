package org.chad.cloudvault.domain.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class FileUploadDTO {
    private Long fileId;//文件ID
    private MultipartFile file;//文件
    private String fileName;//文件名
    private Long filePid;//文件的父ID
    private String fileMD5;//文件的MD5值
    private Integer chunkIndex;//分片idx
    private Integer chunks;//总分片数
}
