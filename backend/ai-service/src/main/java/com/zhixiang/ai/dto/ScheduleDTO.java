package com.zhixiang.ai.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ScheduleDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private LocalDate workDate;
    private String shift;
}
