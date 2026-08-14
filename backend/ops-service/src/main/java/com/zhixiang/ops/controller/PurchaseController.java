package com.zhixiang.ops.controller;

import com.zhixiang.common.Result;
import com.zhixiang.ops.dto.CreatePurchaseRequest;
import com.zhixiang.ops.entity.Purchase;
import com.zhixiang.ops.service.PurchaseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/purchases")
public class PurchaseController {

    private final PurchaseService purchaseService;

    public PurchaseController(PurchaseService purchaseService) {
        this.purchaseService = purchaseService;
    }

    @PostMapping
    public Result<Purchase> create(@RequestBody CreatePurchaseRequest req) {
        return Result.success(purchaseService.create(req));
    }

    @PostMapping("/{orderNo}/stockin")
    public Result<Purchase> stockIn(@PathVariable String orderNo) {
        return Result.success(purchaseService.stockIn(orderNo));
    }

    @GetMapping
    public Result<List<Purchase>> list(@RequestParam(required = false) String status) {
        return Result.success(purchaseService.list(status));
    }

    @GetMapping("/{orderNo}")
    public Result<Purchase> detail(@PathVariable String orderNo) {
        return Result.success(purchaseService.detail(orderNo));
    }
}
