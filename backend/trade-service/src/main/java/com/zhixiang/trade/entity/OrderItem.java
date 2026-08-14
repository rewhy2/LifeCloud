package com.zhixiang.trade.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItem {
    private Long id;
    private Long orderId;
    private Long productId;
    private String name;
    private BigDecimal price;
    private Integer quantity;
}
