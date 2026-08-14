package com.zhixiang.ai.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryDTO {
    private Long id;
    private String name;
    private Long supplierId;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal threshold;
}
