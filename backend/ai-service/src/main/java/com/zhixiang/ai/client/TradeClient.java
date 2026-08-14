package com.zhixiang.ai.client;

import com.zhixiang.ai.client.fallback.TradeClientFallback;
import com.zhixiang.ai.dto.CouponDTO;
import com.zhixiang.ai.dto.ProductDTO;
import com.zhixiang.ai.dto.TableInfoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 交易域(trade-service) Feign 客户端，带 Sentinel 熔断降级
 */
@FeignClient(name = "trade-service", fallback = TradeClientFallback.class)
public interface TradeClient {

    @GetMapping("/products")
    List<ProductDTO> listProducts(@RequestParam(value = "name", required = false) String name,
                                  @RequestParam(value = "categoryId", required = false) Long categoryId,
                                  @RequestParam(value = "status", required = false) Integer status);

    @GetMapping("/business/status")
    Map<String, Object> businessStatus();

    @PostMapping("/business/status")
    Map<String, Object> setBusinessStatus(@RequestParam("status") String status);

    @GetMapping("/tables")
    List<TableInfoDTO> tables(@RequestParam(value = "status", required = false) String status,
                              @RequestParam(value = "area", required = false) String area);

    @GetMapping("/coupons")
    List<CouponDTO> coupons();

    @GetMapping("/coupons/{id}")
    CouponDTO coupon(@PathVariable("id") Long id);

    @PostMapping("/coupons/{id}/grant")
    void grantCoupon(@PathVariable("id") Long id);
}
