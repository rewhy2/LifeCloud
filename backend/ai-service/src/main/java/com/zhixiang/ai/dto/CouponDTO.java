package com.zhixiang.ai.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CouponDTO {
    private Long id;
    private String name;
    private String type;
    private BigDecimal threshold;
    private BigDecimal value;
    private Integer total;
    private Integer received;
    private Integer used;
    private Integer status;
}
