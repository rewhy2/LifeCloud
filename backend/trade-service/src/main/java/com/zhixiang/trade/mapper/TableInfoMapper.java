package com.zhixiang.trade.mapper;

import com.zhixiang.trade.entity.TableInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface TableInfoMapper {
    List<TableInfo> selectAll(@Param("status") String status, @Param("area") String area);
    TableInfo selectById(Long id);
    int insert(TableInfo t);
    int update(TableInfo t);
    @Update("UPDATE table_info SET status = #{status} WHERE no = #{no}")
    int updateStatus(@Param("no") String no, @Param("status") String status);
    int delete(Long id);
}
