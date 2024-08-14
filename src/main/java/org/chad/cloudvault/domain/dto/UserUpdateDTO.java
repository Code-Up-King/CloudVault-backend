package org.chad.cloudvault.domain.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 头像地址
     */
    private String headImg;
}
