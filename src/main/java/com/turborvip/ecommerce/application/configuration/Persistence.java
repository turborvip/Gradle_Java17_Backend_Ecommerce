package com.turborvip.ecommerce.application.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@EnableTransactionManagement
@ConfigurationProperties(prefix = "yaml")
@PropertySource({"classpath:application.yml"})
@ComponentScan({"com.turborvip.ecommerce"})
@EnableJpaRepositories(basePackages = "com.turborvip.ecommerce.application.repositories")
@NoArgsConstructor
@Slf4j
public class Persistence {

    private static final HikariConfig hikariConfig = new HikariConfig();
    @Autowired
    EcommerceProperties ecommerceProperties;

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource());
        // provider entity package name
        entityManagerFactoryBean.setPackagesToScan("com.turborvip.ecommerce.domain.entity");

        final HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        entityManagerFactoryBean.setJpaProperties(additionalProperties());

        return entityManagerFactoryBean;
    }

    final @NotNull Properties additionalProperties() {
        final Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", ecommerceProperties.getHbm2ddlAuto());
        hibernateProperties.setProperty("hibernate.dialect", ecommerceProperties.getHibernateDialect());
        hibernateProperties.setProperty("hibernate.hbm2dll.create_namespaces", ecommerceProperties.getHbm2ddlCreateNamespaces());
        hibernateProperties.setProperty("hibernate.show_sql", ecommerceProperties.getHibernateShowSQL());
        hibernateProperties.setProperty("hibernate.format_sql", ecommerceProperties.getHibernateFormatSQL());
//        hibernateProperties.setProperty("hibernate.cache.use_second_level_cache", env.getProperty("hibernate.cache.use_second_level_cache"));
//        hibernateProperties.setProperty("hibernate.cache.use_query_cache", env.getProperty("hibernate.cache.use_query_cache"));
        // hibernateProperties.setProperty("hibernate.globally_quoted_identifiers", "true");
        return hibernateProperties;
    }

    @Bean
    public DataSource dataSource() {
        hikariConfig.setDriverClassName(ecommerceProperties.getJdbcDriverClassName());
        hikariConfig.setJdbcUrl(ecommerceProperties.getJdbcUrl());
        hikariConfig.setUsername(ecommerceProperties.getJdbcUser());
        hikariConfig.setPassword(ecommerceProperties.getJdbcPass());
        hikariConfig.setPoolName(ecommerceProperties.getHikariPoolName());
        hikariConfig.setMaximumPoolSize(ecommerceProperties.getHikariMaxPoolSize());
        // time which is calculator from not activity, after that time connection was move to pool!
        hikariConfig.setIdleTimeout(ecommerceProperties.getHikariTimeout());
        hikariConfig.setAutoCommit(Boolean.parseBoolean(ecommerceProperties.getHikariAutoCommit()));
        hikariConfig.setMaxLifetime(ecommerceProperties.getHikariMaxLifeTime());
        hikariConfig.setConnectionTimeout(ecommerceProperties.getHikariConnectionTimeout());
        HikariDataSource hikariDataSource = new HikariDataSource(hikariConfig);
        return new HikariDataSource(hikariConfig);
    }

    @Bean
    public PlatformTransactionManager transactionManager(final EntityManagerFactory emf) {
        final JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf);
        return transactionManager;
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

//    @Bean
//    public void getConnection() throws SQLException {
//        log.info(String.valueOf(hikariDataSource.getConnection()));
//        log.info("Memory : {}", Runtime.getRuntime().totalMemory());
//        log.info("Processor : {}", Runtime.getRuntime().availableProcessors());
//    }
}