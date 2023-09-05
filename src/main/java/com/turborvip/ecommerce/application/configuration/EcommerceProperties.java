package com.turborvip.ecommerce.application.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "ecommerce")
public class EcommerceProperties {
    // token
    private int accessTokenDueTime;
    private int refreshTokenDueTime;

    // jdbc
    private String jdbcDriverClassName;
    private String jdbcUrl;
    private String jdbcUser;
    private String jdbcPass;

    // hikari
    private String hikariPoolName;
    private int hikariTimeout;
    private String hikariAutoCommit;
    private int hikariMaxLifeTime;
    private long hikariConnectionTimeout;
    private int hikariMaxPoolSize;

    // hibernate
    private String hibernateDialect;
    private String hibernateShowSQL;
    private String hibernateFormatSQL;
    private String hbm2ddlAuto;
    private String hbm2ddlCreateNamespaces;

    // email
    private String fromEmail;

}