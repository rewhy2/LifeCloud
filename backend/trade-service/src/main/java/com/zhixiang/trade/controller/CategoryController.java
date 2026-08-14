package com.zhixiang.trade.controller;

import com.zhixiang.common.Result;
import com.zhixiang.trade.entity.Category;
import com.zhixiang.trade.mapper.CategoryMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryMapper categoryMapper;

    public CategoryController(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @GetMapping
    public Result<List<Category>> list() {
        return Result.success(categoryMapper.selectAll());
    }

    @GetMapping("/public")
    public Result<List<Category>> publicList() {
        return Result.success(categoryMapper.selectAll());
    }

    @PostMapping
    public Result<Void> save(@RequestBody Category category) {
        if (category.getId() == null) {
            categoryMapper.insert(category);
        } else {
            categoryMapper.update(category);
        }
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryMapper.delete(id);
        return Result.success();
    }
}
