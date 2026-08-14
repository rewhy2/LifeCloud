package com.zhixiang.trade.mapper;

import com.zhixiang.trade.entity.UserCoupon;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserCouponMapper {

    @Insert("INSERT INTO user_coupon(user_id, coupon_id, coupon_name, type, threshold, value, status) " +
            "VALUES(#{userId}, #{couponId}, #{couponName}, #{type}, #{threshold}, #{value}, 1)")
    int insert(UserCoupon uc);

    @Select("SELECT * FROM user_coupon WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<UserCoupon> selectByUser(@Param("userId") Long userId);

    @Select("SELECT COUNT(1) FROM user_coupon WHERE user_id = #{userId} AND coupon_id = #{couponId}")
    int countByUserAndCoupon(@Param("userId") Long userId, @Param("couponId") Long couponId);
}
