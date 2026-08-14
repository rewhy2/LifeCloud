package com.zhixiang.ai.client.fallback;

import com.zhixiang.ai.client.TradeClient;
import com.zhixiang.ai.dto.CouponDTO;
import com.zhixiang.ai.dto.ProductDTO;
import com.zhixiang.ai.dto.TableInfoDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class TradeClientFallback implements TradeClient {

    private static final Logger log = LoggerFactory.getLogger(TradeClientFallback.class);

    @Override public List<ProductDTO> listProducts(String name, Long categoryId, Integer status) {
        log.warn("[TradeClient] 熔断降级: listProducts");
        return Collections.emptyList();
    }
    @Override public Map<String, Object> businessStatus() {
        return Map.of("status", "UNKNOWN", "label", "状态未知(服务降级)");
    }
    @Override public Map<String, Object> setBusinessStatus(String status) {
        return Map.of("status", "UNKNOWN", "label", "切换失败(服务降级)");
    }
    @Override public List<TableInfoDTO> tables(String status, String area) {
        return Collections.emptyList();
    }
    @Override public List<CouponDTO> coupons() {
        return Collections.emptyList();
    }
    @Override public CouponDTO coupon(Long id) {
        return null;
    }
    @Override public void grantCoupon(Long id) { }
}
