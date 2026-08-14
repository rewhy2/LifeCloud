package com.zhixiang.ops.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final JdbcTemplate jdbcTemplate;

    public ReportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> todayOverview() {
        String today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        BigDecimal revenue = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount),0) FROM orders WHERE status='PAID' AND DATE(create_time)=?", BigDecimal.class, today);
        Long orderCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM orders WHERE status='PAID' AND DATE(create_time)=?", Long.class, today);
        BigDecimal refund = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount),0) FROM orders WHERE status='REFUNDED' AND DATE(create_time)=?", BigDecimal.class, today);
        int memberCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM member", Integer.class);
        Map<String, Object> m = new HashMap<>();
        m.put("date", today);
        m.put("revenue", revenue);
        m.put("orderCount", orderCount);
        m.put("refund", refund);
        m.put("memberCount", memberCount);
        return m;
    }

    public List<Map<String, Object>> salesByCategory(String date) {
        if (date == null) date = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        return jdbcTemplate.queryForList(
                "SELECT c.name AS category, COALESCE(SUM(oi.quantity * oi.price),0) AS amount, COALESCE(SUM(oi.quantity),0) AS qty " +
                        "FROM order_item oi JOIN orders o ON oi.order_id=o.id " +
                        "JOIN product p ON oi.product_id=p.id JOIN category c ON p.category_id=c.id " +
                        "WHERE o.status='PAID' AND DATE(o.create_time)=? GROUP BY c.name ORDER BY amount DESC", date);
    }

    public List<Map<String, Object>> revenueTrend(int days) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (int i = days - 1; i >= 0; i--) {
            LocalDate d = LocalDate.now().minusDays(i);
            String ds = d.format(DateTimeFormatter.ISO_LOCAL_DATE);
            BigDecimal rev = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(SUM(amount),0) FROM orders WHERE status='PAID' AND DATE(create_time)=?", BigDecimal.class, ds);
            Long ord = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM orders WHERE status='PAID' AND DATE(create_time)=?", Long.class, ds);
            Map<String, Object> row = new HashMap<>();
            row.put("date", ds);
            row.put("revenue", rev);
            row.put("orders", ord);
            result.add(row);
        }
        return result;
    }

    public List<Map<String, Object>> topProducts(int limit) {
        return jdbcTemplate.queryForList(
                "SELECT p.name, COALESCE(SUM(oi.quantity),0) AS qty, COALESCE(SUM(oi.quantity*oi.price),0) AS amount " +
                        "FROM order_item oi JOIN orders o ON oi.order_id=o.id JOIN product p ON oi.product_id=p.id " +
                        "WHERE o.status='PAID' GROUP BY p.id, p.name ORDER BY qty DESC LIMIT ?", limit);
    }

    public Map<String, Object> diagnose() {
        BigDecimal todayRev = (BigDecimal) todayOverview().get("revenue");
        BigDecimal yesterday = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(amount),0) FROM orders WHERE status='PAID' AND DATE(create_time)=?", BigDecimal.class,
                LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE));
        double rate = yesterday.doubleValue() > 0 ? (todayRev.doubleValue() - yesterday.doubleValue()) / yesterday.doubleValue() * 100 : 0;
        int lowStock = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM inventory WHERE quantity < threshold", Integer.class);
        Map<String, Object> m = new HashMap<>();
        m.put("todayRevenue", todayRev);
        m.put("yesterdayRevenue", yesterday);
        m.put("growthRate", Math.round(rate * 100.0) / 100.0);
        m.put("lowStockCount", lowStock);
        List<String> suggestions = new ArrayList<>();
        if (lowStock > 0) suggestions.add("有 " + lowStock + " 种食材低于安全库存，建议尽快补货");
        if (rate < 0) suggestions.add("营收环比下滑 " + Math.abs(Math.round(rate)) + "%，建议推出促销活动");
        if (suggestions.isEmpty()) suggestions.add("经营状况良好，继续保持");
        m.put("suggestions", suggestions);
        return m;
    }
}
