package com.daleyzou.multidatasource.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

/**
 * SecondConfig
 * @description TODO
 * @author daleyzou
 * @date 2020年10月12日 19:46
 * @version 1.4.8
 */
@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        //实体管理
        entityManagerFactoryRef = "entityManagerFactorySecond",
        //事务管理
        transactionManagerRef = "transactionManagerSecond",
        //实体扫描,设置Repository所在位置
        basePackages = { "com.daleyzou.multidatasource.dao.second" })
public class SecondConfig {

    @Autowired
    private JpaProperties jpaProperties;

    @Autowired
    @Qualifier("secondDataSource")
    private DataSource secondDataSource;

    @Bean(name = "entityManagerSecond")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactorySecond(builder).getObject().createEntityManager();
    }

    @Bean(name = "entityManagerFactorySecond")
    public LocalContainerEntityManagerFactoryBean entityManagerFactorySecond(EntityManagerFactoryBuilder builder) {
        DataSourceConfig.logDS(secondDataSource);
        return builder.dataSource(secondDataSource).properties(getVendorProperties(secondDataSource))
                .packages("com.daleyzou.multidatasource.po").persistenceUnit("secondPersistenceUnit").build();
    }

    private Map<String, String> getVendorProperties(DataSource dataSource) {
        return jpaProperties.getHibernateProperties(dataSource);
    }

    @Bean(name = "transactionManagerSecond")
    PlatformTransactionManager transactionManagerSecond(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactorySecond(builder).getObject());
    }
}