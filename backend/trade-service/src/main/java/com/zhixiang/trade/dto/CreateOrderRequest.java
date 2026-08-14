package com.zhixiang.trade.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    private String tableNo;
    private String payType;
    private String memberPhone;
    private List<OrderItemDTO> items;
}
