-- 演示数据补充：为最近 7 天（2026-08-08 ~ 2026-08-14）补充 PAID/REFUNDED 订单及明细
-- 目的：让经营看板、营收趋势、品类报表、TOP 商品、诊断等 BI 报表有连续真实数据
-- 执行：mysql -uroot -p123456 zhixiang_restaurant < backend/sql/seed_demo_orders.sql

SET NAMES utf8mb4;

-- 避免重复执行导致数据翻倍：删除本脚本写入的演示订单（以固定前缀识别）
DELETE FROM order_item WHERE order_id IN (SELECT id FROM orders WHERE order_no LIKE 'SEED%');
DELETE FROM orders WHERE order_no LIKE 'SEED%';

DROP PROCEDURE IF EXISTS seed_insert_order;
DELIMITER $$
CREATE PROCEDURE seed_insert_order(
    IN p_order_no VARCHAR(40),
    IN p_table_no VARCHAR(20),
    IN p_amount DECIMAL(10,2),
    IN p_pay_type VARCHAR(20),
    IN p_status VARCHAR(20),
    IN p_item_count INT,
    IN p_create_time DATETIME,
    IN p_member_phone VARCHAR(32),
    IN p_username VARCHAR(64)
)
BEGIN
    INSERT INTO orders (order_no, table_no, member_id, amount, discount, pay_type, status, item_count, create_time, member_phone, username)
    VALUES (p_order_no, p_table_no, NULL, p_amount, 0.00, p_pay_type, p_status, p_item_count, p_create_time, p_member_phone, p_username);
END$$
DELIMITER ;

-- 以下为每条订单插入主单，随后用 LAST_INSERT_ID() 插入明细
-- 日期 2026-08-08
CALL seed_insert_order('SEED20260808001','T1',144.00,'WECHAT','PAID',3,'2026-08-08 11:20:00','13800000001','demo_customer');
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),2,'宫保鸡丁',38.00,2),(LAST_INSERT_ID(),4,'米饭',2.00,3),(LAST_INSERT_ID(),5,'可乐',6.00,2);
CALL seed_insert_order('SEED20260808002','T3',196.00,'ALIPAY','PAID',4,'2026-08-08 12:35:00','13800000002','demo_customer');
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),6,'水煮鱼',78.00,1),(LAST_INSERT_ID(),1,'麻辣香锅',68.00,1),(LAST_INSERT_ID(),4,'米饭',2.00,4),(LAST_INSERT_ID(),5,'可乐',6.00,1);
CALL seed_insert_order('SEED20260808003','T5',96.00,'CASH','PAID',2,'2026-08-08 18:10:00',NULL,NULL);
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),2,'宫保鸡丁',38.00,1),(LAST_INSERT_ID(),3,'凉拌黄瓜',12.00,2),(LAST_INSERT_ID(),5,'可乐',6.00,3);

-- 日期 2026-08-09
CALL seed_insert_order('SEED20260809001','T2',240.00,'WECHAT','PAID',5,'2026-08-09 12:05:00','13800000003','demo_customer');
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),1,'麻辣香锅',68.00,1),(LAST_INSERT_ID(),6,'水煮鱼',78.00,1),(LAST_INSERT_ID(),7,'酸辣土豆丝',16.00,2),(LAST_INSERT_ID(),4,'米饭',2.00,4),(LAST_INSERT_ID(),5,'可乐',6.00,3);
CALL seed_insert_order('SEED20260809002','T4',120.00,'ALIPAY','PAID',3,'2026-08-09 13:40:00',NULL,NULL);
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),6,'水煮鱼',78.00,1),(LAST_INSERT_ID(),8,'紫菜蛋花汤',10.00,2),(LAST_INSERT_ID(),4,'米饭',2.00,2);
CALL seed_insert_order('SEED20260809003','T6',54.00,'CASH','PAID',2,'2026-08-09 19:25:00','13800000004','demo_customer');
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),3,'凉拌黄瓜',12.00,3),(LAST_INSERT_ID(),5,'可乐',6.00,3);

-- 日期 2026-08-10
CALL seed_insert_order('SEED20260810001','T1',162.00,'WECHAT','PAID',4,'2026-08-10 11:50:00','13800000001','demo_customer');
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),1,'麻辣香锅',68.00,1),(LAST_INSERT_ID(),7,'酸辣土豆丝',16.00,2),(LAST_INSERT_ID(),4,'米饭',2.00,4),(LAST_INSERT_ID(),5,'可乐',6.00,1);
CALL seed_insert_order('SEED20260810002','T3',214.00,'ALIPAY','PAID',4,'2026-08-10 12:55:00',NULL,NULL);
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),6,'水煮鱼',78.00,1),(LAST_INSERT_ID(),2,'宫保鸡丁',38.00,2),(LAST_INSERT_ID(),4,'米饭',2.00,4),(LAST_INSERT_ID(),5,'可乐',6.00,2);
CALL seed_insert_order('SEED20260810003','T7',88.00,'CASH','PAID',2,'2026-08-10 18:40:00','13800000005','demo_customer');
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),8,'紫菜蛋花汤',10.00,4),(LAST_INSERT_ID(),3,'凉拌黄瓜',12.00,4);

-- 日期 2026-08-11
CALL seed_insert_order('SEED20260811001','T2',276.00,'WECHAT','PAID',6,'2026-08-11 12:15:00','13800000002','demo_customer');
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),1,'麻辣香锅',68.00,1),(LAST_INSERT_ID(),6,'水煮鱼',78.00,1),(LAST_INSERT_ID(),2,'宫保鸡丁',38.00,2),(LAST_INSERT_ID(),4,'米饭',2.00,5),(LAST_INSERT_ID(),5,'可乐',6.00,4),(LAST_INSERT_ID(),7,'酸辣土豆丝',16.00,1);
CALL seed_insert_order('SEED20260811002','T5',132.00,'ALIPAY','PAID',3,'2026-08-11 13:30:00',NULL,NULL);
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),6,'水煮鱼',78.00,1),(LAST_INSERT_ID(),8,'紫菜蛋花汤',10.00,2),(LAST_INSERT_ID(),4,'米饭',2.00,3);
CALL seed_insert_order('SEED20260811003','T8',70.00,'CASH','REFUNDED',2,'2026-08-11 20:05:00','13800000003','demo_customer');
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),3,'凉拌黄瓜',12.00,3),(LAST_INSERT_ID(),5,'可乐',6.00,3);

-- 日期 2026-08-12
CALL seed_insert_order('SEED20260812001','T1',200.00,'WECHAT','PAID',5,'2026-08-12 11:35:00','13800000004','demo_customer');
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),1,'麻辣香锅',68.00,1),(LAST_INSERT_ID(),6,'水煮鱼',78.00,1),(LAST_INSERT_ID(),4,'米饭',2.00,5),(LAST_INSERT_ID(),5,'可乐',6.00,3),(LAST_INSERT_ID(),7,'酸辣土豆丝',16.00,1);
CALL seed_insert_order('SEED20260812002','T4',150.00,'ALIPAY','PAID',3,'2026-08-12 12:50:00',NULL,NULL);
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),2,'宫保鸡丁',38.00,2),(LAST_INSERT_ID(),8,'紫菜蛋花汤',10.00,3),(LAST_INSERT_ID(),4,'米饭',2.00,4);
CALL seed_insert_order('SEED20260812003','T6',104.00,'CASH','PAID',3,'2026-08-12 19:15:00','13800000005','demo_customer');
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),3,'凉拌黄瓜',12.00,2),(LAST_INSERT_ID(),5,'可乐',6.00,4),(LAST_INSERT_ID(),7,'酸辣土豆丝',16.00,2);

-- 日期 2026-08-13
CALL seed_insert_order('SEED20260813001','T2',258.00,'WECHAT','PAID',6,'2026-08-13 12:00:00','13800000001','demo_customer');
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),1,'麻辣香锅',68.00,1),(LAST_INSERT_ID(),6,'水煮鱼',78.00,1),(LAST_INSERT_ID(),2,'宫保鸡丁',38.00,2),(LAST_INSERT_ID(),4,'米饭',2.00,5),(LAST_INSERT_ID(),5,'可乐',6.00,3),(LAST_INSERT_ID(),7,'酸辣土豆丝',16.00,1);
CALL seed_insert_order('SEED20260813002','T3',186.00,'ALIPAY','PAID',4,'2026-08-13 13:20:00',NULL,NULL);
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),6,'水煮鱼',78.00,1),(LAST_INSERT_ID(),1,'麻辣香锅',68.00,1),(LAST_INSERT_ID(),4,'米饭',2.00,4),(LAST_INSERT_ID(),5,'可乐',6.00,2);
CALL seed_insert_order('SEED20260813003','T7',92.00,'CASH','PAID',2,'2026-08-13 18:45:00','13800000002','demo_customer');
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),8,'紫菜蛋花汤',10.00,3),(LAST_INSERT_ID(),3,'凉拌黄瓜',12.00,3),(LAST_INSERT_ID(),5,'可乐',6.00,3);

-- 日期 2026-08-14（今天）
CALL seed_insert_order('SEED20260814001','T1',228.00,'WECHAT','PAID',5,'2026-08-14 11:40:00','13800000003','demo_customer');
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),1,'麻辣香锅',68.00,1),(LAST_INSERT_ID(),6,'水煮鱼',78.00,1),(LAST_INSERT_ID(),7,'酸辣土豆丝',16.00,2),(LAST_INSERT_ID(),4,'米饭',2.00,5),(LAST_INSERT_ID(),5,'可乐',6.00,2);
CALL seed_insert_order('SEED20260814002','T4',168.00,'ALIPAY','PAID',4,'2026-08-14 12:30:00',NULL,NULL);
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),2,'宫保鸡丁',38.00,2),(LAST_INSERT_ID(),8,'紫菜蛋花汤',10.00,3),(LAST_INSERT_ID(),4,'米饭',2.00,4),(LAST_INSERT_ID(),5,'可乐',6.00,2);
CALL seed_insert_order('SEED20260814003','T6',110.00,'CASH','PAID',3,'2026-08-14 18:20:00','13800000004','demo_customer');
INSERT INTO order_item (order_id, product_id, name, price, quantity) VALUES (LAST_INSERT_ID(),3,'凉拌黄瓜',12.00,2),(LAST_INSERT_ID(),5,'可乐',6.00,4),(LAST_INSERT_ID(),7,'酸辣土豆丝',16.00,2);

DROP PROCEDURE IF EXISTS seed_insert_order;

-- 校验
SELECT DATE(create_time) d, status, COUNT(*) c, SUM(amount) rev FROM orders WHERE order_no LIKE 'SEED%' GROUP BY DATE(create_time), status ORDER BY d;
