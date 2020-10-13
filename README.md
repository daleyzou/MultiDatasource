# MultiDatasource
 springboot1.5和jpa利用HikariCP实现多数据源的使用
 
 #### 背景
 现在已有一个完整的项目，需要引入一个新的数据源，其实也就是分一些请求到从库上去
 
 #### 技术栈
 springboot1.5 (哎，升不动啊)
 
 #### 思路
 1. 两个数据源，其中一个设置为主数据源
 1. 两个事物管理器，其中一个设置为主默认事物管理器
 1. 使用非主数据源时，一定要设置对应的事物管理器
 2. 利用 dao 下的不同包路径，不同路径下的对应 Repository 使用不同的数据源
 
 ```
 @Service
 @Transactional(transactionManager = "transactionManagerSecond", rollbackFor = Exception.class)
 public class DashBoardService { }
 ```
 
 #### 启动日志
 
 ```
 [timestamp=2020-10-13 21:09:19.317] [level=INFO] [tx_id=] [span_id=] [bu_id=JT_MW] [app_id=iflow] HikariCP pool "mysql-hikari-pool-1" is starting.
 [timestamp=2020-10-13 21:09:19.584] [level=INFO] [tx_id=] [span_id=] [bu_id=JT_MW] [app_id=iflow] 
 
 	HikariCP连接池配置
 	连接池名称："mysql-hikari-pool-1"
 	最小空闲连接数：1
 	最大连接数：20
 	连接超时时间：3000ms
 	空闲连接超时时间：600000ms
 	连接最长生命周期：1800000ms
 
 [timestamp=2020-10-13 21:09:19.628] [level=INFO] [tx_id=] [span_id=] [bu_id=JT_MW] [app_id=iflow] Building JPA container EntityManagerFactory for persistence unit 'primaryPersistenceUnit'
 [timestamp=2020-10-13 21:09:19.638] [level=INFO] [tx_id=] [span_id=] [bu_id=JT_MW] [app_id=iflow] HHH000204: Processing PersistenceUnitInfo [
 	name: primaryPersistenceUnit
 	...]
 [timestamp=2020-10-13 21:09:19.697] [level=INFO] [tx_id=] [span_id=] [bu_id=JT_MW] [app_id=iflow] HHH000412: Hibernate Core {5.0.11.Final}
 [timestamp=2020-10-13 21:09:19.698] [level=INFO] [tx_id=] [span_id=] [bu_id=JT_MW] [app_id=iflow] HHH000206: hibernate.properties not found
 [timestamp=2020-10-13 21:09:19.699] [level=INFO] [tx_id=] [span_id=] [bu_id=JT_MW] [app_id=iflow] HHH000021: Bytecode provider name : javassist
 [timestamp=2020-10-13 21:09:19.740] [level=INFO] [tx_id=] [span_id=] [bu_id=JT_MW] [app_id=iflow] HCANN000001: Hibernate Commons Annotations {5.0.1.Final}
 [timestamp=2020-10-13 21:09:19.904] [level=INFO] [tx_id=] [span_id=] [bu_id=JT_MW] [app_id=iflow] HHH000400: Using dialect: org.hibernate.dialect.MySQLDialect
 [timestamp=2020-10-13 21:09:20.767] [level=INFO] [tx_id=] [span_id=] [bu_id=JT_MW] [app_id=iflow] Initialized JPA EntityManagerFactory for persistence unit 'primaryPersistenceUnit'
 [timestamp=2020-10-13 21:09:20.937] [level=INFO] [tx_id=] [span_id=] [bu_id=JT_MW] [app_id=iflow] 
 
 	HikariCP连接池配置
 	连接池名称："mysql-hikari-pool-2"
 	最小空闲连接数：1
 	最大连接数：20
 	连接超时时间：3000ms
 	空闲连接超时时间：600000ms
 	连接最长生命周期：1800000ms
 
 [timestamp=2020-10-13 21:09:20.967] [level=INFO] [tx_id=] [span_id=] [bu_id=JT_MW] [app_id=iflow] Building JPA container EntityManagerFactory for persistence unit 'secondPersistenceUnit'
 [timestamp=2020-10-13 21:09:20.967] [level=INFO] [tx_id=] [span_id=] [bu_id=JT_MW] [app_id=iflow] HHH000204: Processing PersistenceUnitInfo [
 	name: secondPersistenceUnit
 	...]
 [timestamp=2020-10-13 21:09:21.036] [level=INFO] [tx_id=] [span_id=] [bu_id=JT_MW] [app_id=iflow] HikariCP pool "mysql-hikari-pool-2" is starting.
 [timestamp=2020-10-13 21:09:21.113] [level=INFO] [tx_id=] [span_id=] [bu_id=JT_MW] [app_id=iflow] HHH000400: Using dialect: org.hibernate.dialect.MySQLDialect
 [timestamp=2020-10-13 21:09:21.369] [level=INFO] [tx_id=] [span_id=] [bu_id=JT_MW] [app_id=iflow] Initialized JPA EntityManagerFactory for persistence unit 'secondPersistenceUnit'
 [timestamp=2020-10-13 21:09:21.834] [level=INFO] [tx_id=] [span_id=] [bu_id=JT_MW] [app_id=iflow] HHH000397: Using ASTQueryTranslatorFactory
 [timestamp=2020-10-13 21:09:26.616] [level=INFO] [tx_id=] [span_id=] [bu_id=JT_MW] [app_id=iflow] HHH000397: Using ASTQueryTranslatorFactory
 ```
 
 #### 重要
 *在使用非主数据源时，一定要显式的指定对应使用管理器，不然连接池会耗尽的
 *
 
 #### dao层使用第二数据源， 用EntityManager
 
 ```
 package com.daleyzou.multidatasource.dao.second;
 
 import com.daleyzou.multidatasource.po.NodePo;
 import lombok.extern.slf4j.Slf4j;
 import org.hibernate.SQLQuery;
 import org.hibernate.transform.Transformers;
 import org.springframework.stereotype.Repository;
 
 import javax.persistence.EntityManager;
 import javax.persistence.PersistenceContext;
 import java.util.List;
 
 /**
  * @ClassName NodeNativeSQLDao
  * @Description: 在dao层使用第二数据源， 用EntityManager， 需要指定对应的 unitName
  * @Author dalelyzou
  * @Date 2020/10/7
  * @Version V1.0
  **/
 @Repository
 @Slf4j
 public class NodeNativeSQLDao {
 
     @PersistenceContext(unitName = "secondPersistenceUnit")
     private EntityManager entityManager;
 
     /**
      *  使用自定义SQL查询数据
      *
      * @param
      * @return
      * @author daleyzou
      */
     public List<NodePo> getAll() {
         StringBuilder sb = new StringBuilder();
         sb.append("SELECT * from node");
         SQLQuery sqlQuery = entityManager.createNativeQuery(sb.toString()).unwrap(SQLQuery.class);
         org.hibernate.Query query = sqlQuery.setResultTransformer(Transformers.aliasToBean(NodePo.class));
         return query.list();
     }
 }
 
 ```
 对应的unitName是我们自己在数据源的 SecondConfig 里定义的
 
 ```
  @Bean(name = "entityManagerFactorySecond")
     public LocalContainerEntityManagerFactoryBean entityManagerFactorySecond(EntityManagerFactoryBuilder builder) {
         DataSourceConfig.logDS(secondDataSource);
         return builder.dataSource(secondDataSource).properties(getVendorProperties(secondDataSource))
                 .packages("com.daleyzou.multidatasource.po").persistenceUnit("secondPersistenceUnit").build();
     }
 ```
 
 #### 主要代码如下
 DataSourceConfig
 
 ```
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
  * @description 数据源配置
  * @author daleyzou
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
 
 ```
 
 PrimaryConfig
 
 ```
 package com.daleyzou.multidatasource.config;
 
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.beans.factory.annotation.Qualifier;
 import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
 import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.context.annotation.Primary;
 import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
 import org.springframework.orm.jpa.JpaTransactionManager;
 import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
 import org.springframework.transaction.PlatformTransactionManager;
 import org.springframework.transaction.annotation.EnableTransactionManagement;
 
 import javax.persistence.EntityManager;
 import javax.sql.DataSource;
 import java.util.Map;
 
 /**
  * PrimaryConfig
  * @description 默认的主数据源
  * @author daleyzou
  * @date 2020年10月12日 19:46
  * @version 1.4.8
  */
 @Configuration
 @EnableTransactionManagement
 @EnableJpaRepositories(entityManagerFactoryRef = "entityManagerFactoryPrimary", transactionManagerRef = "transactionManagerPrimary", basePackages = {
         "com.daleyzou.multidatasource.dao.primary" })
 public class PrimaryConfig {
 
     @Autowired
     private JpaProperties jpaProperties;
 
     @Autowired
     @Qualifier("primaryDataSource")
     private DataSource primaryDataSource;
 
     @Primary
     @Bean(name = "entityManagerPrimary")
     public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
         return entityManagerFactoryPrimary(builder).getObject().createEntityManager();
     }
 
     @Primary
     @Bean(name = "entityManagerFactoryPrimary")
     public LocalContainerEntityManagerFactoryBean entityManagerFactoryPrimary(EntityManagerFactoryBuilder builder) {
         DataSourceConfig.logDS(primaryDataSource);
         return builder.dataSource(primaryDataSource).properties(getVendorProperties(primaryDataSource))
                 .packages("com.daleyzou.multidatasource.po").persistenceUnit("primaryPersistenceUnit").build();
     }
 
     private Map<String, String> getVendorProperties(DataSource dataSource) {
         return jpaProperties.getHibernateProperties(dataSource);
     }
 
     @Primary
     @Bean(name = "transactionManagerPrimary")
     public PlatformTransactionManager transactionManagerPrimary(EntityManagerFactoryBuilder builder) {
         return new JpaTransactionManager(entityManagerFactoryPrimary(builder).getObject());
     }
 }
 
 ```
 
 SecondConfig
 
 ```
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
  * @description 第二数据源
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
 ```
 
 ## 仓库代码
 https://github.com/daleyzou/MultiDatasource
 

