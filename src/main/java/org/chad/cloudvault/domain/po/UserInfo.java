package org.chad.cloudvault.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.chad.cloudvault.common.database.BaseDO;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@TableName("t_user_info")
public class UserInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键,用户ID
     */
    @TableId(value = "user_id")
    private Long userId;

    /**
     * 全部空间
     */
    private Long totalSize;

    /**
     * 剩余空间
     */
    private Long freeSize;
}
