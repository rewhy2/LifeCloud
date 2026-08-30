package com.zhixiang.trade.service;

import com.zhixiang.common.UserContext;
import com.zhixiang.trade.dto.CreateOrderRequest;
import com.zhixiang.trade.dto.OrderItemDTO;
import com.zhixiang.trade.entity.*;
import com.zhixiang.trade.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

/**
 * 订单服务：下单（库存扣减 + 优惠券抵扣）、支付（会员积分累加）、退款（库存回补）。
 *
 * <p>库存(inventory)、会员(member) 与订单(orders) 同库存储，因此通过 Mapper 直接访问，
 * 全部包裹在一个本地事务内，保证一致性。</p>
 */
@Service
public class OrderService {

    private final OrderMapper orderMapper;
    private final ProductMapper productMapper;
    private final InventoryMapper inventoryMapper;
    private final UserCouponMapper userCouponMapper;
    private final CouponMapper couponMapper;
    private final MemberMapper memberMapper;

    public OrderService(OrderMapper orderMapper, ProductMapper productMapper,
                        InventoryMapper inventoryMapper, UserCouponMapper userCouponMapper,
                        CouponMapper couponMapper, MemberMapper memberMapper) {
        this.orderMapper = orderMapper;
        this.productMapper = productMapper;
        this.inventoryMapper = inventoryMapper;
        this.userCouponMapper = userCouponMapper;
        this.couponMapper = couponMapper;
        this.memberMapper = memberMapper;
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
        order.setCouponId(req.getCouponId());

        // 1) 计算商品总价，并逐商品校验+扣减库存
        for (OrderItemDTO it : req.getItems()) {
            Product p = productMapper.selectById(it.getProductId());
            if (p == null) continue;
            int qty = it.getQuantity();
            if (qty <= 0) continue;
            // 超卖防护：存在库存关联时原子扣减，扣减失败（库存不足）回滚
            if (p.getInventoryId() != null) {
                int affected = inventoryMapper.deduct(p.getInventoryId(),
                        BigDecimal.valueOf(qty));
                if (affected == 0) {
                    throw new IllegalArgumentException("商品[" + p.getName() + "]库存不足，下单失败");
                }
            }
            total = total.add(p.getPrice().multiply(BigDecimal.valueOf(qty)));
            itemCount += qty;
        }
        if (itemCount == 0) {
            throw new IllegalArgumentException("订单无有效商品");
        }

        // 2) 优惠券抵扣（仅 CREATED 前计算，标记券已用）
        BigDecimal discount = computeDiscount(req.getCouponId(), total);
        order.setDiscountAmount(discount.setScale(2, RoundingMode.HALF_UP));
        BigDecimal payable = total.subtract(discount).max(BigDecimal.ZERO);
        order.setAmount(payable.setScale(2, RoundingMode.HALF_UP));
        order.setItemCount(itemCount);
        orderMapper.insert(order);

        // 3) 落订单明细 + 销量累加
        for (OrderItemDTO it : req.getItems()) {
            Product p = productMapper.selectById(it.getProductId());
            if (p == null || it.getQuantity() <= 0) continue;
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

        // 4) 标记优惠券已使用
        if (req.getCouponId() != null) {
            int used = userCouponMapper.markUsed(req.getCouponId());
            if (used == 0) {
                throw new IllegalArgumentException("优惠券不可用或已使用");
            }
            couponMapper.incUsed(req.getCouponId());
        }
        return order;
    }

    /** 计算优惠券抵扣金额（满减/折扣）。无券或不可用返回 0。 */
    private BigDecimal computeDiscount(Long userCouponId, BigDecimal total) {
        if (userCouponId == null) return BigDecimal.ZERO;
        Long userId = UserContext.get() == null ? null : UserContext.get().getUserId();
        UserCoupon uc = (userId != null)
                ? userCouponMapper.selectOwnedUnused(userCouponId, userId)
                : userCouponMapper.selectById(userCouponId);
        if (uc == null || uc.getStatus() == null || uc.getStatus() != 1) {
            throw new IllegalArgumentException("优惠券不存在或已使用");
        }
        if (uc.getThreshold() != null && total.compareTo(uc.getThreshold()) < 0) {
            throw new IllegalArgumentException("订单金额未满足优惠券门槛 " + uc.getThreshold());
        }
        BigDecimal discount;
        if ("DISCOUNT".equals(uc.getType()) && uc.getValue() != null) {
            // value 视为折扣率，如 0.9 表示打九折 -> 抵扣 10%
            discount = total.multiply(BigDecimal.ONE.subtract(uc.getValue()));
        } else if ("FULL_REDUCE".equals(uc.getType()) && uc.getValue() != null) {
            discount = uc.getValue();
        } else {
            discount = BigDecimal.ZERO;
        }
        return discount.max(BigDecimal.ZERO);
    }

    @Transactional
    public Order pay(String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null) throw new IllegalArgumentException("订单不存在");
        if (!"CREATED".equals(order.getStatus())) {
            throw new IllegalArgumentException("订单状态异常，无法支付");
        }
        orderMapper.updateStatus(orderNo, "PAID");
        order.setStatus("PAID");

        // 支付成功：会员累计消费 + 积分（1 元 = 1 分）
        if (order.getMemberPhone() != null && !order.getMemberPhone().isBlank()) {
            Member m = memberMapper.selectByPhone(order.getMemberPhone());
            if (m != null) {
                int points = order.getAmount() == null ? 0 : order.getAmount().intValue();
                memberMapper.accumulate(m.getId(), order.getAmount(), points);
            }
        }
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

        // 退款回补库存（按订单明细数量反向加回）
        List<OrderItem> items = orderMapper.selectItemsByOrderId(order.getId());
        for (OrderItem it : items) {
            Product p = productMapper.selectById(it.getProductId());
            if (p != null && p.getInventoryId() != null) {
                inventoryMapper.addBack(p.getInventoryId(), BigDecimal.valueOf(it.getQuantity()));
            }
        }
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
