package com.zhixiang.ops.service;

import com.zhixiang.ops.entity.Employee;
import com.zhixiang.ops.mapper.EmployeeMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeMapper employeeMapper;

    public EmployeeService(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    public List<Employee> list() {
        return employeeMapper.selectAll();
    }

    public void save(Employee e) {
        if (e.getId() == null) {
            employeeMapper.insert(e);
        } else {
            employeeMapper.update(e);
        }
    }

    public void delete(Long id) {
        employeeMapper.delete(id);
    }
}
