package com.zhixiang.auth.controller;

import com.zhixiang.auth.dto.SysUserDTO;
import com.zhixiang.auth.service.AdminService;
import com.zhixiang.common.Result;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AdminService adminService;

    public AdminUserController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public Result<List<Map<String, Object>>> list(@RequestParam(required = false) String role,
                                                  @RequestParam(required = false) String keyword) {
        return Result.success(adminService.listUsers(role, keyword));
    }

    @PostMapping
    public Result<Void> create(@RequestBody SysUserDTO dto) {
        adminService.createUser(dto);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SysUserDTO dto) {
        dto.setId(id);
        adminService.updateUser(dto);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestParam Integer status) {
        adminService.toggleStatus(id, status);
        return Result.success();
    }

    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(@PathVariable Long id) {
        adminService.resetPassword(id);
        return Result.success();
    }
}
