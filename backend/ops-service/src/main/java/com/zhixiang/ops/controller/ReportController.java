package com.zhixiang.ops.controller;

import com.zhixiang.common.Result;
import com.zhixiang.ops.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/today")
    public Result<Map<String, Object>> today() {
        return Result.success(reportService.todayOverview());
    }

    @GetMapping("/category")
    public Result<List<Map<String, Object>>> byCategory(@RequestParam(required = false) String date) {
        return Result.success(reportService.salesByCategory(date == null ? java.time.LocalDate.now().toString() : date));
    }

    @GetMapping("/trend")
    public Result<List<Map<String, Object>>> trend(@RequestParam(defaultValue = "7") int days) {
        return Result.success(reportService.revenueTrend(days));
    }

    @GetMapping("/top")
    public Result<List<Map<String, Object>>> top(@RequestParam(defaultValue = "10") int limit) {
        return Result.success(reportService.topProducts(limit));
    }

    @GetMapping("/diagnose")
    public Result<Map<String, Object>> diagnose() {
        return Result.success(reportService.diagnose());
    }
}
