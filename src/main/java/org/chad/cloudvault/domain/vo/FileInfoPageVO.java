package org.chad.cloudvault.domain.vo;

import cn.hutool.db.Page;
import lombok.Data;

@Data
public class FileInfoPageVO extends Page {

    private Long fileId; // 文件ID

    private String fileMd5; // md5值，第一次上传记录

    private Long filePid; // 父级ID

    private Long fileSize; // 文件大小

    private String fileName; // 文件名称

    private String fileCover; // 封面

    private String filePath; // 文件路径

    private Byte folderType; // 0:文件 1:目录

    private Byte fileCategory; // 1:视频 2:音频 3:图片 4:文档 5:其他

    private Byte fileType; // 1:视频 2:音频 3:图片 4:pdf 5:doc 6:excel 7:txt 8:code 9:zip 10:其他

    private Byte status; // 0:转码中 1转码失败 2:转码成功
}
