package com.zhixiang.ops.controller;

import com.zhixiang.common.Result;
import com.zhixiang.ops.entity.Inventory;
import com.zhixiang.ops.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public Result<List<Inventory>> list(@RequestParam(required = false) String name,
                                         @RequestParam(required = false) Boolean low) {
        return Result.success(inventoryService.list(name, low));
    }

    @GetMapping("/low/count")
    public Result<Integer> lowCount() {
        return Result.success(inventoryService.countLowStock());
    }

    @PostMapping
    public Result<Void> save(@RequestBody Inventory i) {
        inventoryService.save(i);
        return Result.success();
    }

    @PostMapping("/{id}/adjust")
    public Result<Void> adjust(@PathVariable Long id, @RequestParam BigDecimal delta) {
        inventoryService.adjust(id, delta);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        inventoryService.delete(id);
        return Result.success();
    }
}
