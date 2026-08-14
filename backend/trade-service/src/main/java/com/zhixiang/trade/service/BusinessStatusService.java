package com.zhixiang.trade.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 营业状态机服务：Redis 为实时状态机，MySQL 落库持久化。
 */
@Service
public class BusinessStatusService {

    private static final String REDIS_KEY = "biz:status";
    private static final String OPEN = "OPEN";
    private static final String CLOSED = "CLOSED";

    private final StringRedisTemplate redisTemplate;
    private final JdbcTemplate jdbcTemplate;

    public BusinessStatusService(StringRedisTemplate redisTemplate, JdbcTemplate jdbcTemplate) {
        this.redisTemplate = redisTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public String getStatus() {
        String v = redisTemplate.opsForValue().get(REDIS_KEY);
        if (v == null) {
            v = jdbcTemplate.queryForObject("SELECT status FROM business_status WHERE id = 1", String.class);
            if (v == null) v = OPEN;
            redisTemplate.opsForValue().set(REDIS_KEY, v);
        }
        return v;
    }

    public Map<String, Object> setStatus(String status) {
        if (!OPEN.equals(status) && !CLOSED.equals(status)) {
            throw new IllegalArgumentException("状态必须是 OPEN 或 CLOSED");
        }
        redisTemplate.opsForValue().set(REDIS_KEY, status);
        jdbcTemplate.update("UPDATE business_status SET status = ? WHERE id = 1", status);
        Map<String, Object> result = new HashMap<>();
        result.put("status", status);
        result.put("label", OPEN.equals(status) ? "营业中" : "已打烊");
        result.put("updateTime", System.currentTimeMillis());
        return result;
    }

    public Map<String, Object> close() {
        return setStatus(CLOSED);
    }

    public Map<String, Object> open() {
        return setStatus(OPEN);
    }
}
