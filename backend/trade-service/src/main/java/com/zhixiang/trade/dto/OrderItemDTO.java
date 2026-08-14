package com.zhixiang.trade.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long productId;
    @JsonAlias({"qty", "count", "num"})
    private Integer quantity;
}
