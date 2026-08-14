package com.zhixiang.trade.controller;

import com.zhixiang.common.Result;
import com.zhixiang.trade.entity.Product;
import com.zhixiang.trade.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Result<List<Product>> list(@RequestParam(required = false) String name,
                                       @RequestParam(required = false) Long categoryId,
                                       @RequestParam(required = false) Integer status) {
        return Result.success(productService.list(name, categoryId, status));
    }

    @GetMapping("/public")
    public Result<List<Product>> publicList(@RequestParam(required = false) Long categoryId,
                                            @RequestParam(required = false) String keyword) {
        return Result.success(productService.list(keyword, categoryId, 1));
    }

    @GetMapping("/{id}")
    public Result<Product> get(@PathVariable Long id) {
        return Result.success(productService.get(id));
    }

    @PostMapping
    public Result<Void> save(@RequestBody Product product) {
        productService.save(product);
        return Result.success();
    }

    @PutMapping("/{id}/status")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.success();
    }
}
