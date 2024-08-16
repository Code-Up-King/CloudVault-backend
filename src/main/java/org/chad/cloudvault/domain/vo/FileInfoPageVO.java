package org.chad.cloudvault.domain.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;

@Data
public class FileInfoPageVO{

    private Long fileId; // 文件ID

    private Long fileSize; // 文件大小

    private String fileName; // 文件名称

    private String fileCover; // 封面

    private Byte folderType; // 0:文件 1:目录

    private Byte fileCategory; // 1:视频 2:音频 3:图片 4:文档 5:其他

    private Byte fileType; // 1:视频 2:音频 3:图片 4:pdf 5:doc 6:excel 7:txt 8:code 9:zip 10:其他

    private LocalDateTime updateTime;
}
