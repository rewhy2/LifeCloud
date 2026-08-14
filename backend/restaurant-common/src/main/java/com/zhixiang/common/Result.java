package com.zhixiang.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 统一响应封装（泛型友好，可写作 Result<User> 也可写作 Result）
 */
@Data
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;
    private Long total;

    public Result() {}

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static <T> Result<T> ok() {
        return new Result<>(200, "操作成功", null);
    }

    public static <T> Result<T> ok(T data) {
        return new Result<>(200, "操作成功", data);
    }

    public static <T> Result<T> ok(String msg, T data) {
        return new Result<>(200, msg, data);
    }

    public static <T> Result<T> ok(T data, Long total) {
        Result<T> r = new Result<>(200, "操作成功", data);
        r.total = total;
        return r;
    }

    /** 兼容旧调用：Result.success(x) */
    public static <T> Result<T> success(T data) {
        return ok(data);
    }

    public static <T> Result<T> success(String msg, T data) {
        return ok(msg, data);
    }

    /** 兼容旧调用：Result.success() 无数据成功 */
    public static <T> Result<T> success() {
        return ok();
    }

    public static <T> Result<T> error(String msg) {
        return new Result<>(500, msg, null);
    }

    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    @SuppressWarnings("unchecked")
    public Object get(String key) {
        if (data instanceof Map) return ((Map<?, ?>) data).get(key);
        return null;
    }
}
