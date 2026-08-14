package com.zhixiang.ai.client.fallback;

import com.zhixiang.ai.client.OpsClient;
import com.zhixiang.ai.dto.InventoryDTO;
import com.zhixiang.ai.dto.MemberDTO;
import com.zhixiang.ai.dto.ScheduleDTO;
import com.zhixiang.ai.dto.SupplierDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class OpsClientFallback implements OpsClient {

    private static final Logger log = LoggerFactory.getLogger(OpsClientFallback.class);

    @Override public Map<String, Object> reportToday() {
        log.warn("[OpsClient] 熔断降级: reportToday");
        return Map.of("revenue", 0, "orderCount", 0, "degraded", true);
    }
    @Override public List<Map<String, Object>> reportCategory(String date) {
        return Collections.emptyList();
    }
    @Override public List<Map<String, Object>> reportTrend(int days) {
        return Collections.emptyList();
    }
    @Override public Map<String, Object> diagnose() {
        return Map.of("suggestions", List.of("运营服务暂时不可用，请稍后重试"));
    }
    @Override public List<MemberDTO> members(String name, String level) {
        return Collections.emptyList();
    }
    @Override public List<InventoryDTO> inventory(Boolean low) {
        return Collections.emptyList();
    }
    @Override public SupplierDTO supplier(Long id) {
        return null;
    }
    @Override public List<ScheduleDTO> schedules(String date, String start, String end) {
        return Collections.emptyList();
    }
}
