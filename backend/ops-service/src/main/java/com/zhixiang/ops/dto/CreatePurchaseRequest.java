package com.zhixiang.ops.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreatePurchaseRequest {
    private Long supplierId;
    private List<PurchaseItemDTO> items;
}
