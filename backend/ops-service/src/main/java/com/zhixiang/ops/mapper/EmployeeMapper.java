package com.zhixiang.ops.mapper;

import com.zhixiang.ops.entity.Employee;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface EmployeeMapper {
    List<Employee> selectAll();
    Employee selectById(Long id);
    int insert(Employee e);
    int update(Employee e);
    int delete(Long id);
}
