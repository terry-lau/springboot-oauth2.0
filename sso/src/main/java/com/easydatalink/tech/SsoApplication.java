package com.easydatalink.tech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;

/**
 * 单点登录启动入口
 * 
 * @author Terry
 *
 */
@EnableCaching
@SpringBootApplication
@ComponentScan(basePackages = {"com.easydatalink"})
public class SsoApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SsoApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SsoApplication.class, args);
    }
}
