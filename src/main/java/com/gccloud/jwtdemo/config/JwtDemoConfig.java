package com.gccloud.jwtdemo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author hongyang
 * @version 1.0
 * @date 2022/1/15 14:10
 */
@Configuration
@ConfigurationProperties(prefix = "uc.jwt")
@Data
public class JwtDemoConfig {

    /**
     * 认证中心地址
     */
    private String acUrlPrefix;

    /**
     * 此demo的服务地址
     */
    private String demoServerUrl;

    /**
     * 该应用在用户中心注册的appKey
     */
    private String appKey;

    /**
     * 该应用在用户中心注册的appSecret
     */
    private String appSecret;

    /**
     * 用户中心的服务地址
     */
    private String ucPrefixUrl;

}
