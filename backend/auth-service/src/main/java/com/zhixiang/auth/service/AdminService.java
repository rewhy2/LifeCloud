package com.zhixiang.auth.service;

import com.zhixiang.auth.dto.SysUserDTO;
import com.zhixiang.auth.entity.User;
import com.zhixiang.auth.mapper.UserMapper;
import com.zhixiang.common.security.PasswordEncoderUtil;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    private final UserMapper userMapper;

    public AdminService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public List<Map<String, Object>> listUsers(String role, String keyword) {
        return userMapper.selectAll(role, keyword).stream().map(u -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("nickName", u.getNickName());
            m.put("role", u.getRole());
            m.put("status", u.getStatus());
            m.put("createTime", u.getCreateTime());
            return m;
        }).collect(Collectors.toList());
    }

    public void createUser(SysUserDTO dto) {
        if (dto.getUsername() == null || dto.getUsername().isBlank()) throw new IllegalArgumentException("用户名不能为空");
        if (userMapper.selectByUsername(dto.getUsername()) != null) throw new IllegalArgumentException("用户名已存在");
        if (!"ADMIN".equals(dto.getRole()) && !"MERCHANT".equals(dto.getRole())) {
            throw new IllegalArgumentException("角色仅支持 ADMIN 或 MERCHANT");
        }
        User u = new User();
        u.setUsername(dto.getUsername());
        u.setPassword(PasswordEncoderUtil.encode(dto.getPassword() == null ? "123456" : dto.getPassword()));
        u.setNickName(dto.getNickName() == null ? dto.getUsername() : dto.getNickName());
        u.setRole(dto.getRole());
        u.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        userMapper.insert(u);
    }

    public void updateUser(SysUserDTO dto) {
        User u = userMapper.selectById(dto.getId());
        if (u == null) throw new IllegalArgumentException("用户不存在");
        if (dto.getNickName() != null) u.setNickName(dto.getNickName());
        if (dto.getRole() != null) {
            if (!"ADMIN".equals(dto.getRole()) && !"MERCHANT".equals(dto.getRole())) {
                throw new IllegalArgumentException("角色仅支持 ADMIN 或 MERCHANT");
            }
            u.setRole(dto.getRole());
        }
        if (dto.getStatus() != null) u.setStatus(dto.getStatus());
        userMapper.update(u);
    }

    public void toggleStatus(Long id, Integer status) {
        userMapper.updateStatus(id, status);
    }

    public void resetPassword(Long id) {
        userMapper.updatePassword(id, PasswordEncoderUtil.encode("123456"));
    }
}
