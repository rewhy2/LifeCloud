package com.zhixiang.ops.mapper;

import com.zhixiang.ops.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SupplierMapper {
    List<Supplier> selectAll();
    Supplier selectById(Long id);
    int insert(Supplier s);
    int update(Supplier s);
    int delete(Long id);
}
