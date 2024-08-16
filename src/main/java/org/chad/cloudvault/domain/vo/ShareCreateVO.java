package org.chad.cloudvault.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShareCreateVO {
    private String shareLink;//分享链接
    private String code;//提取码
}
