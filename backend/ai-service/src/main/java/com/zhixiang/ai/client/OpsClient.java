package com.zhixiang.ai.client;

import com.zhixiang.ai.client.fallback.OpsClientFallback;
import com.zhixiang.ai.dto.InventoryDTO;
import com.zhixiang.ai.dto.MemberDTO;
import com.zhixiang.ai.dto.ScheduleDTO;
import com.zhixiang.ai.dto.SupplierDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 运营域(ops-service) Feign 客户端，带 Sentinel 熔断降级
 */
@FeignClient(name = "ops-service", fallback = OpsClientFallback.class)
public interface OpsClient {

    @GetMapping("/report/today")
    Map<String, Object> reportToday();

    @GetMapping("/report/category")
    List<Map<String, Object>> reportCategory(@RequestParam("date") String date);

    @GetMapping("/report/trend")
    List<Map<String, Object>> reportTrend(@RequestParam("days") int days);

    @GetMapping("/report/diagnose")
    Map<String, Object> diagnose();

    @GetMapping("/members")
    List<MemberDTO> members(@RequestParam(value = "name", required = false) String name,
                            @RequestParam(value = "level", required = false) String level);

    @GetMapping("/inventory")
    List<InventoryDTO> inventory(@RequestParam(value = "low", required = false) Boolean low);

    @GetMapping("/suppliers/{id}")
    SupplierDTO supplier(@PathVariable("id") Long id);

    @GetMapping("/schedules")
    List<ScheduleDTO> schedules(@RequestParam(value = "date", required = false) String date,
                                @RequestParam(value = "start", required = false) String start,
                                @RequestParam(value = "end", required = false) String end);
}
