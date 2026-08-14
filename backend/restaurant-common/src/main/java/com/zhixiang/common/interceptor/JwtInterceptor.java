package com.zhixiang.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhixiang.common.JwtUtil;
import com.zhixiang.common.LoginUser;
import com.zhixiang.common.Result;
import com.zhixiang.common.UserContext;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * JWT 鉴权拦截器：校验 token，注入当前登录用户到上下文
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JwtInterceptor(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String uri = request.getRequestURI();
        // 兼容是否携带 context-path(/api) 前缀
        if (uri.endsWith("/auth/login") || uri.contains("/auth/login")
                || uri.endsWith("/auth/health") || uri.contains("/auth/health")
                || uri.endsWith("/auth/register") || uri.contains("/auth/register")) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            writeError(response, "未登录或 token 缺失");
            return false;
        }
        String token = header.substring(7);
        try {
            Claims claims = jwtUtil.parse(token);
            if (jwtUtil.isExpired(token)) {
                writeError(response, "登录已过期，请重新登录");
                return false;
            }
            LoginUser user = new LoginUser();
            user.setUsername(claims.getSubject());
            user.setRole((String) claims.get("role"));
            Object uid = claims.get("uid");
            if (uid != null) user.setUserId(Long.valueOf(uid.toString()));
            UserContext.set(user);
            return true;
        } catch (Exception e) {
            writeError(response, "token 无效");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private void writeError(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, msg)));
    }
}
