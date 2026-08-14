package com.zhixiang.ops.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PurchaseItem {
    private Long id;
    private Long purchaseId;
    private Long inventoryId;
    private String name;
    private BigDecimal quantity;
    private BigDecimal price;
}
