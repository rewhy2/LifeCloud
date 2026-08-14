package com.zhixiang.trade.controller;

import com.zhixiang.common.Result;
import com.zhixiang.trade.service.BusinessStatusService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/business")
public class BusinessStatusController {

    private final BusinessStatusService businessStatusService;

    public BusinessStatusController(BusinessStatusService businessStatusService) {
        this.businessStatusService = businessStatusService;
    }

    @GetMapping("/status")
    public Result<Map<String, Object>> status() {
        String s = businessStatusService.getStatus();
        Map<String, Object> m = new HashMap<>();
        m.put("status", s);
        m.put("label", "OPEN".equals(s) ? "营业中" : "已打烊");
        return Result.success(m);
    }

    @PostMapping("/status")
    public Result<Map<String, Object>> setStatus(@RequestParam String status) {
        return Result.success(businessStatusService.setStatus(status));
    }
}
