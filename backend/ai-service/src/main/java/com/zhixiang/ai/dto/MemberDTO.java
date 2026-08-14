package com.zhixiang.ai.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MemberDTO {
    private Long id;
    private String name;
    private String phone;
    private String level;
    private Integer point;
    private BigDecimal balance;
    private BigDecimal totalSpend;
}
