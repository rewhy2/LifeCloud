package com.zhixiang.common.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhixiang.common.Result;
import com.zhixiang.common.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.List;

/**
 * 角色权限拦截器：在 JWT 鉴权之后，按 URL 前缀限制可访问角色。
 */
@Component
public class RoleInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final List<String> ADMIN_ONLY = List.of("/admin/");
    private static final List<String> USER_ONLY = List.of("/user/");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String uri = request.getRequestURI();
        String role = UserContext.get() == null ? null : UserContext.get().getRole();

        if (startsWithAny(uri, ADMIN_ONLY) && !"ADMIN".equals(role)) {
            return deny(response, "仅平台管理员可访问");
        }
        if (startsWithAny(uri, USER_ONLY) && !"USER".equals(role)) {
            return deny(response, "仅顾客端可访问");
        }
        boolean isMerchantDomain = !(startsWithAny(uri, ADMIN_ONLY) || startsWithAny(uri, USER_ONLY));
        if (isMerchantDomain && role != null && !"MERCHANT".equals(role) && !"ADMIN".equals(role)) {
            return deny(response, "无权限访问该模块");
        }
        return true;
    }

    private boolean startsWithAny(String uri, List<String> prefixes) {
        for (String p : prefixes) {
            if (uri.startsWith(p)) return true;
        }
        return false;
    }

    private boolean deny(HttpServletResponse response, String msg) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(Result.error(403, msg)));
        return false;
    }
}
