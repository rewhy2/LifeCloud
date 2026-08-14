package com.zhixiang.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret;
    private Long expiration; // ms
    private String issuer;
    /** JWT 鉴权放行白名单（Ant 风格，单一配置源）。匹配前会对 URI 做规范化（抵御 ../ 路径遍历绕过）。 */
    private List<String> excludePaths = List.of(
            "/auth/login",
            "/auth/register",
            "/auth/health",
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/health",
            "/products/public",
            "/categories/public",
            "/api/products/public",
            "/api/categories/public",
            "/",
            "/index.html",
            "/user.html",
            "/css/**",
            "/js/**",
            "/images/**",
            "/favicon.ico",
            "/*.html");
}
