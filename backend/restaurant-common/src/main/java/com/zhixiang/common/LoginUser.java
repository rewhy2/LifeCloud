package com.zhixiang.common;

import lombok.Data;

@Data
public class LoginUser {
    private Long userId;
    private String username;
    private String role; // ADMIN / MERCHANT / USER
}
