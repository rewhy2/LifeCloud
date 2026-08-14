package com.zhixiang.trade.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TableInfo {
    private Long id;
    private String no;
    private Integer seats;
    private String area;
    private String status; // FREE/OCCUPIED/RESERVED
    private LocalDateTime createTime;
}
