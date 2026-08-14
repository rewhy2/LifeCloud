package com.zhixiang.trade.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Product {
    private Long id;
    private String name;
    private Long categoryId;
    private BigDecimal price;
    private BigDecimal cost;
    private Integer status; // 1在售 0停售
    private String description;
    private String image;
    private Integer salesCount;
    private LocalDateTime createTime;
}
