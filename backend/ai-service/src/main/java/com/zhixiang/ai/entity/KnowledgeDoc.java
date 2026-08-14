package com.zhixiang.ai.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KnowledgeDoc {
    private Long id;
    private String category; // RULE/COMPLAINT
    private String title;
    private String content;
    private LocalDateTime createTime;
}
