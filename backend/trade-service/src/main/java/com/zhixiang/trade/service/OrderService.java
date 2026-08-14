package com.zhixiang.trade.service;

import com.zhixiang.common.UserContext;
import com.zhixiang.trade.dto.CreateOrderRequest;
import com.zhixiang.trade.dto.OrderItemDTO;
import com.zhixiang.trade.entity.Order;
import com.zhixiang.trade.entity.OrderItem;
import com.zhixiang.trade.entity.Product;
import com.zhixiang.trade.mapper.OrderMapper;
import com.zhixiang.trade.mapper.ProductMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;

    public OrderService(OrderMapper orderMapper, ProductMapper productMapper) {
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
    }

    @Transactional
    public Order create(CreateOrderRequest req) {
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new IllegalArgumentException("订单明细不能为空");
        }
        BigDecimal total = BigDecimal.ZERO;
        int itemCount = 0;
        Order order = new Order();
        String orderNo = "ZX" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 4);
        order.setOrderNo(orderNo);
        order.setTableNo(req.getTableNo());
        order.setPayType(req.getPayType() == null ? "CASH" : req.getPayType());
        order.setStatus("CREATED");
        order.setMemberPhone(req.getMemberPhone());
        if (UserContext.get() != null) {
            order.setUsername(UserContext.get().getUsername());
        }

        for (OrderItemDTO it : req.getItems()) {
            Product p = productMapper.selectById(it.getProductId());
            if (p == null) continue;
            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(it.getQuantity())));
            itemCount += it.getQuantity();
        }
        order.setAmount(total.setScale(2, RoundingMode.HALF_UP));
        order.setItemCount(itemCount);
        orderMapper.insert(order);
        for (OrderItemDTO it : req.getItems()) {
            Product p = productMapper.selectById(it.getProductId());
            if (p == null) continue;
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(p.getId());
            item.setName(p.getName());
            item.setPrice(p.getPrice());
            item.setQuantity(it.getQuantity());
            orderMapper.insertItem(item);
            p.setSalesCount((p.getSalesCount() == null ? 0 : p.getSalesCount()) + it.getQuantity());
            productMapper.update(p);
        }
        return order;
    }

    @Transactional
    public Order pay(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) throw new IllegalArgumentException("订单不存在");
        orderMapper.updateStatus(orderNo, "PAID");
        order.setStatus("PAID");
        return order;
    }

    @Transactional
    public Order refund(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) throw new IllegalArgumentException("订单不存在");
        if (!"PAID".equals(order.getStatus())) {
            throw new IllegalArgumentException("仅已支付订单可退款");
        }
        long minutes = Duration.between(order.getCreateTime(), LocalDateTime.now()).toMinutes();
        if (minutes > 15) {
            throw new IllegalArgumentException("超过 15 分钟，需店长手动审批");
        }
        orderMapper.updateStatus(orderNo, "REFUNDED");
        order.setStatus("REFUNDED");
        return order;
    }

    public List<Order> list(String status, String start, String end) {
        return orderMapper.selectList(status, null, start, end);
    }

    public List<Order> myOrders(String memberPhone, String username) {
        if (memberPhone != null && !memberPhone.isBlank()) {
            return orderMapper.selectList(null, memberPhone, null, null);
        }
        return orderMapper.selectList(null, null, null, null).stream()
                .filter(o -> username != null && username.equals(o.getUsername()))
                .toList();
    }

    public Order detail(Long id) {
        Order order = orderMapper.selectById(id);
        if (order != null) {
            order.setItems(orderMapper.selectItemsByOrderId(id));
        }
        return order;
    }
}
