package com.zhixiang.ops.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseItemDTO {
    private Long inventoryId;
    private String name;
    private BigDecimal quantity;
    private BigDecimal price;
}
