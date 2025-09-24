# AI智能客服后端接口 Postman 测试指南

## 🚀 基础设置

### 服务器信息
- **后端地址**: `http://localhost:8080`
- **接口前缀**: `/api/ai/chat`

### 必备条件
1. 后端服务已启动（端口8080）
2. 数据库连接正常
3. DeepSeek API配置正确

---

## 📋 测试用例

### 1. 健康检查接口

**目的**: 验证AI客服系统是否正常运行

```http
GET http://localhost:8080/api/ai/chat/health
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 200,
  "message": "请求成功",
  "data": "AI客服系统运行正常 🤖"
}
```

---

### 2. 基础AI对话测试

**目的**: 测试基本的AI对话功能

```http
POST http://localhost:8080/api/ai/chat
Content-Type: application/json

{
  "message": "你好，我想了解一下你们的外卖平台",
  "userId": 1,
  "chatType": "general"
}
```

**预期响应**:
```json
{
  "code": 200,
  "message": "请求成功",
  "data": {
    "message": "您好！我是小饿，饿了吧外卖平台的智能客服...",
    "sessionId": "xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx",
    "responseType": "text",
    "needConfirmation": false,
    "responseTime": "2025-09-24T10:30:00",
    "processingTime": 1500
  }
}
```

---

### 3. 商家查询测试

**目的**: 测试AI智能识别商家查询并返回相关信息

```http
POST http://localhost:8080/api/ai/chat
Content-Type: application/json

{
  "message": "附近有什么好吃的川菜馆吗？",
  "userId": 1,
  "sessionId": "test-session-001",
  "chatType": "business"
}
```

---

### 4. 菜品推荐测试

**目的**: 测试AI根据用户需求推荐菜品

```http
POST http://localhost:8080/api/ai/chat
Content-Type: application/json

{
  "message": "今天想吃点清淡的菜，有什么推荐吗？",
  "userId": 1,
  "sessionId": "test-session-002",
  "chatType": "food"
}
```

---

### 5. 订单查询测试

**目的**: 测试AI处理订单相关问题

```http
POST http://localhost:8080/api/ai/chat
Content-Type: application/json

{
  "message": "我的订单怎么还没到？能帮我查一下吗？",
  "userId": 1,
  "sessionId": "test-session-003",
  "chatType": "order"
}
```

---

### 6. 连续对话测试

**目的**: 测试多轮对话的上下文保持

**第一轮**:
```http
POST http://localhost:8080/api/ai/chat
Content-Type: application/json

{
  "message": "我想点外卖",
  "userId": 1,
  "sessionId": "continuous-chat-001",
  "chatType": "general"
}
```

**第二轮（使用相同sessionId）**:
```http
POST http://localhost:8080/api/ai/chat
Content-Type: application/json

{
  "message": "有什么好的火锅推荐吗？",
  "userId": 1,
  "sessionId": "continuous-chat-001",
  "chatType": "business"
}
```

---

### 7. 获取对话历史

**目的**: 测试对话历史查询功能

```http
GET http://localhost:8080/api/ai/chat/history?userId=1&page=1&size=10
Content-Type: application/json
```

**预期响应**:
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
      "createTime": "2025-09-24T10:30:00"
    }
  ]
}
```

---

### 8. 根据会话ID获取对话历史

```http
GET http://localhost:8080/api/ai/chat/history/session/test-session-001
Content-Type: application/json
```

---

### 9. 删除对话记录

```http
DELETE http://localhost:8080/api/ai/chat/history/1
Content-Type: application/json
```

**预期响应**:
```json
{
  "code": 200,
  "message": "请求成功",
  "data": true
}
```

---

### 10. 清理旧对话记录

```http
POST http://localhost:8080/api/ai/chat/history/clean?keepCount=50
Content-Type: application/json
```

---

## ❌ 错误场景测试

### 1. 空消息测试

```http
POST http://localhost:8080/api/ai/chat
Content-Type: application/json

{
  "message": "",
  "userId": 1
}
```

**预期**: 返回400错误，提示消息不能为空

### 2. 无效会话ID测试

```http
GET http://localhost:8080/api/ai/chat/history/session/invalid-session
Content-Type: application/json
```

**预期**: 返回空数组或404

### 3. 权限测试

```http
GET http://localhost:8080/api/ai/chat/history?userId=999
Content-Type: application/json
```

**预期**: 根据权限控制返回403或数据

---

## 🔧 高级测试场景

### 1. 复杂查询测试

```http
POST http://localhost:8080/api/ai/chat
Content-Type: application/json

{
  "message": "我想要一家距离我比较近的，有麻辣香锅的，价格在30元左右的商家，最好配送费不要太贵",
  "userId": 1,
  "chatType": "business"
}
```

### 2. 订单状态查询

```http
POST http://localhost:8080/api/ai/chat
Content-Type: application/json

{
  "message": "我昨天下的订单现在是什么状态？订单号是123456",
  "userId": 1,
  "chatType": "order"
}
```

### 3. 投诉处理

```http
POST http://localhost:8080/api/ai/chat
Content-Type: application/json

{
  "message": "我的外卖送错了，而且已经凉了，我要投诉",
  "userId": 1,
  "chatType": "general"
}
```

---

## 📊 性能测试

### 1. 长文本测试

```http
POST http://localhost:8080/api/ai/chat
Content-Type: application/json

{
  "message": "我想要一个非常详细的推荐，包括商家的特色菜品、价格范围、配送时间、用户评价等等信息，另外我有一些特殊的饮食要求，比如不能太辣，不能有香菜，最好是清淡一些的，而且我希望价格不要太贵，大概在50元以内就可以了，配送地址是天津大学，希望能在一个小时内送到...",
  "userId": 1,
  "chatType": "business"
}
```

---

## ⚠️ 注意事项

1. **用户ID**: 测试时请使用数据库中存在的用户ID（如1、2、3等）
2. **会话ID**: 可以使用固定的字符串进行测试，系统会自动生成UUID
3. **响应时间**: 首次调用可能较慢，因为需要初始化DeepSeek API连接
4. **API限制**: 注意DeepSeek API的调用频率限制
5. **日志查看**: 可以查看后端控制台日志了解详细的处理过程

---

## 🛠️ 故障排查

### 常见错误及解决方案

**500错误**: 
- 检查DeepSeek API配置和网络连接
- 确认API Key是否正确
- 检查application.yml中的deepseek配置

**数据库错误**: 
- 确认ai_chat_history表已创建
- 检查数据库连接配置

**权限错误**: 
- 检查用户认证和授权逻辑
- 确认用户ID是否存在

**超时错误**: 
- 调整timeout配置
- 检查网络状况
- DeepSeek API服务是否正常

---

## 📝 测试检查清单

- [ ] 健康检查接口正常
- [ ] 基础对话功能正常
- [ ] 商家查询功能正常
- [ ] 菜品推荐功能正常
- [ ] 订单查询功能正常
- [ ] 连续对话上下文保持正常
- [ ] 对话历史查询正常
- [ ] 对话历史删除正常
- [ ] 错误处理机制正常
- [ ] 性能表现符合预期

---

## 💡 测试建议

1. **按顺序测试**: 先进行健康检查，再测试基础功能
2. **记录会话ID**: 用于后续的连续对话测试
3. **观察响应时间**: 正常情况下应在3秒内返回
4. **检查数据库**: 确认对话记录是否正确保存
5. **测试边界情况**: 空消息、超长消息、特殊字符等
