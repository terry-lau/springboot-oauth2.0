package com.easydatalink.tech.config;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.easydatalink.tech.filter.SystemLogoutHandler;

/**
 * 
 * @author Terry
 * @date 2021/04/27
 */
@EnableOAuth2Sso
@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${sso.logout.url}")
    private String ssoLogoutUrl;
    @Value("${sso.login.url}")
    private String ssoLoginUrl;
    @Value("${security.oauth2.client.client-id}")
    private String clientId;
    @Autowired
    @Qualifier("resourceServerRequestMatcher")
    private RequestMatcher resources;
    @Autowired
    private SystemLogoutHandler systemLogoutHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        RequestMatcher nonResoures = new NegatedRequestMatcher(resources);
        http.requestMatcher(nonResoures).authorizeRequests().anyRequest().authenticated()
            // http.authorizeRequests()
            //// .antMatchers("/oauth/**","/api/**", "/error", "/css/**", "/js/**", "/fonts/**",
            //// "/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**").permitAll()
            // .antMatchers("/", "/login")
            // .authenticated()
            .and().logout().logoutUrl("/logout").deleteCookies("cas_access_token").addLogoutHandler(systemLogoutHandler)
            .logoutSuccessUrl(ssoLogoutUrl + "?clientId=" + clientId + "&redirect_uri=" + ssoLoginUrl + "&state="
                + getRandomString(6))
            .and().cors().and().csrf().disable();
    }

    /**
     * 随机数
     * 
     * @param length
     * @return
     */
    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        // 允许带凭证
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有URL生效
        source.registerCorsConfiguration("/**", config);
        return source;
    }

}
