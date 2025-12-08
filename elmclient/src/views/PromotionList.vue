<template>
    <div class="back-btn-container">
        <BackButton style="margin-top: 2vw;"/>
    </div>
  <div class="promotion-list-page">
    <div class="details-container">
        <div class="header app-header-fixed">
            <h3>促销指定商品</h3>
        </div>
    </div>

    <div class="product-content">
      <p v-if="loading" class="loading-state">正在加载商品信息...</p>
      <p v-else-if="error" class="error-state">加载失败：{{ error }}</p>
      <p v-else-if="products.length === 0 && !loading" class="empty-state">
        暂无关联的促销商品或商品已下架。
      </p>

      <ul v-else class="product-list">
        <li v-for="product in products" :key="product.id" class="product-item">
          
          <div class="product-item-content">
            <img :src="product.foodImg || defaultImg" class="product-image" :alt="product.foodName">
            
            <div class="product-info-group">
              <span class="product-name">{{ product.businessName }} | {{ product.foodName }}</span>
              
              <span class="product-explain">
                {{ formatExplain(product.foodExplain) }}
              </span>
            </div>
          </div>
          
          <div class="product-action-group">
             <div class="product-price">
              <span class="price-value">¥{{ product.foodPrice.toFixed(2) }}</span>
             </div>

             <button @click="navigateToPayment(product.id)" class="buy-now-btn">
               立即购买
             </button>
          </div>
        </li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
// ⭐ 恢复真实网络请求
import request from '../utils/request'; 
// 假设 toast 模块可用，用于错误提示
// import { toast } from 'your-toast-library'; 

const route = useRoute();
const router = useRouter();

// 响应式数据
const products = ref([]);
const loading = ref(true);
const error = ref(null);
const defaultImg = ref('https://via.placeholder.com/60x60?text=Food'); 

// --- 工具函数：限制商品说明字数 ---
const EXPLAIN_LIMIT = 15;
const formatExplain = (explain) => {
    if (!explain) return '暂无说明';
    return explain.length > EXPLAIN_LIMIT ? explain.substring(0, EXPLAIN_LIMIT) + '...' : explain;
};

// --- 认证工具函数 ---
const getToken = () => {
    // 统一从本地存储获取 token 的逻辑
    return localStorage.getItem('token') || sessionStorage.getItem('token');
};

/**
 * 核心逻辑：恢复真实请求，添加调试信息
 */
const fetchProducts = async (ids) => {
    const token = getToken();

    if (!ids || ids.length === 0) {
        loading.value = false;
        products.value = [];
        return;
    }

    if (!token) {
        console.error('❌ [DEBUG] 认证失败：getToken() 返回空值。请检查登录状态和存储！');
        loading.value = false;
        error.value = '请先登录以查看商品详情。';
        // 实际应用中建议 router.push({ path: '/login' });
        return;
    }
    
    loading.value = true;
    error.value = null;
    
    // ⭐ 调试信息：打印使用的 token 状态
    console.log(`[DEBUG] Authorization Token (Exists): ${!!token}`);
    
    // 1. 创建所有 API 请求的 Promise 数组
    const detailPromises = ids.map(foodId => {
        const url = `/api/foods/detail/${foodId}`;
        
        // ⭐ 调试信息：打印请求 URL
        console.log(`[DEBUG] 准备发送请求: ${url}`);
        
        // 恢复真实请求：手动在请求中添加 Authorization Header
        return request.get(url, {
            headers: { 'Authorization': `Bearer ${token}` },
            // 保持 validateStatus 设置，确保非 2xx 状态码进入 .catch
            validateStatus: function (status) { return status >= 200 && status < 600; }
        })
        .then(response => {
            // ⭐ 调试信息：请求成功响应
            console.log(`[DEBUG] foodId: ${foodId} 请求成功响应 (HTTP ${response.status})`, response);
            return response;
        }) 
        .catch(err => {
            // ⭐ 调试信息：请求失败异常捕获
            console.error(`🚨 [ERROR] foodId: ${foodId} 请求异常捕获:`, err);
            
            // 检查 Axios 错误对象结构
            if (err.response) {
                console.error(`🚨 [ERROR] 后端返回状态码: ${err.response.status}, 业务消息: ${err.response.data?.message || 'N/A'}`);
            } else if (err.request) {
                console.error('🚨 [ERROR] 请求已发出，但未收到响应 (可能是代理或 CORS 失败):', err.request);
            } else {
                console.error('🚨 [ERROR] 请求配置失败或网络中断:', err.message);
            }

            // 返回一个结构化的错误对象，确保 Promise.all 不中断
            return { 
                isError: true, 
                // 使用 NETWORK_ERR 或 AUTH_MISSING 作为默认状态码，以便区分
                status: err.response?.status || 'NETWORK_ERR',
                data: err.response?.data || { message: err.message || '底层网络或配置失败' },
                foodId: foodId
            };
        });
    });

    try {
        // 2. 使用 Promise.all 并发发送所有请求
        const responses = await Promise.all(detailPromises);
        
        // 3. 处理和过滤响应数据
        const fetchedProducts = [];
        const failedRequests = [];

        responses.forEach((response, index) => {
            const foodId = ids[index];

            // 1. 检查是否为我们自定义的错误对象（底层失败或网络异常）
            if (response.isError) {
                failedRequests.push(foodId);
                const statusMsg = response.status === 'NETWORK_ERR' ? '网络/配置错误' : response.status;
                const errMsg = response.data?.message || '请求配置或底层网络失败';
                
                console.warn(`⚠️ 获取 foodId: ${foodId} 详情失败 (状态: ${statusMsg}, 消息: ${errMsg})`);
                
                // 强制要求重新登录（例如 401 错误）
                if (response.status === 401) {
                    error.value = '认证失败，请重新登录！';
                }
                return; 
            }

            // 2. 检查成功的 HTTP 状态码 (200) 和业务 success 标记
            if (response.status === 200 && response.data && response.data.success) {
                // 成功获取数据，且 success 为 true
                const foodDetail = response.data.data;
                
                // 3. 仅保留“已上架”且“未删除”的商品
                if (foodDetail && foodDetail.shelveStatus === 1 && foodDetail.deleted === false) {
                    fetchedProducts.push(foodDetail);
                } else {
                    failedRequests.push(foodId);
                    const reason = foodDetail ? (foodDetail.deleted ? '已删除' : '已下架') : '数据结构异常';
                    console.warn(`⚠️ foodId: ${foodId} 被业务逻辑过滤 (${reason})`);
                }
            } else {
                // 请求返回但业务失败 (非 200，或 200 但 success: false)
                failedRequests.push(foodId);
                const statusDisplay = response.status || 'N/A';
                console.warn(`⚠️ 获取 foodId: ${foodId} 详情失败 (HTTP 状态码: ${statusDisplay}, 业务消息: ${response.data?.message || '后端业务逻辑失败'})`);
            }
        });
        
        products.value = fetchedProducts;
        console.log(`✅ 成功加载 ${fetchedProducts.length} 条促销商品详情.`);
        
        if (failedRequests.length > 0) {
            console.warn(`部分商品请求失败或无法显示 (ID: ${failedRequests.join(', ')})`);
        }

    } catch (err) {
        console.error('❌ 批量获取商品详情失败 (Promise.all 之外的错误):', err);
        error.value = '连接商品详情服务失败。';
    } finally {
        loading.value = false;
    }
};

/**
 * 跳转到支付页面 (接受商品ID)
 */
const navigateToPayment = (productId) => {
  if (productId) {
    console.log(`🛒 跳转到支付页面，购买商品 ID: ${productId}`);
    router.push({ 
      path: '/payment', 
      query: { productId: productId } 
    });
  } else {
    console.warn('⚠️ 缺少商品 ID，无法跳转到支付页面。');
  }
};

// 组件挂载时执行
onMounted(() => {
  const idsParam = route.query.ids; 
  let foodIdsArray = [];
  
  if (idsParam) {
    foodIdsArray = idsParam.split(',').map(id => parseInt(id.trim())).filter(id => !isNaN(id));
  }
  
  // 如果 URL 中没有提供 ids，使用默认值 [1, 2, 3, 4, 5] 进行测试
  if (foodIdsArray.length === 0) {
      foodIdsArray = [1, 2, 3, 4, 5]; 
  }

  console.log('🚀 页面获取到的 foodIds (真实请求):', foodIdsArray);
  
  fetchProducts(foodIdsArray);
});

</script>

<style scoped>
/*试图统一header */
.app-header-fixed {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    /* 确保在宽屏设备上不会过宽，这里设置 max-width */
    max-width: 600px; 
    /* 在 fixed 模式下，margin: 0 auto 需要额外的 left/right 配合 */
    left: 50%;
    transform: translateX(-50%); 
    
    height: 12vw; /* 统一高度，使用 vw */
    background-color: #0097FF;
    color: #fff;
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.app-header-fixed h3,
.app-header-fixed h1 { /* 统一 h1 和 h3 的样式 */
    font-size: 4.8vw; /* 统一字体大小 */
    margin: 0;
    font-weight: 500;
    color: inherit; /* 继承父级的 #fff 颜色 */
}

/* ⚠️ 重要：添加 padding-top 以防止内容被固定头部遮挡 */
.details-container {
    padding-top: 13vw; /* 顶部容器增加内边距，等于头部高度 */
}

.back-btn-container {
    position: fixed; /* 固定定位，不随滚动移动 */
    left: 0vw; /* 距离左侧的距离，可根据需求调整 */
    top: 0vw; /* 距离顶部的距离，与 header 高度（12vw）适配，确保垂直居中 */
    z-index: 1001; /* 比 header 的 z-index:1000 高，避免被遮挡 */
}

/* --- 整体容器和头部样式保持不变 --- */
.promotion-list-page {
  max-width: 600px;
  margin: 0 auto;
  padding: 0 15px 20px;
}

.page-header {
  padding: 15px 0;
  border-bottom: 1px solid #eee;
  margin-bottom: 20px;
}

.header-title {
  font-size: 20px;
  color: #333;
  font-weight: bold;
}

.product-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

/* --- ⭐ 商品列表项样式更新 (参考用户提供的样式) ⭐ --- */
.product-item {
  /* 统一列表项基础样式 */
  padding: 12px;
  background-color: #fff;
  border-radius: 8px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  
  /* 实现主要内容和操作按钮的分隔 */
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  transition: transform 0.3s ease-in-out, background-color 0.3s; 
  border-bottom: 1px solid #f0f0f0; /* 结合用户提供的 business-list li 样式 */
}

.product-item:hover {
    background-color: #f9f9f9;
    transform: translateY(-2px);
}

/* --- 新增图片和信息区域的 Flex 容器 --- */
.product-item-content {
    display: flex;
    align-items: center;
    flex-grow: 1; /* 占据主要空间 */
    margin-right: 15px; 
}

/* --- 新增商品图片样式 --- */
.product-image {
    width: 60px;
    height: 60px;
    object-fit: cover;
    border-radius: 6px;
    margin-right: 12px;
    flex-shrink: 0; /* 防止图片被压缩 */
}

/* --- 信息组样式 --- */
.product-info-group {
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex-grow: 1; 
  min-width: 0; /* 允许文本溢出和省略号生效 */
}

.product-name {
  font-size: 15px;
  color: #333;
  font-weight: bold; 
  margin-bottom: 4px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* --- ⭐ 商品说明样式 ⭐ --- */
.product-explain {
  font-size: 12px;
  color: #999;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* --- 价格和按钮容器 (右侧区域) --- */
.product-action-group {
    display: flex;
    flex-direction: column;
    align-items: flex-end; /* 价格和按钮靠右对齐 */
    flex-shrink: 0;
}

.product-price {
    margin-bottom: 5px; /* 价格和按钮留出空间 */
}

/* --- 价格值样式 --- */
.price-value {
  font-size: 16px;
  font-weight: bold;
  color: #ff5722;
  display: block;
  text-align: right;
}

/* 立即购买按钮 */
.buy-now-btn {
  background-color: #ff5722; 
  color: white;
  border: none;
  border-radius: 20px;
  padding: 6px 12px; /* 减小 padding 适应新布局 */
  font-size: 13px;
  cursor: pointer;
  transition: background-color 0.2s;
  flex-shrink: 0;
}

.buy-now-btn:hover {
  background-color: #e64a19;
}

/* 状态信息 */
.loading-state, .error-state, .empty-state {
  text-align: center;
  padding: 40px 0;
  color: #999;
}
</style>