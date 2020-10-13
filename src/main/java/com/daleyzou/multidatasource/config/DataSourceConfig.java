package com.daleyzou.multidatasource.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

/**
 * DataSourceConfig
 * @description TODO
 * @author zoudaifa
 * @date 2020年10月12日 19:45
 * @version 1.4.8
 */
@Configuration
@Slf4j
public class DataSourceConfig {

    /**
     * 数据源配置对象
     * Primary 表示默认的对象，Autowire可注入，不是默认的得明确名称注入
     * @return
     */
    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSourceProperties primaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 数据源对象
     * @return
     */
    @Primary
    @Bean(name = "primaryDataSource")
    @Qualifier("primaryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.primary")
    public DataSource primaryDataSource() {
        return primaryDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.second")
    public DataSourceProperties secondaryDataSourceProperties() {
        return new DataSourceProperties();
    }

    /**
     * 第二个数据源
     * @return
     */
    @Bean(name = "secondDataSource")
    @Qualifier("secondDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.second")
    public DataSource secondaryDataSource() {
        return secondaryDataSourceProperties().initializeDataSourceBuilder().type(HikariDataSource.class).build();
    }

    /**
     * 显示数据库连接池信息
     *
     * @param dataSource
     */
    public static void logDS(DataSource dataSource) {
        HikariDataSource hds = (HikariDataSource) dataSource;
        String info = "\n\n\tHikariCP连接池配置\n\t连接池名称：" + hds.getPoolName() + "\n\t最小空闲连接数：" + hds.getMinimumIdle() + "\n\t最大连接数：" + hds
                .getMaximumPoolSize() + "\n\t连接超时时间：" + hds.getConnectionTimeout() + "ms\n\t空闲连接超时时间：" + hds.getIdleTimeout()
                + "ms\n\t连接最长生命周期：" + hds.getMaxLifetime() + "ms\n";
        log.info(info);
    }
}
