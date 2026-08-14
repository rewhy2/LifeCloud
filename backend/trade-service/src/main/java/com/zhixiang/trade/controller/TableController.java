package com.zhixiang.trade.controller;

import com.zhixiang.common.Result;
import com.zhixiang.trade.entity.TableInfo;
import com.zhixiang.trade.service.TableService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tables")
public class TableController {

    private final TableService tableService;

    public TableController(TableService tableService) {
        this.tableService = tableService;
    }

    @GetMapping
    public Result<List<TableInfo>> list(@RequestParam(required = false) String status,
                                         @RequestParam(required = false) String area) {
        return Result.success(tableService.list(status, area));
    }

    @PostMapping
    public Result<Void> save(@RequestBody TableInfo t) {
        tableService.save(t);
        return Result.success();
    }

    @PutMapping("/{no}/status")
    public Result<Void> changeStatus(@PathVariable String no, @RequestParam String status) {
        tableService.changeStatus(no, status);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        tableService.delete(id);
        return Result.success();
    }
}
