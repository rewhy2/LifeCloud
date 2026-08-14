package com.zhixiang.ops.mapper;

import com.zhixiang.ops.entity.Purchase;
import com.zhixiang.ops.entity.PurchaseItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PurchaseMapper {
    int insert(Purchase p);
    int insertItem(PurchaseItem item);
    int updateStatus(@Param("orderNo") String orderNo, @Param("status") String status);
    Purchase selectByOrderNo(String orderNo);
    List<Purchase> selectList(@Param("status") String status);
    List<PurchaseItem> selectItems(Long purchaseId);
}
