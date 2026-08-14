package com.zhixiang.auth.mapper;

import com.zhixiang.auth.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM sys_user WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);

    @Select("SELECT * FROM sys_user WHERE id = #{id}")
    User selectById(@Param("id") Long id);

    @Select("<script>SELECT * FROM sys_user" +
            "<where>" +
            "<if test='role != null and role != \"\"'> AND role = #{role}</if>" +
            "<if test='keyword != null and keyword != \"\"'> AND (username LIKE CONCAT('%',#{keyword},'%') OR nick_name LIKE CONCAT('%',#{keyword},'%'))</if>" +
            "</where> ORDER BY create_time DESC</script>")
    List<User> selectAll(@Param("role") String role, @Param("keyword") String keyword);

    @Insert("INSERT INTO sys_user(username, password, nick_name, role, status) " +
            "VALUES(#{username}, #{password}, #{nickName}, #{role}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    @Update("UPDATE sys_user SET nick_name = #{nickName}, role = #{role}, status = #{status} WHERE id = #{id}")
    int update(User user);

    @Update("UPDATE sys_user SET password = #{password} WHERE id = #{id}")
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @Update("UPDATE sys_user SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
}
