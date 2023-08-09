package com.turborvip.ecommerce.application.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import jakarta.persistence.EntityManagerFactory;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    @Value("${jdbc:driverClassName}")
    private String driverClassName;
    @Value("${jdbc:url}")
    private String jdbcUrl;
    @Value("${jdbc:user}")
    private String username;
    @Value("${jdbc:pass}")
    private String password;
    @Value("${hikari:poolName}")
    private String poolName;
    @Value("${hikari:timeout}")
    private String timeout;
    @Value("${hikari:autoCommit}")
    private String autoCommit;
    @Value("${hikari:maxLifeTime}")
    private String maxLifeTime;
    @Value("${hikari:connectionTimeout}")
    private String connectionTimeout;
    @Value("${hikari:maxPoolSize}")
    private String maxPoolSize;
    @Value("${hibernate:hbm2ddl:auto}")
    private String hbm2ddlAuto;
    @Value("${hibernate:dialect}")
    private String dialect;
    @Value("${hibernate:hbm2dll:create_namespaces}")
    private String createNamespaces;
    @Value("${hibernate:showSQL}")
    private String showSQL;
    @Value("${hibernate:formatSQL}")
    private String formatSQL;


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

    final Properties additionalProperties() {
        final Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "update");
        hibernateProperties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgresPlusDialect");
        hibernateProperties.setProperty("hibernate.hbm2dll.create_namespaces", "true");
        hibernateProperties.setProperty("hibernate.show_sql", "false");
        hibernateProperties.setProperty("hibernate.format_sql", "false");
//        hibernateProperties.setProperty("hibernate.cache.use_second_level_cache", env.getProperty("hibernate.cache.use_second_level_cache"));
//        hibernateProperties.setProperty("hibernate.cache.use_query_cache", env.getProperty("hibernate.cache.use_query_cache"));
        // hibernateProperties.setProperty("hibernate.globally_quoted_identifiers", "true");
        return hibernateProperties;
    }

    @Bean
    public DataSource dataSource() {
        hikariConfig.setDriverClassName("org.postgresql.Driver");
        hikariConfig.setJdbcUrl("jdbc:postgresql://localhost:2345/security?createDatabaseIfNotExist=true");
        hikariConfig.setUsername("turborvip");
        hikariConfig.setPassword("123456a");
        hikariConfig.setPoolName("DEVELOPER_CATALOG");
        hikariConfig.setMaximumPoolSize(Integer.parseInt("50"));
        // time which is calculator from not activity, after that time connection was move to pool!
        hikariConfig.setIdleTimeout(Long.parseLong("300000"));
        hikariConfig.setAutoCommit(Boolean.parseBoolean("true"));
        hikariConfig.setMaxLifetime(Long.parseLong("1800000"));
        hikariConfig.setConnectionTimeout(Long.parseLong("50000"));
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