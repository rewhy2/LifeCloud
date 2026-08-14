package com.zhixiang.auth.service;

import com.zhixiang.auth.dto.LoginRequest;
import com.zhixiang.auth.dto.LoginResponse;
import com.zhixiang.auth.dto.RegisterRequest;
import com.zhixiang.auth.entity.User;
import com.zhixiang.auth.mapper.UserMapper;
import com.zhixiang.common.JwtUtil;
import com.zhixiang.common.LoginUser;
import com.zhixiang.common.Result;
import com.zhixiang.common.UserContext;
import com.zhixiang.common.security.PasswordEncoderUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;

    public AuthService(UserMapper userMapper, JwtUtil jwtUtil) {
        this.userMapper = userMapper;
        this.jwtUtil = jwtUtil;
    }

    public Result<LoginResponse> login(LoginRequest req) {
        User user = userMapper.selectByUsername(req.getUsername());
        if (user == null) {
            return Result.error("用户不存在");
        }
        if (!PasswordEncoderUtil.matches(req.getPassword(), user.getPassword())) {
            return Result.error("密码错误");
        }
        // 存量 MD5 密码兼容迁移：校验通过后用 BCrypt 重新加密写回
        if (PasswordEncoderUtil.isLegacyMd5(user.getPassword())) {
            userMapper.updatePassword(user.getId(), PasswordEncoderUtil.encode(req.getPassword()));
        }
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());
        LoginResponse resp = new LoginResponse(token, user.getUsername(), user.getNickName(), user.getRole());
        return Result.success(resp);
    }

    /** 顾客注册：固定角色 USER */
    public Result<LoginResponse> register(RegisterRequest req) {
        if (req.getUsername() == null || req.getUsername().isBlank()
                || req.getPassword() == null || req.getPassword().isBlank()) {
            return Result.error("用户名和密码不能为空");
        }
        if (userMapper.selectByUsername(req.getUsername()) != null) {
            return Result.error("该用户名已被注册");
        }
        User user = new User();
        user.setUsername(req.getUsername());
        user.setPassword(PasswordEncoderUtil.encode(req.getPassword()));
        user.setNickName(req.getNickName() == null || req.getNickName().isBlank() ? "顾客" : req.getNickName());
        user.setRole("USER");
        user.setStatus(1);
        userMapper.insert(user);
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());
        LoginResponse resp = new LoginResponse(token, user.getUsername(), user.getNickName(), user.getRole());
        return Result.success(resp);
    }

    public Result<LoginResponse> me() {
        LoginUser u = UserContext.get();
        if (u == null) {
            return Result.error("未登录");
        }
        User user = userMapper.selectByUsername(u.getUsername());
        if (user == null) {
            return Result.error("用户不存在");
        }
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole(), user.getId());
        LoginResponse resp = new LoginResponse(token, user.getUsername(), user.getNickName(), user.getRole());
        return Result.success(resp);
    }
}
