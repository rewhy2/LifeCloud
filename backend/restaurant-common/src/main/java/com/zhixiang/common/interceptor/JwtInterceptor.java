package com.zhixiang.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhixiang.common.JwtUtil;
import com.zhixiang.common.LoginUser;
import com.zhixiang.common.Result;
import com.zhixiang.common.UserContext;
import com.zhixiang.common.config.JwtProperties;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.List;

/**
 * JWT 鉴权拦截器：校验 token，注入当前登录用户到上下文。
 *
 * <p>放行判定使用 {@link AntPathMatcher} 对规范化后的 URI 做精确匹配，
 * 避免使用 {@code contains}/子串匹配导致的路径遍历绕过（如 {@code /x/auth/login/../admin/users}）。</p>
 */
@Component
public class JwtInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final List<String> excludePaths;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtInterceptor(JwtUtil jwtUtil, JwtProperties jwtProperties) {
        this.jwtUtil = jwtUtil;
        this.excludePaths = jwtProperties.getExcludePaths();
    }

    /** 规范化路径：解码并解析 {@code ./}{@code ../}，消除路径遍历。 */
    private static String normalize(String uri) {
        if (uri == null) return "/";
        String s = uri;
        int q = s.indexOf('?');
        if (q >= 0) s = s.substring(0, q);
        int h = s.indexOf('#');
        if (h >= 0) s = s.substring(0, h);
        try {
            s = java.net.URLDecoder.decode(s, java.nio.charset.StandardCharsets.UTF_8);
            s = org.springframework.web.util.UriComponentsBuilder.fromPath(s).build().toUriString();
        } catch (Exception ignored) {
            // 解析失败时原样返回，由匹配逻辑兜底
        }
        return s;
    }

    private boolean isExcluded(String uri) {
        for (String pattern : excludePaths) {
            if (pathMatcher.match(pattern, uri)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String uri = normalize(request.getRequestURI());
        if (isExcluded(uri)) {
            return true;
        }

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "未登录或 token 缺失");
            return false;
        }
        String token = header.substring(7);
        try {
            Claims claims = jwtUtil.parse(token);
            if (jwtUtil.isExpired(token)) {
                writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "登录已过期，请重新登录");
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
            writeError(response, HttpServletResponse.SC_UNAUTHORIZED, "token 无效");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        UserContext.clear();
    }

    private void writeError(HttpServletResponse response, int status, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(status);
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(status, msg)));
    }
}
