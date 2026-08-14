package com.zhixiang.ops.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class Employee {
    private Long id;
    private String name;
    private String role; // WAITER/CHEF/CASHIER
    private String phone;
    private BigDecimal salary;
    private LocalDateTime createTime;
}
