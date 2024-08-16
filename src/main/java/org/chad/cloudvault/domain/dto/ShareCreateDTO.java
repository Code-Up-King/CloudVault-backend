package org.chad.cloudvault.domain.dto;

import lombok.Data;

@Data
public class ShareCreateDTO {
    private Integer expireTime;
    private String code;
    private Long fileId;
}
