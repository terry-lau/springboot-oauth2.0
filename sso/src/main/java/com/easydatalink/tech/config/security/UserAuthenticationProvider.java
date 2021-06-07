package com.easydatalink.tech.config.security;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.easydatalink.tech.dao.auth.ICoreUserDao;
import com.easydatalink.tech.entity.auth.CoreUser;
import com.easydatalink.tech.utils.captcha.CaptchaHelper;

import lombok.extern.log4j.Log4j;

/**
 * 登录用户验证处理
 * 
 * @author Terry
 *
 */
@Log4j
@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private ICoreUserDao coreUserDao;

    private static final String SPLIT_STR = "&&&";

    @SuppressWarnings("unchecked")
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        HttpServletRequest request =
            ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        // 获取输入的用户名
        String username = authentication.getName();
        // 获取输入的明文
        String rawPassword = (String)authentication.getCredentials();
        if (StringUtils.isBlank(username) || username.equals(SPLIT_STR)) {
            throw new BadCredentialsException("用户名不能为空");
        }
        if (StringUtils.isBlank(rawPassword)) {
            throw new BadCredentialsException("密码不能为空");
        }
        Object object = authentication.getDetails();
        if (object instanceof LinkedHashMap) {
            LinkedHashMap<String, String> auDetails = (LinkedHashMap<String, String>)authentication.getDetails();
            String grantType = auDetails.get("grant_type");
            log.debug(grantType);
        } else if (object instanceof WebAuthenticationDetails) {
            String CAPTCHA = (String)request.getSession().getAttribute(CaptchaHelper.CACHE_CAPTCHA);
            if (CAPTCHA == null || CAPTCHA.equals("")) {
                throw new LockedException("验证码失效");
            }
            String[] captcha = StringUtils.split(username, SPLIT_STR);
            if (StringUtils.isBlank(captcha[1])) {
                throw new LockedException("验证码为空");
            }
            if (!captcha[1].toUpperCase().equals(CAPTCHA)) {
                throw new LockedException("验证码错误");
            }
            username = captcha[0];
        } else {
            log.error("TODO I don't know this way!");
        }
        // 查询用户是否存在
        CoreUser sysUser = coreUserDao.findUserByLoginName(username);
        if (null == sysUser) {
            throw new UsernameNotFoundException("用户不存在");
        }
        // String userJson = JSON.toJSONString(sysUserDto);
        // 验证密码
        // if (!Md5Util.isMatchPassword(rawPassword, sysUser.getPassword())) {
        // throw new BadCredentialsException("输入密码错误");
        // }
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        return new UsernamePasswordAuthenticationToken(sysUser.getCode(), rawPassword, grantedAuthorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 确保authentication能转成该类
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
