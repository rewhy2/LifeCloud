package com.zhixiang.ai.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private Long categoryId;
    private BigDecimal price;
    private Integer status;
}
