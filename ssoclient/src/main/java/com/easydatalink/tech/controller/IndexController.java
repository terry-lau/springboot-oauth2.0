package com.easydatalink.tech.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.easydatalink.tech.commons.SaasConstant;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试
 * 
 * @author Terry
 * @date 2021/05/07
 */
@Slf4j
@Controller
public class IndexController {
    @Value("${host.front.url}")
    private String front_url;

    @RequestMapping("/")
    public String index(HttpServletRequest request, HttpServletResponse response, Authentication authentication,
        Model model) {
        OAuth2AuthenticationDetails detail = (OAuth2AuthenticationDetails)authentication.getDetails();
        log.info("【登录成功】username:{}, sessonId:{}, {}", authentication.getPrincipal(), detail.getSessionId(),
            detail.getTokenValue());
        String token = detail.getTokenValue();
        response.setHeader("Authorization", token);
        if (StringUtils.isNotBlank(front_url)) {
            return "redirect:" + front_url + SaasConstant.PRE_FIX + token;
        } else {
            model.addAttribute("token", detail.getTokenValue());
            addTokenInCookie(token, request, response);
            return "index";
        }
    }

    // Cookie添加token
    private void addTokenInCookie(String token, HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("cas_access_token", token);
        cookie.setPath("/");
        if ("https".equals(request.getScheme())) {
            cookie.setSecure(true);
        }
        cookie.setHttpOnly(true);
        if (null == token) {
            cookie.setMaxAge(0);
        }
        response.addCookie(cookie);
    }

    @ResponseBody
    @RequestMapping("/test")
    public void test(HttpServletRequest request) {
        System.out.println(request.getUserPrincipal().getName());
    }
}
