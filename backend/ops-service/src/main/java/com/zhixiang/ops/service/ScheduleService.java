package com.zhixiang.ops.service;

import com.zhixiang.ops.entity.Employee;
import com.zhixiang.ops.entity.Schedule;
import com.zhixiang.ops.mapper.EmployeeMapper;
import com.zhixiang.ops.mapper.ScheduleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleMapper scheduleMapper;
    private final EmployeeMapper employeeMapper;

    public ScheduleService(ScheduleMapper scheduleMapper, EmployeeMapper employeeMapper) {
        this.scheduleMapper = scheduleMapper;
        this.employeeMapper = employeeMapper;
    }

    public List<Schedule> byDate(LocalDate date) {
        return scheduleMapper.selectByDate(date);
    }

    public List<Schedule> range(LocalDate start, LocalDate end) {
        return scheduleMapper.selectRange(start, end);
    }

    @Transactional
    public Schedule add(Long employeeId, LocalDate date, String shift) {
        Employee emp = employeeMapper.selectById(employeeId);
        if (emp == null) throw new IllegalArgumentException("员工不存在");
        Schedule s = new Schedule();
        s.setEmployeeId(employeeId);
        s.setEmployeeName(emp.getName());
        s.setWorkDate(date);
        s.setShift(shift);
        scheduleMapper.insert(s);
        return s;
    }

    @Transactional
    public void clearDay(LocalDate date) {
        scheduleMapper.deleteByDate(date, null);
    }
}
