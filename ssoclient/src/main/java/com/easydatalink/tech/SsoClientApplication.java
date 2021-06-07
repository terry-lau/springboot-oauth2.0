package com.easydatalink.tech;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * SsoClient
 * 
 * @author Terry
 * @date 2021/04/27
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.easydatalink"})
public class SsoClientApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(SsoClientApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(SsoClientApplication.class, args);
    }
}
