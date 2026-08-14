package com.zhixiang.auth.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;
    private String password;
    private String nickName;
    private String role;
    private Integer status; // 1启用 0禁用
    private LocalDateTime createTime;
}
