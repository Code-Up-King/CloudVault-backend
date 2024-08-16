package org.chad.cloudvault.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareInfoVO {
    private String username;
    private String filename;
    private Long fileId;
    private Long userId;
    private String headImg;
    private Boolean currentUser;
    private LocalDateTime createTime;
    private LocalDateTime expireTime;
}
