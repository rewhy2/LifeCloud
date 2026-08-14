package com.zhixiang.ops.mapper;

import com.zhixiang.ops.entity.Schedule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ScheduleMapper {
    List<Schedule> selectByDate(@Param("date") LocalDate date);
    List<Schedule> selectRange(@Param("start") LocalDate start, @Param("end") LocalDate end);
    int insert(Schedule s);
    int deleteByDate(@Param("date") LocalDate date, @Param("employeeId") Long employeeId);
}
