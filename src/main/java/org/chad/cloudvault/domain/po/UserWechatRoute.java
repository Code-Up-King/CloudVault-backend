package org.chad.cloudvault.domain.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("t_user_vx")
public class UserWechatRoute {

    private Long id;

    private Long userId;

    private String vxId;
}
