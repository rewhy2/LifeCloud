package com.zhixiang.trade.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserCoupon {
    private Long id;
    private Long userId;
    private Long couponId;
    private String couponName;
    private String type;
    private BigDecimal threshold;
    private BigDecimal value;
    private Integer status; // 1未使用 2已使用 3已过期
    private LocalDateTime createTime;
}
