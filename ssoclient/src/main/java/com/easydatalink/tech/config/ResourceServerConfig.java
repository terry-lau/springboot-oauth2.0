package com.easydatalink.tech.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

/**
 * 资源管理
 * 
 * @author Terry
 * @date 2021/04/27
 */
@Configuration
@EnableResourceServer
public class ResourceServerConfig extends ResourceServerConfigurerAdapter {
    // SING签名的设置，不能对外暴露
    public static final String PRIVATE_KEY = "szYh2021!J1JK3JH^&g%f*f@f*(f!)fs*#s*$H3J4DK43";

    @Bean("resourceServerRequestMatcher")
    public RequestMatcher resources() {
        return new AntPathRequestMatcher("/client/**");
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.requestMatcher(resources()).authorizeRequests().anyRequest().authenticated().and().cors().and().csrf()
            .disable();

    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        super.configure(resources);
        resources.tokenServices(tokenServices());
    }

    @Bean
    @Primary
    public JwtTokenStore jwtTokenStore() {
        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    @Primary
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(PRIVATE_KEY);
        return converter;
    }

    /**
     * resourceServerTokenServices 类的实例，用来实现令牌服务。
     *
     * @return
     */
    @Bean
    @Primary
    public DefaultTokenServices tokenServices() {
        DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(jwtTokenStore());
        return defaultTokenServices;
    }
}
