-- ============================================================
-- 订单功能增强 数据库迁移脚本
-- 适用于: zhixiang_restaurant (与所有服务共用同一库)
-- 说明: 新增下单库存扣减、优惠券抵扣、会员积分累加所需字段
-- ============================================================

-- 1) product 表增加库存关联字段
ALTER TABLE product
    ADD COLUMN inventory_id BIGINT NULL COMMENT '关联库存食材(inventory.id)，用于下单扣减库存';

-- 2) orders 表增加优惠券与抵扣金额字段
ALTER TABLE orders
    ADD COLUMN coupon_id BIGINT NULL COMMENT '使用的用户优惠券(user_coupon.id)',
    ADD COLUMN discount_amount DECIMAL(12,2) NULL COMMENT '优惠抵扣金额';

-- 3) 若库存表尚无 product_id 反向关联（可选，便于按商品查库存），可补充：
-- ALTER TABLE inventory ADD COLUMN product_id BIGINT NULL COMMENT '关联商品(product.id)';

-- 4) 会员积分/累计消费字段（如已存在请忽略）
-- member 表已有 point / total_spend 字段，无需新增；
-- 如缺失可取消下行注释执行：
-- ALTER TABLE member
--     ADD COLUMN total_spend DECIMAL(12,2) NULL DEFAULT 0 COMMENT '累计消费',
--     ADD COLUMN point INT NULL DEFAULT 0 COMMENT '会员积分';

-- 提示：
-- - 库存扣减依赖 product.inventory_id 正确指向 inventory.id
-- - 折扣券 value 约定为 0~1 的折扣率（如 0.9 表示打九折）
-- - 满减券 value 为固定减免金额
