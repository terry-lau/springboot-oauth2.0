package com.easydatalink.tech.config.mybatis;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * @ClassName: MyBatisConfig
 * @Description: Spring Boot集成Mybatis的基本入口
 * @author Terry
 */
@Configuration
@MapperScan(basePackages = "com.easydatalink.tech.mapper")
public class MyBatisConfig {

    @Autowired
    private Environment env;

    /**
     * 根据数据源创建SqlSessionFactory
     *
     * @param ds
     * @return
     * @throws Exception
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource ds) throws Exception {
        SqlSessionFactoryBean sfb = new SqlSessionFactoryBean();
        sfb.setDataSource(ds);
        // 下边两句仅仅用于*.xml文件，如果整个持久层操作不需要使用到xml文件的话（只用注解就可以搞定），则不加
        sfb.setTypeAliasesPackage(env.getProperty("mybatis.typeAliasesPackage"));
        sfb.setMapperLocations(new PathMatchingResourcePatternResolver().getResources(env
            .getProperty("mybatis.mapperLocations")));
//        sfb.setPlugins(new Interceptor[] {new CatMybatisPlugin()});
        sfb.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
        return sfb.getObject();
    }
}
