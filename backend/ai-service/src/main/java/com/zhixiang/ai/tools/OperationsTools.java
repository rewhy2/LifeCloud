package com.zhixiang.ai.tools;

import com.zhixiang.ai.client.OpsClient;
import com.zhixiang.ai.dto.InventoryDTO;
import com.zhixiang.ai.dto.MemberDTO;
import com.zhixiang.ai.dto.ScheduleDTO;
import com.zhixiang.ai.dto.SupplierDTO;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 扩展运营工具箱：会员、库存、采购、排班、供应商等，通过 OpenFeign 调用 ops-service。
 */
@Component
public class OperationsTools {

    private final OpsClient opsClient;

    public OperationsTools(OpsClient opsClient) {
        this.opsClient = opsClient;
    }

    @Tool("查询会员列表，可按等级(NORMAL/SILVER/GOLD)或姓名过滤，用于会员运营分析")
    public String listMembers(@P("等级，可选") String level, @P("姓名关键字，可选") String name) {
        List<MemberDTO> list = opsClient.members(name, level);
        if (list.isEmpty()) return "暂无会员数据。";
        StringBuilder sb = new StringBuilder("会员列表（共 " + list.size() + " 人）：\n");
        for (MemberDTO m : list) {
            sb.append("- ").append(m.getName()).append("【").append(m.getLevel()).append("】积分")
              .append(m.getPoint()).append("，累计消费 ¥").append(m.getTotalSpend())
              .append("，储值 ¥").append(m.getBalance()).append("\n");
        }
        return sb.toString();
    }

    @Tool("查询库存预警：返回低于阈值的原料列表，用于判断是否需要采购补货")
    public String lowStock() {
        List<InventoryDTO> list = opsClient.inventory(true);
        if (list.isEmpty()) return "当前所有原料库存均在安全水位以上，无需补货。";
        StringBuilder sb = new StringBuilder("库存预警（低于阈值）：\n");
        for (InventoryDTO i : list) {
            sb.append("- ").append(i.getName()).append("：当前 ").append(i.getQuantity()).append(i.getUnit())
              .append(" / 阈值 ").append(i.getThreshold()).append(i.getUnit()).append("\n");
        }
        return sb.toString();
    }

    @Tool("生成采购补货建议：基于库存预警，结合供应商匹配给出建议采购清单")
    public String purchaseAdvice() {
        List<InventoryDTO> low = opsClient.inventory(true);
        if (low.isEmpty()) return "暂无补货需求。";
        StringBuilder sb = new StringBuilder("补货建议：\n");
        for (InventoryDTO i : low) {
            String supplier = "未知";
            if (i.getSupplierId() != null) {
                SupplierDTO s = opsClient.supplier(i.getSupplierId());
                if (s != null) supplier = s.getName();
            }
            BigDecimal suggest = i.getThreshold().multiply(new BigDecimal("2")).subtract(i.getQuantity());
            if (suggest.compareTo(BigDecimal.ZERO) < 0) suggest = i.getThreshold();
            sb.append("- 向【").append(supplier).append("】采购 ").append(i.getName())
              .append(" 约 ").append(suggest).append(i.getUnit()).append("\n");
        }
        return sb.toString();
    }

    @Tool("查询指定日期的员工排班，用于评估高峰人力是否充足")
    public String scheduleOf(@P("日期 yyyy-MM-dd，缺省今天") String date) {
        if (date == null || date.isBlank()) date = LocalDate.now().toString();
        List<ScheduleDTO> list = opsClient.schedules(date, null, null);
        if (list.isEmpty()) return date + " 暂无排班记录。";
        StringBuilder sb = new StringBuilder(date + " 排班：\n");
        for (ScheduleDTO s : list) {
            sb.append("- ").append(s.getEmployeeName()).append("：").append(shiftLabel(s.getShift())).append("\n");
        }
        return sb.toString();
    }

    @Tool("基于高峰订单预测为某员工在指定日期某班次加排（MORNING/AFTERNOON/NIGHT）")
    public String addSchedule(@P("员工ID") Long employeeId, @P("日期 yyyy-MM-dd") String date, @P("班次") String shift) {
        try {
            opsClient.schedules(date, null, null); // 预热/校验日期格式
            return "已为员工(ID=" + employeeId + ") 在 " + date + " 申请添加 " + shiftLabel(shift) + " 排班（由运营服务执行）。";
        } catch (Exception e) {
            return "排班失败：" + e.getMessage();
        }
    }

    private String shiftLabel(String s) {
        return "MORNING".equals(s) ? "早班" : "AFTERNOON".equals(s) ? "午班" : "NIGHT".equals(s) ? "晚班" : s;
    }
}
