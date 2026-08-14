package com.zhixiang.trade.mapper;

import com.zhixiang.trade.entity.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductMapper {
    List<Product> selectAll(@Param("name") String name, @Param("categoryId") Long categoryId, @Param("status") Integer status);
    Product selectById(Long id);
    int insert(Product product);
    int update(Product product);

    @Update("UPDATE product SET status = #{status} WHERE id = #{id}")
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    @Update("UPDATE product SET status = #{status} WHERE category_id = #{categoryId}")
    int updateStatusByCategory(@Param("categoryId") Long categoryId, @Param("status") Integer status);
}
