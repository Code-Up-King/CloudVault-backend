package org.chad.cloudvault.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.InitiateMultipartUploadRequest;
import com.amazonaws.services.s3.model.InitiateMultipartUploadResult;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.chad.cloudvault.config.MinioConfig;
import org.chad.cloudvault.domain.dto.InitTaskParamDTO;
import org.chad.cloudvault.domain.entity.TaskRecord;
import org.chad.cloudvault.domain.po.UploadTask;
import org.chad.cloudvault.domain.vo.TaskInfoVO;
import org.chad.cloudvault.mapper.UploadTaskMapper;
import org.chad.cloudvault.service.UploadTaskService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UploadTaskServiceImpl extends ServiceImpl<UploadTaskMapper, UploadTask> implements UploadTaskService {

    private final AmazonS3 amazonS3;

    private final MinioConfig minioConfig;

    @Value("${file.shard-size}")
    private Long shardSize;

    @Override
    public UploadTask getByIdentifier(String identifier) {
        return getOne(new QueryWrapper<UploadTask>().lambda().eq(UploadTask::getFileIdentifier, identifier));
    }


    @Override
    public TaskInfoVO initTask(InitTaskParamDTO param) {
        Date currentDate = new Date();
        String bucketName = minioConfig.getBucketName();
        String fileName = param.getFileName();
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
        String key = StrUtil.format("{}/{}.{}", DateUtil.format(currentDate, "YYYY-MM-dd"), IdUtil.randomUUID(), suffix);
        String contentType = MediaTypeFactory.getMediaType(key).orElse(MediaType.APPLICATION_OCTET_STREAM).toString();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(contentType);
        InitiateMultipartUploadResult initiateMultipartUploadResult = amazonS3
                .initiateMultipartUpload(new InitiateMultipartUploadRequest(bucketName, key).withObjectMetadata(objectMetadata));
        String uploadId = initiateMultipartUploadResult.getUploadId();

        UploadTask task = new UploadTask();
        task.setBucketName(bucketName)
                .setChunkNum(param.getChunks())
                .setChunkSize(shardSize)
                .setTotalSize(param.getTotalSize())
                .setFileIdentifier(param.getIdentifier())
                .setFileName(fileName)
                .setObjectKey(key)
                .setUploadId(uploadId);
        save(task);
        return new TaskInfoVO().setFinished(false).setTaskRecord(TaskRecord.convertFromEntity(task)).setPath(getPath(bucketName, key));
    }

    @Override
    public String getPath(String bucket, String objectKey) {
        return StrUtil.format("{}/{}/{}", minioConfig.getEndpoint(), bucket, objectKey);
    }

    @Override
    public TaskInfoVO getTaskInfo(String identifier) {
        return null;
    }

    @Override
    public String genPreSignUploadUrl(String bucket, String objectKey, Map<String, String> params) {
        return null;
    }

    @Override
    public void merge(String identifier) {

    }
}