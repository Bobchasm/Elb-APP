-- 1.获取参数
local foodId = ARGV[1]
local userId = ARGV[2]
local quantity = tonumber(ARGV[3])
local requiredPoints = tonumber(ARGV[4])
local requestId = ARGV[5]

-- 2.参数基础校验
if not quantity or quantity <= 0 then
    return "-4:Invalid quantity:" .. requestId
end

if not requiredPoints or requiredPoints <= 0 then
    return "-5:Invalid points:" .. requestId
end

-- 3.检查库存
local stockKey = KEYS[1]
local recordKey = KEYS[2]

local stock = redis.call('GET', stockKey)
if not stock then
    -- 商品库存未预热
    return "-6:Stock cache not initialized:" .. requestId
end

local stockNum = tonumber(stock)
if not stockNum then
    -- 库存值格式错误，重置为0
    redis.call('SET', stockKey, '0')
    stockNum = 0
end

-- 4.检查库存
if stockNum < quantity then
    return "-1:Insufficient stock:" .. requestId
end

-- 5.检查是否重复兑换
local record = redis.call('GET', recordKey)
if record then
    return "-3:Duplicate request:" .. requestId
end

-- 6.预扣库存
redis.call('DECRBY', stockKey, quantity)

-- 7.设置兑换记录
redis.call('SET', recordKey, requestId, 'EX', 30)

-- 8.记录预扣详情
local detailKey = 'points:exchange:detail:' .. requestId
redis.call('HMSET', detailKey,
        'foodId', foodId,
        'userId', userId,
        'quantity', tostring(quantity),
        'points', tostring(requiredPoints),
        'timestamp', redis.call('TIME')[1],
        'stockBefore', tostring(stockNum),
        'stockAfter', tostring(stockNum - quantity)
)
redis.call('EXPIRE', detailKey, 300)

return "1:Success:" .. requestId