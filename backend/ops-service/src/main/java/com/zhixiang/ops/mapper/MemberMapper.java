package com.zhixiang.ops.mapper;

import com.zhixiang.ops.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MemberMapper {
    List<Member> selectAll(@Param("name") String name, @Param("level") String level);
    Member selectById(Long id);
    @Select("SELECT * FROM member WHERE phone = #{phone}")
    Member selectByPhone(@Param("phone") String phone);
    int insert(Member m);
    int update(Member m);
    int delete(Long id);
}
