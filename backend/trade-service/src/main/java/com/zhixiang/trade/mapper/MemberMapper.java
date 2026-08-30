package com.zhixiang.trade.mapper;

import com.zhixiang.trade.entity.Member;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface MemberMapper {

    @Select("SELECT * FROM member WHERE phone = #{phone}")
    Member selectByPhone(@Param("phone") String phone);

    /** 支付成功后累加累计消费与积分（1 元 = 1 积分）。 */
    @Update("UPDATE member SET total_spend = COALESCE(total_spend,0) + #{amount}, " +
            "point = COALESCE(point,0) + #{points} WHERE id = #{id}")
    int accumulate(@Param("id") Long id,
                  @Param("amount") java.math.BigDecimal amount,
                  @Param("points") Integer points);
}
