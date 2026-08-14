package com.zhixiang.trade.controller;

import com.zhixiang.common.LoginUser;
import com.zhixiang.common.Result;
import com.zhixiang.common.UserContext;
import com.zhixiang.trade.dto.CreateOrderRequest;
import com.zhixiang.trade.entity.Order;
import com.zhixiang.trade.entity.UserCoupon;
import com.zhixiang.trade.service.CouponService;
import com.zhixiang.trade.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 顾客端接口（仅 USER 角色可访问，由 RoleInterceptor 拦截）
 */
@RestController
@RequestMapping("/user")
public class UserEndController {

    private final OrderService orderService;
    private final CouponService couponService;

    public UserEndController(OrderService orderService, CouponService couponService) {
        this.orderService = orderService;
        this.couponService = couponService;
    }

    @GetMapping("/my/orders")
    public Result<List<Order>> myOrders() {
        LoginUser u = UserContext.get();
        return Result.success(orderService.myOrders(null, u.getUsername()));
    }

    @PostMapping("/orders")
    public Result<Order> createOrder(@RequestBody CreateOrderRequest req) {
        UserContext.get();
        Order order = orderService.create(req);
        return Result.success(order);
    }

    @GetMapping("/coupons")
    public Result<?> availableCoupons() {
        return Result.success(couponService.list());
    }

    @PostMapping("/coupons/{id}/claim")
    public Result<Void> claim(@PathVariable Long id) {
        couponService.grant(id);
        return Result.success();
    }

    @GetMapping("/my/coupons")
    public Result<List<UserCoupon>> myCoupons() {
        return Result.success(couponService.myCoupons());
    }
}
