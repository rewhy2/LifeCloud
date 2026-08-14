package com.zhixiang.common.security;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;

/**
 * 密码工具：统一使用 BCrypt 存储与校验，并兼容存量 MD5 明文密码的平滑迁移。
 *
 * <p>迁移策略：登录校验时，若库中密文为 32 位十六进制（旧 MD5 特征），
 * 且 MD5(rawPassword) 与其匹配，则视为存量用户，调用方应使用 {@link #encode(String)}
 * 重新生成 BCrypt 密文并写回数据库，下次登录即走 BCrypt 分支。</p>
 */
public final class PasswordEncoderUtil {

    private static final PasswordEncoder BCRYPT = new BCryptPasswordEncoder();

    private PasswordEncoderUtil() {
    }

    /** 生成 BCrypt 密文（含随机 salt）。 */
    public static String encode(String rawPassword) {
        return BCRYPT.encode(rawPassword);
    }

    /** 校验明文与密文是否匹配（兼容旧 MD5）。 */
    public static boolean matches(String rawPassword, String stored) {
        if (stored == null || stored.isBlank()) {
            return false;
        }
        if (isLegacyMd5(stored)) {
            return DigestUtils.md5DigestAsHex(rawPassword.getBytes()).equalsIgnoreCase(stored);
        }
        return BCRYPT.matches(rawPassword, stored);
    }

    /** 判断是否为旧 MD5 密文（32 位十六进制）。 */
    public static boolean isLegacyMd5(String stored) {
        return stored != null && stored.matches("^[a-fA-F0-9]{32}$");
    }
}
