package com.zhixiang.auth.controller;

import com.zhixiang.auth.dto.LoginRequest;
import com.zhixiang.auth.dto.LoginResponse;
import com.zhixiang.auth.dto.RegisterRequest;
import com.zhixiang.auth.service.AuthService;
import com.zhixiang.common.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Result<LoginResponse> login(@RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/register")
    public Result<LoginResponse> register(@RequestBody RegisterRequest req) {
        return authService.register(req);
    }

    @GetMapping("/health")
    public Result<String> health() {
        return Result.success("ok");
    }

    @GetMapping("/me")
    public Result<LoginResponse> me() {
        return authService.me();
    }
}
