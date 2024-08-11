package org.chad.cloudvault.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信公众平台账号实体
 */
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("wechat")
public class Account {
    /**
     * appID
     */
    private String appId;
    /**
     * app secret
     */
    private String appSecret;
    /**
     * 微信公众平台网页服务->网页帐号的授权回调页面域名
     */
    private String domain;
    /**
     * 本地处理扫码结果的controller方法访问地址
     */
    private String redirectUri;
}
