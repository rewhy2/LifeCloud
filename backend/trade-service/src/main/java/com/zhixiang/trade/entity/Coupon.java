package com.zhixiang.trade.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Coupon {
    private Long id;
    private String name;
    private String type; // FULL_REDUCE/DISCOUNT
    private BigDecimal threshold;
    private BigDecimal value;
    private Integer total;
    private Integer received;
    private Integer used;
    private Integer status; // 1启用 0停用
    private LocalDateTime createTime;
}
