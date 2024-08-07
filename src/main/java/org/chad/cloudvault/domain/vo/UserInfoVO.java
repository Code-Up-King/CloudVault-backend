package org.chad.cloudvault.domain.vo;

import lombok.Data;

@Data
public class UserInfoVO {
    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户姓名
     */
    private String username;

    /**
     * 用户头像
     */
    private String icon = "";

    /**
     * 全部空间
     */
    private Long totalSize;

    /**
     * 剩余空间
     */
    private Long freeSize;
}
