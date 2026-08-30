package com.zhixiang.trade.mapper;

import com.zhixiang.trade.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface InventoryMapper {

    @Select("SELECT * FROM inventory WHERE id = #{id}")
    Inventory selectById(@Param("id") Long id);

    /** 原子扣减库存：仅当余量充足时扣减，返回受影响行数（0 表示库存不足）。 */
    @Update("UPDATE inventory SET quantity = quantity - #{delta} " +
            "WHERE id = #{id} AND quantity >= #{delta}")
    int deduct(@Param("id") Long id, @Param("delta") java.math.BigDecimal delta);

    /** 回补库存（退款场景）。 */
    @Update("UPDATE inventory SET quantity = quantity + #{delta} WHERE id = #{id}")
    int addBack(@Param("id") Long id, @Param("delta") java.math.BigDecimal delta);
}
