package org.chad.cloudvault.domain.dto;

import lombok.Data;

@Data
public class UserRegisterDTO {
    /**
     * 用户姓名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 确认密码
     */
    private String confirmPassword;
}
