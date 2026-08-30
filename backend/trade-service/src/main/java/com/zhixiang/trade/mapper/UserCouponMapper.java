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

    @Select("SELECT * FROM user_coupon WHERE id = #{id}")
    UserCoupon selectById(@Param("id") Long id);

    /** 标记优惠券已使用（仅当仍为未使用状态时生效，返回受影响行数）。 */
    @Update("UPDATE user_coupon SET status = 2 WHERE id = #{id} AND status = 1")
    int markUsed(@Param("id") Long id);

    @Select("SELECT * FROM user_coupon WHERE id = #{id} AND user_id = #{userId} AND status = 1")
    UserCoupon selectOwnedUnused(@Param("id") Long id, @Param("userId") Long userId);
}
