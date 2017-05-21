package com.spider.business.repostory.configration;

import com.alibaba.druid.pool.DruidDataSource;
import com.spider.business.repostory.MyBatisRepository;
import com.spider.business.repostory.plugin.PageHelper;
import com.spider.business.repostory.properties.DataSourceProperties;
import java.sql.SQLException;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

/**
 * Created by wangpeng on 2016/10/8.
 */
@Configuration
@MapperScan(basePackages = { "com.spider.business.repostory" }, annotationClass = MyBatisRepository.class)
@EnableTransactionManagement(proxyTargetClass = true)
public class MyBatisConfigration implements TransactionManagementConfigurer {

    @Autowired
    private DataSourceProperties dataSourceProperties;

    @Autowired
    private PageHelper pageHelper;

    @Bean(name = "dataSource", destroyMethod = "close")
    public DruidDataSource getDataSource() throws SQLException {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(dataSourceProperties.getDriverClassName());
        dataSource.setUrl(dataSourceProperties.getUrl());
        dataSource.setUsername(dataSourceProperties.getUsername());
        dataSource.setPassword(dataSourceProperties.getPassword());
        dataSource.setDefaultAutoCommit(dataSourceProperties.isDefaultAutoCommit());
        dataSource.setMaxActive(dataSourceProperties.getMaxActive());
        dataSource.setMaxIdle(dataSourceProperties.getMaxIdle());
        dataSource.setMinIdle(dataSourceProperties.getMinIdle());
        dataSource.setMaxWait(dataSourceProperties.getMaxWait());
        dataSource.setFilters("stat");
        dataSource.setInitialSize(2);
        dataSource.setTimeBetweenEvictionRunsMillis(60000);
        dataSource.setMinEvictableIdleTimeMillis(300000);
        dataSource.setValidationQuery("SELECT 'x' FROM DUAL");
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(50);
        //<!-- 超过时间限制是否回收 -->
        dataSource.setRemoveAbandoned(true);
        //<!-- 超时时间；单位为秒。180秒=3分钟 -->
        dataSource.setRemoveAbandonedTimeout(180);
        return dataSource;
    }


    @Bean
    public SqlSessionFactory getSqlSessionFactory() throws Exception {
        SqlSessionFactoryBean sqlSessionFactory = new SqlSessionFactoryBean();
        sqlSessionFactory.setDataSource(getDataSource());
        sqlSessionFactory.setTypeAliasesPackage("com.spider.common.bean");
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sqlSessionFactory.setConfigLocation(resolver.getResource("classpath:configuration.xml"));
        sqlSessionFactory.setMapperLocations(resolver.getResources("classpath:mybatis/*Mapper.xml"));
        sqlSessionFactory.setPlugins(new Interceptor[]{pageHelper});
        return sqlSessionFactory.getObject();
    }




    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        try {
            return new DataSourceTransactionManager(getDataSource());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
