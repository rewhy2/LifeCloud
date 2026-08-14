package com.zhixiang.ai.tools;

import com.zhixiang.ai.client.OpsClient;
import com.zhixiang.ai.client.TradeClient;
import com.zhixiang.ai.dto.CouponDTO;
import com.zhixiang.ai.dto.ProductDTO;
import com.zhixiang.ai.dto.TableInfoDTO;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * AI 经营工具箱：通过 OpenFeign 调用 trade-service / ops-service，封装为 Function Calling 工具。
 */
@Component
public class BusinessTools {

    private final OpsClient opsClient;
    private final TradeClient tradeClient;

    public BusinessTools(OpsClient opsClient, TradeClient tradeClient) {
        this.opsClient = opsClient;
        this.tradeClient = tradeClient;
    }

    @Tool("查询当日经营概览：营收、有效订单数、退款数、客单价等关键指标")
    public String getTodayOverview() {
        return mapToText(opsClient.reportToday());
    }

    @Tool("查询指定日期（yyyy-MM-dd）各商品分类的销售额与销量，用于分析品类结构")
    public String getSalesByCategory(@P("日期，格式 yyyy-MM-dd，缺省为今天") String date) {
        if (date == null || date.isBlank()) date = java.time.LocalDate.now().toString();
        List<Map<String, Object>> list = opsClient.reportCategory(date);
        if (list.isEmpty()) return "该日期暂无已支付订单数据。";
        StringBuilder sb = new StringBuilder("分类销售（" + date + "）：\n");
        for (Map<String, Object> m : list) {
            sb.append("- ").append(m.get("category")).append("：销售额 ").append(m.get("amount"))
              .append(" 元，销量 ").append(m.get("qty")).append(" 件\n");
        }
        return sb.toString();
    }

    @Tool("查询近 N 日（默认7日）营收趋势，用于判断经营走势")
    public String getRevenueTrend(@P("天数，默认7") Integer days) {
        if (days == null || days <= 0) days = 7;
        List<Map<String, Object>> list = opsClient.reportTrend(days);
        StringBuilder sb = new StringBuilder(days + "日营收趋势：\n");
        for (Map<String, Object> m : list) {
            sb.append("- ").append(m.get("date")).append("：营收 ").append(m.get("revenue"))
              .append(" 元\n");
        }
        return sb.toString();
    }

    @Tool("对当日核心经营指标进行交叉诊断，输出异常预警与营销调优建议")
    public String diagnoseBusiness() {
        Map<String, Object> d = opsClient.diagnose();
        StringBuilder sb = new StringBuilder("经营诊断：\n");
        for (Map.Entry<String, Object> e : d.entrySet()) {
            if ("suggestions".equals(e.getKey()) || "warnings".equals(e.getKey())) continue;
            sb.append("- ").append(e.getKey()).append("：").append(e.getValue()).append("\n");
        }
        sb.append("【建议】\n");
        Object sug = d.get("suggestions");
        if (sug instanceof List) for (Object s : (List<?>) sug) sb.append("• ").append(s).append("\n");
        return sb.toString();
    }

    @Tool("读取当前营业状态（OPEN 营业中 / CLOSED 已打烊）")
    public String getBusinessStatus() {
        Map<String, Object> m = tradeClient.businessStatus();
        String s = String.valueOf(m.get("status"));
        return "当前营业状态：" + ("OPEN".equals(s) ? "营业中(OPEN)" : "已打烊(CLOSED)");
    }

    @Tool("一键修改营业状态，status 传 OPEN 表示开业，CLOSED 表示打烊")
    public String setBusinessStatus(@P("营业状态：OPEN 或 CLOSED") String status) {
        try {
            Map<String, Object> r = tradeClient.setBusinessStatus(status);
            return "已成功将营业状态切换为：" + r.get("label") + "（" + r.get("status") + "）";
        } catch (Exception e) {
            return "操作失败：" + e.getMessage();
        }
    }

    @Tool("查询在售/停售商品列表，可指定分类或名称过滤")
    public String listProducts(@P("分类ID，可选") Long categoryId,
                               @P("状态 1在售 0停售，可选") Integer status,
                               @P("名称关键字，可选") String name) {
        List<ProductDTO> list = tradeClient.listProducts(name, categoryId, status);
        if (list.isEmpty()) return "未查询到商品。";
        StringBuilder sb = new StringBuilder("商品列表（共 " + list.size() + " 项）：\n");
        for (ProductDTO p : list) {
            sb.append("- [").append(p.getId()).append("] ").append(p.getName())
              .append(" ￥").append(p.getPrice()).append(" ")
              .append(p.getStatus() != null && p.getStatus() == 1 ? "在售" : "停售").append("\n");
        }
        return sb.toString();
    }

    @Tool("查询桌台使用情况，返回空闲/占用/预订数量，用于翻台率与接待能力评估")
    public String tableStatus() {
        List<TableInfoDTO> all = tradeClient.tables(null, null);
        long free = all.stream().filter(t -> "FREE".equals(t.getStatus())).count();
        long occ = all.stream().filter(t -> "OCCUPIED".equals(t.getStatus())).count();
        long res = all.stream().filter(t -> "RESERVED".equals(t.getStatus())).count();
        return String.format("桌台共 %d 张：空闲 %d、占用 %d、预订 %d。翻台空间：%d 张可用。",
                all.size(), free, occ, res, free);
    }

    @Tool("查询优惠券投放与核销情况，用于评估营销活动效果")
    public String couponReport() {
        List<CouponDTO> list = tradeClient.coupons();
        if (list.isEmpty()) return "暂无优惠券。";
        StringBuilder sb = new StringBuilder("优惠券效果：\n");
        for (CouponDTO c : list) {
            double rate = c.getReceived() != null && c.getReceived() > 0
                    ? (double) (c.getUsed() == null ? 0 : c.getUsed()) / c.getReceived() * 100 : 0;
            sb.append("- ").append(c.getName()).append("：发放 ").append(c.getTotal())
              .append("，领取 ").append(c.getReceived()).append("，核销 ").append(c.getUsed())
              .append(String.format("（核销率 %.0f%%）", rate))
              .append(c.getStatus() != null && c.getStatus() == 1 ? "" : "【已停用】").append("\n");
        }
        return sb.toString();
    }

    @Tool("向某优惠券发放一张（用于唤醒沉默会员），返回最新领取数")
    public String grantCoupon(@P("优惠券ID") Long couponId) {
        try {
            tradeClient.grantCoupon(couponId);
            CouponDTO c = tradeClient.coupon(couponId);
            return "已发放优惠券【" + (c == null ? couponId : c.getName()) + "】，当前领取数：" + (c == null ? "?" : c.getReceived());
        } catch (Exception e) {
            return "发放失败：" + e.getMessage();
        }
    }

    private String mapToText(Map<String, Object> m) {
        StringBuilder sb = new StringBuilder("当日经营概览：\n");
        for (Map.Entry<String, Object> e : m.entrySet()) {
            sb.append("- ").append(e.getKey()).append("：").append(e.getValue()).append("\n");
        }
        return sb.toString();
    }
}
