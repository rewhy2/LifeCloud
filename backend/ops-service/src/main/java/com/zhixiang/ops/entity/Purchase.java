package com.zhixiang.ops.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class Purchase {
    private Long id;
    private String orderNo;
    private Long supplierId;
    private BigDecimal amount;
    private String status; // PENDING/STOCKED
    private LocalDateTime createTime;
    private List<PurchaseItem> items;
}
