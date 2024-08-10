package org.chad.cloudvault.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.chad.cloudvault.domain.dto.InitTaskParamDTO;
import org.chad.cloudvault.domain.po.UploadTask;
import org.chad.cloudvault.domain.vo.TaskInfoVO;

import java.util.Map;

public interface UploadTaskService extends IService<UploadTask> {

    /**
     * 根据md5标识获取分片上传任务
     * @param identifier
     * @return
     */
    UploadTask getByIdentifier (String identifier);

    /**
     * 初始化一个任务
     */
    TaskInfoVO initTask (InitTaskParamDTO param);

    /**
     * 获取文件地址
     * @param bucket
     * @param objectKey
     * @return
     */
    String getPath (String bucket, String objectKey);

    /**
     * 获取上传进度
     * @param identifier
     * @return
     */
    TaskInfoVO getTaskInfo (String identifier);

    /**
     * 生成预签名上传url
     * @param bucket 桶名
     * @param objectKey 对象的key
     * @param params 额外的参数
     * @return
     */
    String genPreSignUploadUrl (String bucket, String objectKey, Map<String, String> params);

    /**
     * 合并分片
     * @param identifier
     */
    void merge (String identifier);
}