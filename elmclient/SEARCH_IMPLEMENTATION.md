# 搜索功能实现说明

## 功能概述

已成功实现搜索和排序功能，连接后端接口 `/api/businesses/search`，支持以下功能：

- 关键词搜索（keyword参数）
- 按评分排序（isScore参数：0/1）
- 按销量排序（isSales参数：0/1）

## 实现的功能

### 1. 搜索页面 (`src/views/Search.vue`)

**新增功能：**
- 搜索框输入关键词
- 三种排序方式：综合排序、按评分排序、按销量排序
- 显示搜索结果数量
- 显示商家评分和销量信息
- 无搜索结果时的友好提示
- 搜索历史功能

**API调用：**
```javascript
// 搜索参数示例
const params = {
  keyword: '美食',        // 搜索关键词
  isScore: 1,            // 是否按评分排序 (0/1)
  isSales: 0             // 是否按销量排序 (0/1)
};

// 调用接口
const response = await request.get('/api/businesses/search', { params });
```

### 2. 请求工具更新 (`src/utils/request.js`)

**新增功能：**
- 自动添加 `Authorization` 和 `token` 请求头
- 支持GET请求参数传递

### 3. 测试页面 (`src/views/SearchTest.vue`)

**功能：**
- 测试搜索接口调用
- 显示请求参数和响应结果
- 错误信息显示
- 访问路径：`/search-test`

## 接口规范

### 请求参数

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| keyword | string | 否 | 搜索关键词 |
| isScore | boolean | 否 | 是否按评分排序 (0/1) |
| isSales | boolean | 否 | 是否按销量排序 (0/1) |

### 请求头

```
Authorization: Bearer {token}
token: {token}
```

### 响应格式

```json
{
  "success": true,
  "code": "string",
  "data": [
    {
      "id": 0,
      "businessName": "string",
      "businessImg": "string",
      "startPrice": 0,
      "deliveryPrice": 0,
      "score": 0,
      "salesCount": 0
    }
  ],
  "message": "string"
}
```

## 使用方法

### 1. 基本搜索
```javascript
// 搜索关键词为"美食"的商家
const params = {
  keyword: '美食',
  isScore: 0,
  isSales: 0
};
```

### 2. 按评分排序
```javascript
// 按评分从高到低排序
const params = {
  keyword: '美食',
  isScore: 1,
  isSales: 0
};
```

### 3. 按销量排序
```javascript
// 按销量从高到低排序
const params = {
  keyword: '美食',
  isScore: 0,
  isSales: 1
};
```

## 测试方法

1. 启动前端项目
2. 访问 `/search-test` 页面进行接口测试
3. 访问 `/search` 页面进行完整功能测试

## 注意事项

1. 确保后端服务运行在 `http://localhost:8080`
2. 确保用户已登录并获取到有效的token
3. 搜索参数 `isScore` 和 `isSales` 不能同时为1
4. 图片加载失败时会自动使用默认图片

## 文件修改清单

- ✅ `src/views/Search.vue` - 更新搜索页面UI和逻辑
- ✅ `src/utils/request.js` - 添加token请求头支持
- ✅ `src/views/SearchTest.vue` - 新增测试页面
- ✅ `src/router/index.js` - 添加测试路由
