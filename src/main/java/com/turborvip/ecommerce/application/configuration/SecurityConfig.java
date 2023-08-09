package com.turborvip.ecommerce.application.configuration;

import com.turborvip.ecommerce.application.constants.EnumRole;
import com.turborvip.ecommerce.application.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true, jsr250Enabled = true)
public class SecurityConfig {
    private final   JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(STATELESS)
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(true))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/v1/both/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/login").permitAll()
                        .requestMatchers(HttpMethod.POST,"/api/v1/logout").permitAll()
                        .requestMatchers("/api/v1/auth/**").hasAnyAuthority(EnumRole.ROLE_USER.toString(),EnumRole.ROLE_SUPER_ADMIN.toString(),EnumRole.ROLE_ADMIN.toString(),EnumRole.MANAGER.toString())
                        .requestMatchers("/api/v1/user/**").hasAnyAuthority(EnumRole.ROLE_USER.toString())
                        .requestMatchers("/api/v1/admin/**").hasAnyAuthority(EnumRole.ROLE_SUPER_ADMIN.toString(),EnumRole.ROLE_ADMIN.toString())
                        .requestMatchers("/api/v1/manager/**").hasAnyAuthority(EnumRole.ROLE_SUPER_ADMIN.toString(),EnumRole.ROLE_ADMIN.toString(),EnumRole.MANAGER.toString())
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}