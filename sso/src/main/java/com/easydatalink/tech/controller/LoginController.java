package com.easydatalink.tech.controller;

import java.io.IOException;
import java.util.Collection;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.easydatalink.tech.config.oauth.AuthorizationServerConfig;
import com.easydatalink.tech.utils.captcha.CaptchaHelper;

import lombok.extern.slf4j.Slf4j;

/**
 * SSO 登录，退出
 * 
 * @author Terry
 *
 */
@Slf4j
@Controller
public class LoginController {

    @Autowired
    private ConsumerTokenServices consumerTokenServices;
    @Autowired
    private AuthorizationServerConfig authorizationServerConfig;
    @Value("${sso.local.url}")
    private String defaultUrl;
    private RequestCache requestCache = new HttpSessionRequestCache();

    @GetMapping("/login")
    public String login(HttpServletRequest request, HttpServletResponse response) {
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        String targetUrl = null;
        if (null != savedRequest) {
            targetUrl = savedRequest.getRedirectUrl();
            log.debug(targetUrl);
        }
        return "login";
    }

    @GetMapping("/captcha")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setDateHeader("Expires", 0L);
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        response.setContentType("image/jpeg");
        CaptchaHelper.setInCache(request, response);
    }

    /**
     * token can be revoked here if needed
     * 
     * @param request
     * @param response
     * @param authentication
     */
    @RequestMapping("/exit")
    public void exit(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        new SecurityContextLogoutHandler().logout(request, null, null);
        try {
            String clientId = request.getParameter("clientId");
            String redirect_uri = request.getParameter("redirect_uri");
            String token = request.getParameter("token");
            if (authentication != null) {
                Collection<OAuth2AccessToken> cO = authorizationServerConfig.redisTokenStore()
                    .findTokensByClientIdAndUserName(clientId, authentication.getName());
                for (OAuth2AccessToken oAuth2AccessToken : cO) {
                    consumerTokenServices.revokeToken(oAuth2AccessToken.getValue());
                }
            }
            if (null != token && !"".equals(token)) {
                consumerTokenServices.revokeToken(token);
            } else {
                if (request.getHeader("referer") != null) {
                    response.sendRedirect(request.getHeader("referer"));
                } else {
                    if (null != redirect_uri) {
                        response.sendRedirect(redirect_uri);
                    } else {
                        response.sendRedirect(defaultUrl);
                    }
                }
            }
            // addTokenInCookie(null, request, response);
        } catch (IOException e) {
            e.printStackTrace();
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

    @GetMapping("/")
    public String index(HttpServletRequest request, HttpServletResponse response, Authentication authentication,
        Model model) {
        model.addAttribute("userName", authentication.getPrincipal());
        return "index";
    }

    @GetMapping("/403")
    public String error403() {
        return "error/403";
    }

    @GetMapping("/404")
    public String error404() {
        return "error/404";
    }

    @GetMapping("/500")
    public String error500() {
        return "error/500";
    }

}
