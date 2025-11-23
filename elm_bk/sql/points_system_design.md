# 积分系统数据库设计说明

## 一、设计原则

基于现有 `elm_v2` 数据库的表结构，积分系统数据库设计遵循以下原则：

1. **命名规范一致性**：表名、字段名遵循现有命名风格
2. **字段类型一致性**：使用与现有表相同的字段类型（bigint, decimal(10,2), timestamp等）
3. **审计字段统一**：所有表包含 create_time, update_time, creator, updater, is_deleted
4. **外键关联规范**：遵循现有表的外键关联方式
5. **索引优化**：根据查询场景设计合理的索引

## 二、表结构说明

### 2.1 积分账户表 (points_account)

**作用**：存储用户积分账户信息

**关键字段**：
- `user_id`：用户ID，唯一索引，关联 users 表
- `total_points`：总积分余额
- `available_points`：可用积分余额
- `frozen_points`：冻结积分（订单处理中）
- `member_level`：会员等级（0-普通，1-白银，2-黄金，3-钻石）

**设计要点**：
- 用户注册时自动创建积分账户
- 一个用户只有一个积分账户（唯一索引保证）
- 会员等级由管理员或其他系统设置，积分系统只读取使用

### 2.2 积分明细表 (points_transaction)

**作用**：记录所有积分变动明细

**关键字段**：
- `transaction_type`：交易类型（0-获得，1-消费，2-过期，3-冻结，4-解冻）
- `points_source`：积分来源（0-消费积分，1-促销积分，2-等级积分，3-行为积分，4-兑换商品，5-积分+现金消费）
- `points_change`：积分变动数量（正数增加，负数减少）
- `points_balance`：变动后余额（便于对账）
- `expire_time`：积分过期时间（NULL表示永久有效）

**设计要点**：
- 每条积分变动都记录，不可物理删除（逻辑删除）
- `points_balance` 记录变动后余额，便于对账和审计
- 支持积分有效期管理

### 2.3 积分过期记录表 (points_expiration)

**作用**：记录每笔积分的过期信息，支持优先扣减即将过期积分

**关键字段**：
- `transaction_id`：关联积分明细ID
- `points_amount`：即将过期积分数量
- `expire_time`：过期时间（精确到秒）
- `expire_date`：过期日期（用于按日期查询）
- `is_expired`：是否已过期

**设计要点**：
- 积分获得时，如果设置了过期时间，在此表创建记录
- 积分消费时，按 `expire_time` 升序扣减（优先扣减即将过期的积分）
- 定时任务定期清理已过期记录

### 2.4 营销系统-积分规则表 (marketing_points_rule)

**作用**：存储积分获取规则

**关键字段**：
- `rule_type`：规则类型（0-消费积分，1-促销积分，2-等级积分，3-行为积分）
- `rule_status`：规则状态（0-禁用，1-启用）
- `points_ratio`：积分比例（如1.0表示消费1元获得1积分）
- `points_multiplier`：积分倍数（如2.0表示双倍积分）
- `member_level`：适用会员等级（NULL表示所有等级）
- `priority`：优先级（数字越大优先级越高）

**促销积分规则字段**：
- `holiday_start/end`：节假日日期范围
- `food_id`：指定商品ID
- `food_price_threshold`：商品价格阈值
- `min_order_amount/max_order_amount`：订单金额区间

**行为积分规则字段**：
- `behavior_type`：行为类型（like-点赞，collect-收藏，repay_loan-还贷款）
- `points_amount`：固定积分数量

**设计要点**：
- 支持多规则，通过优先级控制
- 支持时间范围、会员等级、商品等条件
- 规则可启用/禁用，不影响历史数据

### 2.5 营销系统-积分兑换规则表 (marketing_points_exchange_rule)

**作用**：存储积分消费规则

**关键字段**：
- `rule_type`：规则类型（0-积分+现金，1-兑换商品）
- `exchange_ratio`：兑换比例（如10表示10积分=1元）
- `food_id`：商品ID（兑换商品时使用）
- `required_points`：所需积分（兑换商品时使用）
- `stock_quantity`：库存数量（兑换商品时使用）

**设计要点**：
- 积分+现金：统一兑换比例，如10积分=1元
- 兑换商品：每个商品独立设置所需积分和库存

### 2.6 积分兑换订单表 (points_exchange_order)

**作用**：记录积分兑换订单

**关键字段**：
- `order_id`：关联订单ID（积分+现金消费时使用）
- `exchange_type`：兑换类型（0-积分+现金，1-纯积分兑换商品）
- `food_id`：兑换商品ID
- `points_used`：使用积分数量
- `cash_amount`：现金金额（积分+现金消费时使用）
- `status`：状态（0-待处理，1-已完成，2-已取消）

**设计要点**：
- 积分+现金消费：关联到原订单
- 纯积分兑换：独立订单，不关联原订单

### 2.7 积分到期预警配置表 (points_expiration_alert_config)

**作用**：存储预警配置（仅短信）

**关键字段**：
- `alert_days`：提前预警天数（如7表示提前7天预警）
- `alert_cycle`：预警周期（天数，如1表示每天，3表示每3天）
- `sms_template`：短信模板（支持变量：{username}, {points}, {expireDate}）
- `is_enabled`：是否启用

**设计要点**：
- 系统级配置，通常只有一条记录
- 支持模板变量替换
- 仅支持短信预警，不包含站内信

### 2.8 积分到期预警记录表 (points_expiration_alert_log)

**作用**：记录预警发送历史（仅短信）

**关键字段**：
- `points_amount`：即将过期积分数量
- `expire_date`：过期日期
- `alert_time`：预警时间
- `next_alert_time`：下次预警时间（根据预警周期计算）
- `is_sent`：是否已发送
- `phone`：用户手机号（发送短信时使用）

**设计要点**：
- 记录每次预警，避免重复发送
- `next_alert_time` 用于控制预警周期

### 2.9 订单表扩展字段

在现有 `orders` 表中添加：
- `points_used`：使用积分数量（默认0）
- `points_amount`：获得积分数量（默认0）

**说明**：业务逻辑只有两种支付方式：
1. **积分+现金消费**：先用积分，积分用完了剩下的部分再用现金支付（这是默认的）
2. **只用现金支付**：包括微信、支付宝、虚拟钱包支付

因此不需要 `points_payment_method` 字段来区分支付方式，可以通过 `points_used` 是否为 0 来判断是否使用了积分。

## 三、表关系图

```
users (用户表)
  ├── points_account (积分账户表) [1:1]
  │   └── points_transaction (积分明细表) [1:N]
  │       └── points_expiration (积分过期记录表) [1:1]
  │
  ├── orders (订单表) [1:N]
  │   ├── points_exchange_order (积分兑换订单表) [1:1]
  │   └── orderdetailet (订单详情表) [1:N]
  │       └── food (商品表) [N:1]
  │
  └── merchant_interaction (商家互动表) [1:N]
      └── business (商家表) [N:1]

marketing_points_rule (积分规则表)
  ├── food (商品表) [N:1] (可选)
  └── points_transaction (积分明细表) [1:N]

marketing_points_exchange_rule (积分兑换规则表)
  └── food (商品表) [N:1] (可选)

points_expiration_alert_config (预警配置表)
  └── points_expiration_alert_log (预警记录表) [1:N]
      └── users (用户表) [N:1]
```

## 四、索引设计说明

### 4.1 主键索引
所有表使用 `id` 作为主键，自增。

### 4.2 唯一索引
- `points_account.user_id`：确保一个用户只有一个积分账户

### 4.3 普通索引
- **用户相关查询**：`user_id` 索引（points_transaction, points_expiration, points_exchange_order等）
- **时间相关查询**：`create_time`, `expire_time`, `expire_date` 索引
- **状态相关查询**：`rule_status`, `transaction_type`, `status` 索引
- **关联查询**：`order_id`, `food_id`, `account_id` 索引

### 4.4 联合索引
- `points_expiration(user_id, expire_time)`：查询用户即将过期积分
- `points_expiration_alert_log(user_id, expire_date)`：查询用户预警记录

## 五、外键约束

所有外键约束遵循现有表的规范：
- `ON DELETE RESTRICT`：防止误删关联数据
- `ON UPDATE RESTRICT`：防止误更新关联数据

## 六、数据初始化

SQL文件包含以下初始化数据：

1. **基础消费积分规则**：消费1元=1积分
2. **积分+现金兑换规则**：10积分=1元
3. **默认预警配置**：提前7天预警，每天预警一次

## 七、使用说明

### 7.1 执行SQL文件
```sql
-- 在MySQL客户端执行
source /path/to/points_system.sql;
```

### 7.2 验证表结构
```sql
-- 查看所有积分系统相关表
SHOW TABLES LIKE 'points%';
SHOW TABLES LIKE 'marketing_points%';

-- 查看表结构
DESC points_account;
```

### 7.3 注意事项
1. 执行SQL前请备份数据库
2. 确保有足够的权限创建表和索引
3. 外键约束需要确保关联表已存在
4. 建议在测试环境先执行验证

## 八、后续扩展建议

1. **性能优化**：
   - 考虑使用Redis缓存积分余额
   - 定期归档历史积分明细数据

2. **功能扩展**：
   - 可扩展积分转赠功能
   - 可扩展积分商城功能

3. **监控告警**：
   - 监控积分异常变动
   - 监控积分过期情况

