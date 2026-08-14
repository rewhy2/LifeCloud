package com.zhixiang.common.config;

import com.zhixiang.common.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 公共自动配置：注入 JwtUtil Bean（基于 JwtProperties）。
 */
@Configuration
public class CommonAutoConfiguration {

    private final JwtProperties jwtProperties;

    public CommonAutoConfiguration(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Bean
    public JwtUtil jwtUtil() {
        String secret = jwtProperties.getSecret() != null ? jwtProperties.getSecret() : "zhixiang-restaurant-secret-key-2024";
        Long exp = jwtProperties.getExpiration() != null ? jwtProperties.getExpiration() : 86400000L;
        String issuer = jwtProperties.getIssuer() != null ? jwtProperties.getIssuer() : "zhixiang";
        return new JwtUtil(secret, exp, issuer);
    }
}
