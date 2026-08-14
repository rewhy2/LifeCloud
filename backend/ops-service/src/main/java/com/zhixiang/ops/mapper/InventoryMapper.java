package com.zhixiang.ops.mapper;

import com.zhixiang.ops.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface InventoryMapper {
    List<Inventory> selectAll(@Param("name") String name, @Param("low") Boolean low);
    Inventory selectById(Long id);
    int insert(Inventory i);
    int update(Inventory i);
    @Update("UPDATE inventory SET quantity = quantity + #{delta} WHERE id = #{id}")
    int addQuantity(@Param("id") Long id, @Param("delta") BigDecimal delta);
    @Select("SELECT COUNT(*) FROM inventory WHERE quantity < threshold")
    int countLowStock();
    int delete(Long id);
}
