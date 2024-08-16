package org.chad.cloudvault.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileSharePageVO {

    private Long shareId; // 分享ID

    private Long fileId; // 文件ID

    private Boolean validType;//有效期类型

    private String code; //提取码

    private LocalDateTime createTime;//创建时间

    private LocalDateTime expireTime;//过期时间

    private Integer showCount;

    private Long fileSize; // 文件大小

    private String fileName; // 文件名称

    private String fileCover; // 封面

    private Byte folderType; // 0:文件 1:目录

    private Byte fileCategory; // 1:视频 2:音频 3:图片 4:文档 5:其他

    private Byte fileType; // 1:视频 2:音频 3:图片 4:pdf 5:doc 6:excel 7:txt 8:code 9:zip 10:其他
}
