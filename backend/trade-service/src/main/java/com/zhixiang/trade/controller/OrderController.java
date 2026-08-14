package com.zhixiang.trade.controller;

import com.zhixiang.common.Result;
import com.zhixiang.trade.dto.CreateOrderRequest;
import com.zhixiang.trade.entity.Order;
import com.zhixiang.trade.service.OrderService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Result<Order> create(@RequestBody CreateOrderRequest req) {
        return Result.success(orderService.create(req));
    }

    @PostMapping("/{orderNo}/pay")
    public Result<Order> pay(@PathVariable String orderNo) {
        return Result.success(orderService.pay(orderNo));
    }

    @PostMapping("/{orderNo}/refund")
    public Result<Order> refund(@PathVariable String orderNo) {
        return Result.success(orderService.refund(orderNo));
    }

    @GetMapping
    public Result<List<Order>> list(@RequestParam(required = false) String status,
                                     @RequestParam(required = false) String start,
                                     @RequestParam(required = false) String end) {
        return Result.success(orderService.list(status, start, end));
    }

    @GetMapping("/{id}")
    public Result<Order> detail(@PathVariable Long id) {
        return Result.success(orderService.detail(id));
    }
}
