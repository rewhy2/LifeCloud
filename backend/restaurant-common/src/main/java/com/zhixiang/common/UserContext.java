package com.zhixiang.common;

/**
 * 当前登录用户上下文（基于 ThreadLocal）
 */
public class UserContext {
    private static final ThreadLocal<LoginUser> TL = new ThreadLocal<>();

    public static void set(LoginUser user) {
        TL.set(user);
    }

    public static LoginUser get() {
        return TL.get();
    }

    public static void clear() {
        TL.remove();
    }
}
