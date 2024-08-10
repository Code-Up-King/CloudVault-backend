package org.chad.cloudvault.domain.entity;

import cn.hutool.core.bean.BeanUtil;
import com.amazonaws.services.s3.model.PartSummary;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.chad.cloudvault.domain.po.UploadTask;

import java.util.List;

@Data
@ToString
@Accessors(chain = true)
public class TaskRecord extends UploadTask {

    /**
     * 已上传完的分片
     */
    private List<PartSummary> exitPartList;

    public static TaskRecord convertFromEntity (UploadTask task) {
        TaskRecord dto = new TaskRecord();
        BeanUtil.copyProperties(task, dto);
        return dto;
    }
}