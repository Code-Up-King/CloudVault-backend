package org.chad.cloudvault.domain.po;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chad.cloudvault.common.database.BaseDO;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_file_info")
public class FileInfo extends BaseDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private Long fileId; // 文件ID

    @TableField("user_id")
    private Long userId; // 用户ID

    @TableField("file_md5")
    private String fileMd5; // md5值，第一次上传记录

    @TableField("file_pid")
    private Long filePid; // 父级ID

    @TableField("file_size")
    private Long fileSize; // 文件大小

    @TableField("file_name")
    private String fileName; // 文件名称

    @TableField("file_cover")
    private String fileCover; // 封面

    @TableField("file_path")
    private String filePath; // 文件路径

    @TableField("folder_type")
    private Byte folderType; // 0:文件 1:目录

    @TableField("file_category")
    private Byte fileCategory; // 1:视频 2:音频 3:图片 4:文档 5:其他

    @TableField("file_type")
    private Byte fileType; // 1:视频 2:音频 3:图片 4:pdf 5:doc 6:excel 7:txt 8:code 9:zip 10:其他

    @TableField("status")
    private Byte status; // 0:转码中 1转码失败 2:转码成功

    @TableField("recovery_time")
    private Date recoveryTime; // 回收站时间
}