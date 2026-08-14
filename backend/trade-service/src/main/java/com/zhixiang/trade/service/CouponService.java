package com.zhixiang.trade.service;

import com.zhixiang.common.UserContext;
import com.zhixiang.trade.entity.Coupon;
import com.zhixiang.trade.entity.UserCoupon;
import com.zhixiang.trade.mapper.CouponMapper;
import com.zhixiang.trade.mapper.UserCouponMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CouponService {

    private final CouponMapper couponMapper;
    private final UserCouponMapper userCouponMapper;

    public CouponService(CouponMapper couponMapper, UserCouponMapper userCouponMapper) {
        this.couponMapper = couponMapper;
        this.userCouponMapper = userCouponMapper;
    }

    public List<Coupon> list() {
        return couponMapper.selectAll();
    }

    public void save(Coupon c) {
        if (c.getId() == null) {
            c.setReceived(0);
            c.setUsed(0);
            couponMapper.insert(c);
        } else {
            couponMapper.update(c);
        }
    }

    public void toggleStatus(Long id, Integer status) {
        couponMapper.updateStatus(id, status);
    }

    public void delete(Long id) {
        couponMapper.delete(id);
    }

    /** 顾客领取优惠券 */
    @Transactional
    public void grant(Long id) {
        Coupon coupon = couponMapper.selectById(id);
        if (coupon == null || coupon.getStatus() == 0) {
            throw new IllegalArgumentException("优惠券不存在或已停用");
        }
        if (coupon.getTotal() != null && coupon.getReceived() != null && coupon.getReceived() >= coupon.getTotal()) {
            throw new IllegalArgumentException("优惠券已被领完");
        }
        Long userId = UserContext.get() == null ? null : UserContext.get().getUserId();
        if (userId == null) throw new IllegalArgumentException("请先登录");
        if (userCouponMapper.countByUserAndCoupon(userId, id) > 0) {
            throw new IllegalArgumentException("已领取过该优惠券");
        }
        UserCoupon uc = new UserCoupon();
        uc.setUserId(userId);
        uc.setCouponId(coupon.getId());
        uc.setCouponName(coupon.getName());
        uc.setType(coupon.getType());
        uc.setThreshold(coupon.getThreshold());
        uc.setValue(coupon.getValue());
        userCouponMapper.insert(uc);
        couponMapper.incReceived(id);
    }

    /** 当前顾客已领的优惠券 */
    public List<UserCoupon> myCoupons() {
        Long userId = UserContext.get() == null ? null : UserContext.get().getUserId();
        if (userId == null) throw new IllegalArgumentException("请先登录");
        return userCouponMapper.selectByUser(userId).stream()
                .filter(uc -> uc.getStatus() != null && uc.getStatus() == 1)
                .collect(Collectors.toList());
    }
}
