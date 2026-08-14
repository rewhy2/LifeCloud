package com.zhixiang.ops.service;

import com.zhixiang.ops.dto.CreatePurchaseRequest;
import com.zhixiang.ops.dto.PurchaseItemDTO;
import com.zhixiang.ops.entity.Purchase;
import com.zhixiang.ops.entity.PurchaseItem;
import com.zhixiang.ops.mapper.PurchaseMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class PurchaseService {

    private final PurchaseMapper purchaseMapper;
    private final InventoryService inventoryService;

    public PurchaseService(PurchaseMapper purchaseMapper, InventoryService inventoryService) {
        this.purchaseMapper = purchaseMapper;
        this.inventoryService = inventoryService;
    }

    @Transactional
    public Purchase create(CreatePurchaseRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("采购明细不能为空");
        }
        BigDecimal total = BigDecimal.ZERO;
        Purchase purchase = new Purchase();
        String orderNo = "PO" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 4);
        purchase.setOrderNo(orderNo);
        purchase.setSupplierId(req.getSupplierId());
        purchase.setStatus("PENDING");
        for (PurchaseItemDTO it : req.getItems()) {
            total = total.add(it.getPrice().multiply(it.getQuantity()));
        }
        purchase.setAmount(total.setScale(2, RoundingMode.HALF_UP));
        purchaseMapper.insert(purchase);
        for (PurchaseItemDTO it : req.getItems()) {
            PurchaseItem item = new PurchaseItem();
            item.setPurchaseId(purchase.getId());
            item.setInventoryId(it.getInventoryId());
            item.setName(it.getName());
            item.setQuantity(it.getQuantity());
            item.setPrice(it.getPrice());
            purchaseMapper.insertItem(item);
        }
        return purchase;
    }

    @Transactional
    public Purchase stockIn(String orderNo) {
        Purchase p = purchaseMapper.selectByOrderNo(orderNo);
        if (p == null) throw new IllegalArgumentException("采购单不存在");
        if ("STOCKED".equals(p.getStatus())) throw new IllegalArgumentException("已入库");
        for (PurchaseItem it : purchaseMapper.selectItems(p.getId())) {
            inventoryService.adjust(it.getInventoryId(), it.getQuantity());
        }
        purchaseMapper.updateStatus(orderNo, "STOCKED");
        p.setStatus("STOCKED");
        return p;
    }

    public List<Purchase> list(String status) {
        return purchaseMapper.selectList(status);
    }

    public Purchase detail(String orderNo) {
        Purchase p = purchaseMapper.selectByOrderNo(orderNo);
        if (p != null) {
            p.setItems(purchaseMapper.selectItems(p.getId()));
        }
        return p;
    }
}
