package com.zhixiang.auth.dto;

import lombok.Data;

@Data
public class SysUserDTO {
    private Long id;
    private String username;
    private String password;
    private String nickName;
    private String role; // ADMIN / MERCHANT
    private Integer status; // 1启用 0禁用
}
