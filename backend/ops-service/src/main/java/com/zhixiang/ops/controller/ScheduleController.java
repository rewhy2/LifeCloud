package com.zhixiang.ops.controller;

import com.zhixiang.common.Result;
import com.zhixiang.ops.entity.Schedule;
import com.zhixiang.ops.service.ScheduleService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public Result<List<Schedule>> list(@RequestParam(required = false) String date,
                                        @RequestParam(required = false) String start,
                                        @RequestParam(required = false) String end) {
        if (date != null) return Result.success(scheduleService.byDate(LocalDate.parse(date)));
        if (start != null && end != null)
            return Result.success(scheduleService.range(LocalDate.parse(start), LocalDate.parse(end)));
        return Result.success(scheduleService.byDate(LocalDate.now()));
    }

    @PostMapping
    public Result<Schedule> add(@RequestParam Long employeeId,
                                @RequestParam String date,
                                @RequestParam String shift) {
        return Result.success(scheduleService.add(employeeId, LocalDate.parse(date), shift));
    }

    @DeleteMapping("/{date}")
    public Result<Void> clear(@PathVariable String date) {
        scheduleService.clearDay(LocalDate.parse(date));
        return Result.success();
    }
}
