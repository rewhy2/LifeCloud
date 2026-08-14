package com.zhixiang.trade.controller;

import com.zhixiang.common.Result;
import com.zhixiang.trade.entity.Coupon;
import com.zhixiang.trade.service.CouponService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public Result<List<Coupon>> list() {
        return Result.success(couponService.list());
    }

    @PostMapping
    public Result<Void> save(@RequestBody Coupon c) {
        couponService.save(c);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> toggle(@PathVariable Long id, @RequestParam Integer status) {
        couponService.toggleStatus(id, status);
        return Result.success();
    }

    @PostMapping("/{id}/grant")
    public Result<Void> grant(@PathVariable Long id) {
        couponService.grant(id);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        couponService.delete(id);
        return Result.success();
    }
}
