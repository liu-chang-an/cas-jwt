package com.gccloud.jwtdemo.services;

import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSON;
import com.gccloud.jwtdemo.config.JwtDemoConfig;
import com.gccloud.uc.sdk.SignUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.UUID;

/**
 * @author hy
 */
@Service
public class UserCenterService {

    @Resource
    private JwtDemoConfig config;

    public <T> T get(String url, Map<String, String> header, Class<T> resp) {
        // 通过SDK进行签名获取用户信息
        header.put(SignUtils.U_APP_KEY, config.getAppKey());
        header.put(SignUtils.U_ONCE, UUID.randomUUID().toString().replaceAll("-", ""));
        header.put(SignUtils.U_SIGN, SignUtils.DEFAULT_SIGN);
        header.put(SignUtils.U_TIMESTAMP, System.currentTimeMillis() + "");
        String signature = SignUtils.sign(header, config.getAppSecret());
        header.put(SignUtils.U_SIGNATURE, signature);
        String body = HttpRequest.get(config.getUcPrefixUrl() + url).headerMap(header, true).execute().body();
        T respObj = JSON.parseObject(body, resp);
        return respObj;
    }
}
