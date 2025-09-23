# AI智能客服系统 - Postman测试指南

## 🚀 快速开始测试

### 必要条件
1. 后端服务已启动（端口8080）
2. 数据库连接正常，ai_chat_history表已创建
3. DeepSeek API配置正确，网络能访问api.deepseek.com

### 立即测试
**第一步 - 健康检查：**
```
GET http://localhost:8080/api/ai/chat/health
```

**第二步 - 基础对话：**
```
POST http://localhost:8080/api/ai/chat
Content-Type: application/json

{
  "message": "你好，我想了解外卖平台",
  "userId": 1,
  "chatType": "general"
}
```

**第三步 - 智能推荐：**
```
POST http://localhost:8080/api/ai/chat
Content-Type: application/json

{
  "message": "有什么好吃的川菜馆推荐吗？",
  "userId": 1,
  "sessionId": "test-001",
  "chatType": "business"
}
```

## 详细测试用例

## Postman测试用例

### 1. 健康检查
**目的：** 验证AI客服系统是否正常运行

```
Method: GET
URL: http://localhost:8080/api/ai/chat/health
Headers: 
  Content-Type: application/json
```

**预期响应：**
```json
{
  "code": 200,
  "message": "请求成功",
  "data": "AI客服系统运行正常 🤖"
}
```

### 2. 基础AI对话测试
**目的：** 测试基本的AI对话功能

```
Method: POST
URL: http://localhost:8080/api/ai/chat
Headers: 
  Content-Type: application/json
Body (raw JSON):
{
  "message": "你好，我想了解一下你们的外卖平台",
  "userId": 1,
  "chatType": "general"
}
```

**预期响应：**
```json
{
  "code": 200,
  "message": "请求成功",
  "data": {
    "message": "您好！我是小饿，饿了吧外卖平台的智能客服...",
    "sessionId": "生成的UUID",
    "responseType": "text",
    "needConfirmation": false,
    "responseTime": "2025-09-23T10:30:00",
    "processingTime": 1500
  }
}
```

### 3. 商家查询测试
**目的：** 测试AI智能识别商家查询并返回相关信息

```
Method: POST
URL: http://localhost:8080/api/ai/chat
Headers: 
  Content-Type: application/json
Body (raw JSON):
{
  "message": "附近有什么好吃的川菜馆吗？",
  "userId": 1,
  "sessionId": "test-session-001",
  "chatType": "business"
}
```

### 4. 菜品推荐测试
**目的：** 测试AI根据用户需求推荐菜品

```
Method: POST
URL: http://localhost:8080/api/ai/chat
Headers: 
  Content-Type: application/json
Body (raw JSON):
{
  "message": "今天想吃点清淡的菜，有什么推荐吗？",
  "userId": 1,
  "sessionId": "test-session-002",
  "chatType": "food"
}
```

### 5. 订单查询测试
**目的：** 测试AI处理订单相关问题

```
Method: POST
URL: http://localhost:8080/api/ai/chat
Headers: 
  Content-Type: application/json
Body (raw JSON):
{
  "message": "我的订单怎么还没到？能帮我查一下吗？",
  "userId": 1,
  "sessionId": "test-session-003",
  "chatType": "order"
}
```

### 6. 连续对话测试
**目的：** 测试多轮对话的上下文保持

**第一轮：**
```
Method: POST
URL: http://localhost:8080/api/ai/chat
Body:
{
  "message": "我想点外卖",
  "userId": 1,
  "sessionId": "continuous-chat-001",
  "chatType": "general"
}
```

**第二轮（使用相同sessionId）：**
```
Method: POST
URL: http://localhost:8080/api/ai/chat
Body:
{
  "message": "有什么好的火锅推荐吗？",
  "userId": 1,
  "sessionId": "continuous-chat-001",
  "chatType": "business"
}
```

### 7. 获取对话历史
**目的：** 测试对话历史查询功能

```
Method: GET
URL: http://localhost:8080/api/ai/chat/history?userId=1&page=1&size=10
Headers: 
  Content-Type: application/json
```

**预期响应：**
```json
{
  "code": 200,
  "message": "请求成功",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "sessionId": "test-session-001",
      "userMessage": "你好，我想了解一下你们的外卖平台",
      "aiResponse": "您好！我是小饿...",
      "chatType": "general",
      "processingTime": 1500,
      "createTime": "2025-09-23T10:30:00"
    }
  ]
}
```

### 8. 根据会话ID获取对话历史
```
Method: GET
URL: http://localhost:8080/api/ai/chat/history/session/test-session-001
Headers: 
  Content-Type: application/json
```

### 9. 删除对话记录
```
Method: DELETE
URL: http://localhost:8080/api/ai/chat/history/1
Headers: 
  Content-Type: application/json
```

### 10. 清理旧对话记录
```
Method: POST
URL: http://localhost:8080/api/ai/chat/history/clean?keepCount=50
Headers: 
  Content-Type: application/json
```

## 错误场景测试

### 1. 空消息测试
```
Method: POST
URL: http://localhost:8080/api/ai/chat
Body:
{
  "message": "",
  "userId": 1
}
```
**预期：** 返回400错误，提示消息不能为空

### 2. 无效会话ID测试
```
Method: GET
URL: http://localhost:8080/api/ai/chat/history/session/invalid-session
```
**预期：** 返回空数组或404

### 3. 权限测试
```
Method: GET
URL: http://localhost:8080/api/ai/chat/history?userId=999
```
**预期：** 根据权限控制返回403或数据

## 高级测试场景

### 1. 复杂查询测试
```json
{
  "message": "我想要一家距离我比较近的，有麻辣香锅的，价格在30元左右的商家，最好配送费不要太贵",
  "userId": 1,
  "chatType": "business"
}
```

### 2. 订单状态查询
```json
{
  "message": "我昨天下的订单现在是什么状态？订单号是123456",
  "userId": 1,
  "chatType": "order"
}
```

### 3. 投诉处理
```json
{
  "message": "我的外卖送错了，而且已经凉了，我要投诉",
  "userId": 1,
  "chatType": "general"
}
```

## 性能测试

### 1. 并发测试
- 同时发送多个请求测试系统并发处理能力
- 建议使用Postman的Collection Runner进行批量测试

### 2. 长文本测试
```json
{
  "message": "我想要一个非常详细的推荐，包括商家的特色菜品、价格范围、配送时间、用户评价等等信息，另外我有一些特殊的饮食要求，比如不能太辣，不能有香菜，最好是清淡一些的，而且我希望价格不要太贵，大概在50元以内就可以了，配送地址是天津大学，希望能在一个小时内送到...",
  "userId": 1,
  "chatType": "business"
}
```

## 注意事项

1. **用户ID**：测试时请使用数据库中存在的用户ID
2. **会话ID**：可以使用固定的字符串进行测试，系统会自动生成UUID
3. **响应时间**：首次调用可能较慢，因为需要初始化连接
4. **API限制**：注意DeepSeek API的调用频率限制
5. **日志查看**：可以查看后端日志了解详细的处理过程

## 故障排查

1. **500错误**：检查DeepSeek API配置和网络连接
2. **数据库错误**：确认ai_chat_history表已创建
3. **权限错误**：检查用户认证和授权逻辑
4. **超时错误**：调整timeout配置或检查网络状况
