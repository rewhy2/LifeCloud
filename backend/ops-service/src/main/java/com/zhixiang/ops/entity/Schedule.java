package com.zhixiang.ops.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class Schedule {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private LocalDate workDate;
    private String shift; // MORNING/AFTERNOON/NIGHT
    private LocalDateTime createTime;
}
