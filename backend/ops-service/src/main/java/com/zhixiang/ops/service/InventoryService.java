package com.zhixiang.ops.service;

import com.zhixiang.ops.entity.Inventory;
import com.zhixiang.ops.mapper.InventoryMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class InventoryService {

    private final InventoryMapper inventoryMapper;

    public InventoryService(InventoryMapper inventoryMapper) {
        this.inventoryMapper = inventoryMapper;
    }

    public List<Inventory> list(String name, Boolean low) {
        return inventoryMapper.selectAll(name, low);
    }

    public int countLowStock() {
        return inventoryMapper.countLowStock();
    }

    public void save(Inventory i) {
        if (i.getId() == null) {
            i.setQuantity(i.getQuantity() == null ? BigDecimal.ZERO : i.getQuantity());
            i.setThreshold(i.getThreshold() == null ? BigDecimal.ZERO : i.getThreshold());
            i.setPrice(i.getPrice() == null ? BigDecimal.ZERO : i.getPrice());
            inventoryMapper.insert(i);
        } else {
            inventoryMapper.update(i);
        }
    }

    public void adjust(Long id, BigDecimal delta) {
        inventoryMapper.addQuantity(id, delta);
    }

    public void delete(Long id) {
        inventoryMapper.delete(id);
    }
}
