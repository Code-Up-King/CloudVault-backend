package org.chad.cloudvault.config;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class AmazonS3Config {

    @Resource
    private MinioConfig minioConfig;

    @Bean(name = "amazonS3Client")
    public AmazonS3 amazonS3Client () {
        //设置连接时的参数
        ClientConfiguration config = new ClientConfiguration();
        //设置连接方式为HTTP，可选参数为HTTP和HTTPS
        config.setProtocol(Protocol.HTTP);
        //设置网络访问超时时间
        config.setConnectionTimeout(5000);
        config.setUseExpectContinue(true);
        AWSCredentials credentials = new BasicAWSCredentials(minioConfig.getAccessKey(), minioConfig.getSecretKey());
        //设置Endpoint
        AwsClientBuilder.EndpointConfiguration end_point = new AwsClientBuilder.EndpointConfiguration(minioConfig.getEndpoint(), Regions.US_EAST_1.name());
        return AmazonS3ClientBuilder.standard()
                .withClientConfiguration(config)
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withEndpointConfiguration(end_point)
                .withPathStyleAccessEnabled(true).build();
    }

}
