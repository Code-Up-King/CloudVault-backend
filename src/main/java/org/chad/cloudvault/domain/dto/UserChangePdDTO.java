package org.chad.cloudvault.domain.dto;

import lombok.Data;

@Data
public class UserChangePdDTO {
    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 确认密码
     */
    private String confirmPassword;
}
