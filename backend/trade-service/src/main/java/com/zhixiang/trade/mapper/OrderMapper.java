package com.zhixiang.trade.mapper;

import com.zhixiang.trade.entity.Order;
import com.zhixiang.trade.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {
    int insert(Order order);
    int insertItem(OrderItem item);
    int updateStatus(@Param("orderNo") String orderNo, @Param("status") String status);

    Order selectById(Long id);
    Order selectByOrderNo(String orderNo);
    List<Order> selectList(@Param("status") String status, @Param("memberPhone") String memberPhone, @Param("start") String start, @Param("end") String end);

    List<OrderItem> selectItemsByOrderId(Long orderId);
}
