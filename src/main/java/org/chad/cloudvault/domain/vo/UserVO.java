package org.chad.cloudvault.domain.vo;

import lombok.Data;

@Data
public class UserVO {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户头像
     */
    private String icon = "";
}
