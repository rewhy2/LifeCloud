package com.zhixiang.ops.controller;

import com.zhixiang.common.Result;
import com.zhixiang.ops.entity.Supplier;
import com.zhixiang.ops.service.SupplierService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public Result<List<Supplier>> list() {
        return Result.success(supplierService.list());
    }

    @PostMapping
    public Result<Void> save(@RequestBody Supplier s) {
        supplierService.save(s);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return Result.success();
    }
}
