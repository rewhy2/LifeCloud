package com.zhixiang.trade.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Order {
    private Long id;
    private String orderNo;
    private String tableNo;
    private BigDecimal amount;
    private String payType;
    private String status; // CREATED/PAID/CANCELLED/REFUNDED
    private Integer itemCount;
    private String memberPhone;
    private String username;
    private LocalDateTime createTime;

    private List<OrderItem> items;
}
