package org.chad.cloudvault.domain.po;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chad.cloudvault.common.database.BaseDO;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_file_share")
public class FileShare extends BaseDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId
    private Long shareId; // 分享ID

    private Long fileId; // 文件ID

    private Long userId; // 用户ID

    private Boolean validType;//有效期类型

    private String code; //提取码

    private LocalDateTime expireTime;

    private Integer showCount;
}