package com.gccloud.jwtdemo.controller.views;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.gccloud.jwtdemo.Utils.XmlUtils;
import com.gccloud.jwtdemo.config.JwtDemoConfig;
import com.gccloud.jwtdemo.services.UserCenterService;
import com.gccloud.uc.sdk.SignUtils;
import com.github.benmanes.caffeine.cache.Cache;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

/**
 * @author hongyang
 * @version 1.0
 * @date 2021/8/23 17:32
 */
@Slf4j
@Controller
@RequestMapping
public class ViewsController {

    @Autowired
    Cache<String, String> caffeineCache;
    @Resource
    private JwtDemoConfig config;
    @Resource
    private UserCenterService userCenterService;

    /**
     * 主页面
     * @return
     */
    @GetMapping
    public String homePage(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        String token = request.getParameter("token");
        if (StringUtils.isBlank(token)) {
            response.sendRedirect(config.getAcUrlPrefix() + "login?service=" + config.getDemoServerUrl());
            return "home";
        }
        Claims claims = Jwts.parser()
                .setSigningKey("GsT@2020")
                .parseClaimsJws(token)
                .getBody();
        String sessionIndex = claims.get("jwtId", String.class);
        String cacheToken = caffeineCache.getIfPresent(sessionIndex);
        if (StringUtils.isBlank(cacheToken) || !cacheToken.equals(token)) {
            log.error("token失效");
            response.sendRedirect(config.getAcUrlPrefix() + "login?service=" + config.getDemoServerUrl());
            return "home";
        }
        String name = claims.get("subject", String.class);;
        model.addAttribute("name", name);
        String logoutUrl = config.getAcUrlPrefix() + "logout?service=" + config.getAcUrlPrefix() + "login";
        model.addAttribute("logoutUrl", logoutUrl);
        log.info("访问首页");
        Map<String, String> header = new HashMap<>();
        header.put(SignUtils.U_NAME, name);
        JSONObject userInfo = userCenterService.get("/sys/user/current", header, JSONObject.class);
        log.info("用户信息:{}", userInfo);
        String info = JSON.toJSONString(userInfo.getJSONObject("data"), SerializerFeature.PrettyFormat, SerializerFeature.WriteMapNullValue,
                SerializerFeature.WriteDateUseDateFormat);
        model.addAttribute("userInfo", info);
        return "index";
    }


    @GetMapping(value = "/login")
    public void login(HttpServletRequest request, HttpServletResponse response, Model model) throws IOException {
        String ticket = request.getParameter("ticket");
        if (StringUtils.isNotBlank(ticket)) {
            Claims claims = Jwts.parser()
                    .setSigningKey("GsT@2020")
                    .parseClaimsJws(ticket)
                    .getBody();
            String username = claims.get("subject", String.class);
            String sessionIndex = claims.get("jwtId", String.class);
            caffeineCache.put(sessionIndex, ticket);
            int index = request.getRequestURL().toString().indexOf("/login");
            String urlPrefix = request.getRequestURL().toString().substring(0, index);
            String redirectUrl = urlPrefix + "?token=" + ticket ;
            response.sendRedirect(redirectUrl);
        }

    }

    @PostMapping("/callBack")
    public void postCallBack(HttpServletRequest request, HttpServletResponse response){
        String logoutRequest = request.getParameter("logoutRequest");
        String sessionIndex = XmlUtils.getTextForElement(logoutRequest, "SessionIndex");
        caffeineCache.invalidate(sessionIndex);
    }

}
