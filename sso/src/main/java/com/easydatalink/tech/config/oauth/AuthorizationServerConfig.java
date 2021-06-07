package com.easydatalink.tech.config.oauth;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.easydatalink.tech.service.MyUserDetailService;
import com.easydatalink.tech.utils.JwtTokenUtils;

/**
 * @program: oauth2_server
 * 
 * @description: AuthorizationServer配置
 * 
 *               1、配置认证相关属性 configure(AuthorizationServerSecurityConfigurer security)
 * 
 *               2、配置客户端详情 configure(ClientDetailsServiceConfigurer clients)
 * 
 *               3、配置认证服务的端点Endpoints相关属性 configure(AuthorizationServerEndpointsConfigurer endpoints)
 * 
 * 
 * @author Terry
 *
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;
    @Autowired
    private MyUserDetailService myUserDetailService;
    @Autowired
    private ClientDetailsService clientDetailsService;

    /**
     * 配置认证服务 oauthServer
     * 
     * @param oauthServer
     */
    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // 允许表单认证
        security.allowFormAuthenticationForClients();
        // 允许check_token访问
        security.checkTokenAccess("permitAll()");
        // 校验客户端授权信息
        security.tokenKeyAccess("isAuthenticated()");
    }

    /**
     * 授权客户端
     * 
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.jdbc(dataSource);
    }

    /**
     * 配置访问端口endpoints
     * 
     * @param endpoints
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        // 自定义token生成方案
        endpoints.accessTokenConverter(jwtAccessTokenConverter());
        // token存储
        endpoints.tokenStore(redisTokenStore());
        // 设置userDetailsService刷新token时候会用到
        endpoints.userDetailsService(myUserDetailService);
        // 身份认证管理器, 主要用于"password"授权模式
        endpoints.authenticationManager(authenticationManager);
        // 客户端模式
        endpoints.setClientDetailsService(clientDetailsService);
    }

    /**
     * tokenstore
     */
    @Bean
    @Primary
    public JwtTokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtAccessTokenConverter());
    }

    @Bean
    @Primary
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        // 设置签名
        jwtAccessTokenConverter.setSigningKey(JwtTokenUtils.PRIVATE_KEY);
        return jwtAccessTokenConverter;
    }

    @Bean
    public TokenStore redisTokenStore() {
        return new RedisTokenStore(redisConnectionFactory);
    }

    /**
     * <p>
     * 注意，自定义TokenServices的时候，需要设置@Primary，否则报错，
     * </p>
     * 
     * @return
     */
    @Primary
    @Bean
    public DefaultTokenServices defaultTokenServices() {
        DefaultTokenServices tokenServices = new DefaultTokenServices();
        tokenServices.setTokenStore(redisTokenStore());
        tokenServices.setSupportRefreshToken(true);
        // token有效期自定义设置，默认12小时
        tokenServices.setAccessTokenValiditySeconds(60 * 60 * 12);
        // refresh_token默认30天
        tokenServices.setRefreshTokenValiditySeconds(60 * 60 * 24 * 7);
        return tokenServices;
    }

}
