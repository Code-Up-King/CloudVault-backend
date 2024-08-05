package org.chad.cloudvault.domain.dto;

import lombok.Data;

@Data
public class UserUpdateDTO {
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;
}
