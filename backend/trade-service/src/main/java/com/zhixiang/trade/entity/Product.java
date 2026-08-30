package com.zhixiang.trade.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private String name;
    private Long categoryId;
    private Long inventoryId; // 关联库存食材(inventory.id)，用于下单扣减库存
    private BigDecimal price;
    private BigDecimal cost;
    private Integer status; // 1在售 0停售
    private String description;
    private String image;
    private Integer salesCount;
    private LocalDateTime createTime;
}
