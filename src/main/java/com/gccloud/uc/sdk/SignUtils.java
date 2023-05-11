package com.gccloud.uc.sdk;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author hujunzheng
 * @create 2018-12-25 19:58
 **/
public class SignUtils {
    /**
     * 标识是否使用数字签名进行交互
     * 可选值：SIGN
     */
    public static final String U_SIGN = "u-sign";
    /**
     * appKey键,标识唯一客户端
     */
    public static final String U_APP_KEY = "u-appKey";
    /**
     * 随机数，防止重复提交
     */
    public static final String U_ONCE = "u-once";
    /**
     * 时间戳，设置有效期
     */
    public static final String U_TIMESTAMP = "u-timestamp";
    /**
     * 用户名，标识哪一个用户
     */
    public static final String U_NAME = "u-uname";
    /**
     * 签名
     */
    public static final String U_SIGNATURE = "u-signature";

    /**
     * 签名有效期
     */
    public static final long TIME_OUT = 30 * 60 * 1000;

    public static final String DEFAULT_SIGN = "sign";

    /**
     * 设置once有效期，避免重放攻击
     */
    private static final Cache<String, String> ONCE_KEY_CACHE = Caffeine.newBuilder().expireAfterWrite(TIME_OUT, TimeUnit.MILLISECONDS).build();

    /**
     * 签名
     *
     * @param header 请求头
     * @param secret appSecret
     * @return
     */
    public static String sign(Map<String, String> header, String secret) {
        String appKey = header.get(U_APP_KEY);
        Assert.isTrue(StringUtils.isNotBlank(appKey), U_APP_KEY + "非法");
        String uSign = header.get(U_SIGN);
        Assert.isTrue(StringUtils.isNotBlank(uSign), U_SIGN + "非法");
        String once = header.get(U_ONCE);
        Assert.isTrue(StringUtils.isNotBlank(once), U_ONCE + "非法");
        String timestamp = header.get(U_TIMESTAMP);
        Assert.isTrue(StringUtils.isNotBlank(timestamp), U_TIMESTAMP + "非法");
        String uname = header.get(U_NAME);
        Assert.isTrue(StringUtils.isNotBlank(uname), U_NAME + "非法");
        String sign = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret).hmacHex(appKey + once + timestamp + uname);
        return sign;
    }

    /**
     * 校验签名
     *
     * @param header 请求头
     * @param secret appSecret
     */
    public static void validateSign(Map<String, String> header, String secret) {
        String appKey = header.get(U_APP_KEY);
        Assert.isTrue(StringUtils.isNotBlank(appKey), U_APP_KEY + "非法");
        String timestampStr = header.get(U_TIMESTAMP);
        Assert.isTrue(StringUtils.isNotBlank(timestampStr), U_TIMESTAMP + "非法");
        // 校验时间有效期
        long timestamp = Long.parseLong(timestampStr);
        long now = System.currentTimeMillis();
        Assert.isTrue(now > timestamp, U_TIMESTAMP + "非法");
        Assert.isTrue(now - timestamp <= TIME_OUT, U_TIMESTAMP + "非法");
        // 防止重放
        String once = header.get(U_ONCE);
        Assert.isTrue(StringUtils.isNotBlank(once), U_ONCE + "非法");
        String uname = header.get(U_NAME);
        Assert.isTrue(StringUtils.isNotBlank(uname), U_NAME + "非法");
        String sign = header.get(U_SIGNATURE);
        Assert.isTrue(StringUtils.isNotBlank(sign), U_SIGNATURE + "非法");
        String signResult = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, secret).hmacHex(appKey + once + timestamp + uname);
        Assert.isTrue(sign.equals(signResult), "非法签名访问");
        // 拒绝重放攻击
        String cachedOnce = ONCE_KEY_CACHE.getIfPresent(once);
        Assert.isTrue(StringUtils.isEmpty(cachedOnce), "非法重放攻击");
        ONCE_KEY_CACHE.put(once, "1");
    }

    public static void main(String[] args) {
        String secret = "secret";
        HashMap<String, String> header = new HashMap<>();
        header.put(SignUtils.U_SIGN, "sign");
        header.put(SignUtils.U_APP_KEY, "uc");
        header.put(SignUtils.U_ONCE, UUID.randomUUID().toString().replaceAll("-", ""));
        header.put(SignUtils.U_TIMESTAMP, System.currentTimeMillis() + "");
        header.put(SignUtils.U_NAME, "admin");
        String sign = sign(header, secret);
        header.put(SignUtils.U_SIGNATURE, sign);
        System.out.println(header);
        validateSign(header, secret);
    }

}