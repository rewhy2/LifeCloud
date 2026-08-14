package com.zhixiang.trade.mapper;

import com.zhixiang.trade.entity.Coupon;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface CouponMapper {
    List<Coupon> selectAll();
    Coupon selectById(Long id);
    int insert(Coupon c);
    int update(Coupon c);
    @Update("UPDATE coupon SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);
    @Update("UPDATE coupon SET received = received + 1 WHERE id = #{id}")
    int incReceived(Long id);
    @Update("UPDATE coupon SET used = used + 1 WHERE id = #{id}")
    int incUsed(Long id);
    int delete(Long id);
}
