package org.chad.cloudvault.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSpaceVO {
    /**
     * 全部空间
     */
    private Long totalSize;

    /**
     * 剩余空间
     */
    private Long freeSize;
}
