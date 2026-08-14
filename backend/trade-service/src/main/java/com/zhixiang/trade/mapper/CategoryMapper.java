package com.zhixiang.trade.mapper;

import com.zhixiang.trade.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {
    List<Category> selectAll();
    int insert(Category category);
    int update(Category category);
    int delete(Long id);
}
