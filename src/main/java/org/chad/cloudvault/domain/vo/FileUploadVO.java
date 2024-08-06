package org.chad.cloudvault.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadVO {
    private Long fileId;

    //0:上传失败 1:文件全部上传成功 2:该片文件上传成功，继续上传后续分片
    private Integer status;
}
