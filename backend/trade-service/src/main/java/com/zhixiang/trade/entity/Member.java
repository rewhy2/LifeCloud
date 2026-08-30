package com.zhixiang.trade.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Member {
    private Long id;
    private String name;
    private String phone;
    private String level; // NORMAL/SILVER/GOLD
    private Integer point;
    private BigDecimal balance;
    private BigDecimal totalSpend;
    private LocalDateTime createTime;
}
