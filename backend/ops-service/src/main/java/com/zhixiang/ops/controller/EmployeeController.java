package com.zhixiang.ops.controller;

import com.zhixiang.common.Result;
import com.zhixiang.ops.entity.Employee;
import com.zhixiang.ops.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public Result<List<Employee>> list() {
        return Result.success(employeeService.list());
    }

    @PostMapping
    public Result<Void> save(@RequestBody Employee e) {
        employeeService.save(e);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return Result.success();
    }
}
