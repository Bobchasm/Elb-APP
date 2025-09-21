# 首页搜索功能实现说明

## 功能概述

已在首页（Index.vue）中成功实现搜索和排序功能，连接后端接口 `/api/businesses/search`，支持以下功能：

- 关键词搜索（keyword参数）
- 按评分排序（isScore参数：0/1）
- 按销量排序（isSales参数：0/1）

## 实现的功能

### 1. 搜索框更新
- 将原来的点击跳转搜索框改为可输入的搜索框
- 添加搜索按钮
- 支持回车键搜索

### 2. 排序选项
- 综合排序（默认）
- 按评分排序
- 按销量排序
- 排序按钮有激活状态显示

### 3. 搜索结果显示
- 显示搜索到的商家列表
- 显示商家评分和销量信息
- 兼容原有的商家数据结构

## 主要修改内容

### 1. 模板更新
```vue
<!-- 搜索框 -->
<div class="search-box">
    <i class="fa fa-search"></i>
    <input v-model="searchKeyword" type="text" placeholder="搜索饿了么商家、商品名称" @keyup.enter="performSearch" />
    <button @click="performSearch" class="search-btn">搜索</button>
</div>

<!-- 排序选项 -->
<div v-if="businessList.length > 0" class="sort-options">
    <div class="sort-buttons">
        <button :class="{ active: sortBy === 'default' }" @click="setSortBy('default')">综合排序</button>
        <button :class="{ active: sortBy === 'score' }" @click="setSortBy('score')">按评分排序</button>
        <button :class="{ active: sortBy === 'sales' }" @click="setSortBy('sales')">按销量排序</button>
    </div>
</div>
```

### 2. JavaScript逻辑
```javascript
// 搜索功能
const performSearch = async () => {
    if (searchKeyword.value.trim() !== '') {
        const params = {
            keyword: searchKeyword.value.trim()
        };
        
        // 根据排序方式添加参数
        if (sortBy.value === 'score') {
            params.isScore = 1;
            params.isSales = 0;
        } else if (sortBy.value === 'sales') {
            params.isScore = 0;
            params.isSales = 1;
        } else {
            params.isScore = 0;
            params.isSales = 0;
        }
        
        // 调用搜索接口
        const response = await request.get('/api/businesses/search', { params });
        
        if (response && response.data) {
            businessList.value = response.data;
        }
    } else {
        // 如果搜索关键词为空，显示所有商家
        getBusinessList();
    }
};

// 排序功能
const setSortBy = (type) => {
    sortBy.value = type;
    if (searchKeyword.value.trim() !== '') {
        performSearch(); // 重新搜索以应用新的排序
    } else {
        getBusinessList(); // 如果没有搜索关键词，重新加载所有商家
    }
};
```

### 3. CSS样式
- 搜索框样式：输入框、搜索按钮
- 排序按钮样式：激活状态、悬停效果
- 商家列表样式：评分和销量显示

## 使用方法

### 1. 基本搜索
1. 在搜索框中输入关键词
2. 点击搜索按钮或按回车键
3. 系统会调用 `/api/businesses/search` 接口

### 2. 排序功能
1. 在搜索后或搜索前选择排序方式
2. 系统会根据选择的排序方式重新搜索或加载数据

### 3. 清空搜索
1. 清空搜索框内容
2. 系统会自动显示所有商家

## 接口调用示例

```javascript
// 基本搜索
GET /api/businesses/search?keyword=美食&isScore=0&isSales=0

// 按评分排序
GET /api/businesses/search?keyword=美食&isScore=1&isSales=0

// 按销量排序
GET /api/businesses/search?keyword=美食&isScore=0&isSales=1
```

## 注意事项

1. 确保后端服务运行在 `http://localhost:8080`
2. 确保用户已登录并获取到有效的token
3. 搜索参数 `isScore` 和 `isSales` 不能同时为1
4. 搜索结果会替换原有的商家列表
5. 如果没有搜索关键词，会显示所有商家

## 文件修改清单

- ✅ `src/views/Index.vue` - 更新首页搜索和排序功能
- ✅ 搜索框改为可输入
- ✅ 添加排序选项UI
- ✅ 实现搜索和排序逻辑
- ✅ 更新商家列表显示
- ✅ 添加相关CSS样式
