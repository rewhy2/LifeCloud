package com.zhixiang.trade.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Inventory {
    private Long id;
    private String name;
    private Long supplierId;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal threshold;
    private BigDecimal price;
    private LocalDateTime createTime;
}
