package com.zhixiang.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具（HS256）。secret/expiration/issuer 由 JwtProperties 注入。
 */
public class JwtUtil {

    private final String secret;
    private final Long expiration; // ms
    private final String issuer;

    public JwtUtil(String secret, Long expiration, String issuer) {
        this.secret = secret;
        this.expiration = expiration;
        this.issuer = issuer;
    }

    public JwtUtil() {
        // 默认占位，由 Spring 注入的实例优先
        this.secret = "zhixiang-restaurant-secret-key-2024";
        this.expiration = 86400000L;
        this.issuer = "zhixiang";
    }

    public String generateToken(String username, String role, Long userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        if (userId != null) claims.put("uid", userId);
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuer(issuer)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expiration))
                .signWith(SignatureAlgorithm.HS256, secret.getBytes())
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .setSigningKey(secret.getBytes())
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isExpired(String token) {
        try {
            Date exp = parse(token).getExpiration();
            return exp.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
}
