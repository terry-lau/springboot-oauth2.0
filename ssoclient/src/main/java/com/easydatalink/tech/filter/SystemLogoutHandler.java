package com.easydatalink.tech.filter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class SystemLogoutHandler implements LogoutHandler {

    private static final Logger log = LoggerFactory.getLogger(SystemLogoutHandler.class);
    @Value("${security.oauth2.client.client-id}")
    private String serviceUrl;
    @Value("${security.oauth2.client.client-secret}")
    private String clientId;
    @Value("${sso.logout.url}")
    private String logoutUrl;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        OAuth2AuthenticationDetails detail = (OAuth2AuthenticationDetails)authentication.getDetails();
        Map<String, String> params = new HashMap<String, String>();
        params.put("clientId", clientId);
        params.put("redirect_uri", serviceUrl);
        params.put("token", detail.getTokenValue());
        String str = HttpUtil.doPost(logoutUrl, params, "UTF-8");
        addTokenInCookie(null, request, response);
        log.info(str);
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

}
