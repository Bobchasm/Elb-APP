<template>
    <div class="points-container">
        <div class="header">
            <h3>我的积分</h3>
        </div>

        <div class="tab-content overview-content">
            
            <div class="current-points-card" @click="goToDetails">
                <i class="fas fa-star points-icon-large"></i>
                <div class="points-detail">
                    <span class="label">当前积分</span>
                    <span class="value">{{ totalPoints }}</span>
                </div>
                <i class="fas fa-chevron-right detail-link-icon"></i> 
            </div>

            <div 
                :class="['recent-expiry-detail', { 'no-warning': expiringCount === 0 }]" 
                class="card-small"
                @click="goToExpiringDetails"
            >
                <i :class="['fas', expiringCount > 0 ? 'fa-exclamation-triangle' : 'fa-check-circle']"></i> 
                <span v-if="expiringCount > 0">
                    您有 **{{ expiringCount }}** 积分即将过期！
                </span>
                <span v-else>
                    暂无即将过期的积分。
                </span>
            </div>

            <div class="lottery-entry-card card-small" @click="goToLottery">
                <div class="main-info">
                    <i class="fas fa-gift lottery-icon"></i>
                    <span class="title">点击进入积分抽奖</span>
                </div>
                <div class="extra-tip">
                    <i class="fas fa-crown"></i> 
                    <span>升级VIP尽享更多福利~</span>
                    <i class="fas fa-chevron-right"></i>
                </div>
            </div>
            <div class="exchange-section card">
                <h4>积分兑换商品 ({{ exchangeProducts.length }})</h4>
                <div class="product-grid">
                    <div 
                        v-for="product in exchangeProducts.filter(p => p.stockQuantity > 0)" 
                        :key="product.foodId" 
                        class="product-card"
                    >
                        <div class="product-image" :style="{ backgroundImage: 'url(' + product.foodImg + ')' }"></div>
                        <div class="product-info">
                            <p class="name">{{ product.foodName }}</p>
                            <div class="price">
                                <i class="fas fa-star"></i>
                                <span class="points-cost">{{ product.requiredPoints }}</span>
                            </div>
                            <div class="stock-info">
                                库存: <span :class="{'low-stock': product.stockQuantity < 10}">{{ product.stockQuantity }}</span>
                            </div>
                            <button 
                                :disabled="totalPoints < product.requiredPoints || product.stockQuantity <= 0"
                                class="exchange-btn"
                                @click.stop="handleExchange(product)"
                            >
                                立即兑换
                            </button>
                        </div>
                    </div>
                </div>
                <div v-if="exchangeProducts.filter(p => p.stockQuantity > 0).length === 0" class="empty-state-small">
                    <i class="fas fa-box-open"></i> 暂无商品可供兑换。
                </div>
            </div>
        </div>

        <div v-if="showExchangeModal" class="modal-overlay" @click.self="showExchangeModal = false">
            <div class="modal-content">
                <div class="modal-header">
                    <h4>兑换 {{ currentProduct.foodName }}</h4>
                    <button class="close-btn" @click="showExchangeModal = false">&times;</button>
                </div>
                <div v-if="currentProduct" class="modal-body">
                    <p>
                        <strong>消耗积分:</strong> 
                        <span class="required-points">{{ currentProduct.requiredPoints * exchangeQuantity }}</span>
                        (当前可用: {{ totalPoints }})
                    </p>
                    <p>
                        <strong>单品积分:</strong> {{ currentProduct.requiredPoints }}
                    </p>
                    
                    <div class="form-group">
                        <label>兑换数量:</label>
                        <div class="quantity-control">
                            <button @click="decreaseQuantity" :disabled="exchangeQuantity <= 1">-</button>
                            <input type="number" v-model.number="exchangeQuantity" readonly min="1" :max="maxQuantity"/>
                            <button @click="increaseQuantity" :disabled="exchangeQuantity >= maxQuantity">+</button>
                        </div>
                    </div>

                    <div class="form-group">
                        <label>配送地址:</label>
                        <select v-model="selectedAddressId" class="address-select">
                            <option value="">请选择收货地址</option>
                            <option 
                                v-for="address in userAddresses" 
                                :key="address.id" 
                                :value="address.id"
                            >
                                {{ address.contactName }} ({{ address.contactTel }}) - {{ address.address }}
                            </option>
                        </select>
                        <p v-if="userAddresses.length === 0" class="address-tip">
                            <i class="fas fa-info-circle"></i> 暂无地址，请先添加。
                        </p>
                    </div>

                    <p v-if="totalPoints < currentProduct.requiredPoints * exchangeQuantity" class="warning-text">
                        <i class="fas fa-exclamation-circle"></i> 积分不足以兑换当前数量。
                    </p>

                    <button 
                        @click="confirmExchange" 
                        :disabled="!selectedAddressId || totalPoints < currentProduct.requiredPoints * exchangeQuantity || isExchanging"
                        class="confirm-btn"
                    >
                        {{ isExchanging ? '兑换中...' : `确认兑换 (${currentProduct.requiredPoints * exchangeQuantity} 积分)` }}
                    </button>
                </div>
            </div>
        </div>

    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router'; 
import request from '../utils/request';
import { toast } from '../utils/toast';

// 初始化路由
const router = useRouter();

// --- 响应式数据 ---
const totalPoints = ref(0); 
const expiringCount = ref(0); 
const exchangeProducts = ref([]);
const userAddresses = ref([]); // 用户地址列表

// 兑换弹窗状态
const showExchangeModal = ref(false);
const currentProduct = ref(null);
const exchangeQuantity = ref(1);
const selectedAddressId = ref('');
const maxQuantity = ref(1); // 最大可兑换数量
const isExchanging = ref(false); // 防止重复提交

// --- 工具函数 ---

const getToken = () => {
    return localStorage.getItem('token') || sessionStorage.getItem('token');
};

const getUserId = () => {
    try {
        const userInfo = JSON.parse(sessionStorage.getItem('userInfo'));
        return userInfo ? userInfo.id : null; // 假设 userInfo 存储了 id
    } catch (e) {
        return null;
    }
};

// 统一 API 错误处理函数
const handleApiError = (error, fallbackMessage) => {
    console.error(fallbackMessage, error);
    
    if (error.response && error.response.status === 401) {
        toast.error('登录已过期，请重新登录！');
        localStorage.removeItem('token');
        sessionStorage.removeItem('token');
        sessionStorage.removeItem('userInfo');
        router.push({ path: '/login' });
    } else {
        toast.error(fallbackMessage + (error.message ? `: ${error.message}` : ''));
    }
    // 失败时重置状态
    isExchanging.value = false;
};

// --- API 调用逻辑 ---

/**
 * 刷新数据：积分和商品列表
 */
const refreshAllData = async () => {
    await Promise.all([
        fetchPointsAccount(),
        fetchExchangeGoods(),
        fetchExpiringCount(),
        fetchUserAddresses(),
    ]);
};

/**
 * 1. 查询积分账户
 */
const fetchPointsAccount = async () => {
    const token = getToken();
    if (!token) return;
    try {
        const response = await request.get(`/api/points/account`, {
            headers: { 'Authorization': `Bearer ${token}` },
        });

        if (response && response.success && response.data) {
            totalPoints.value = response.data.availablePoints || 0; 
        } else {
            toast.error('获取积分账户失败: ' + (response ? response.message : '未知错误'));
        }
    } catch (e) {
        handleApiError(e, '获取积分账户异常');
    }
};

/**
 * 2. 获取可兑换商品列表
 */
const fetchExchangeGoods = async () => {
    const token = getToken();
    if (!token) return;
    try {
        const response = await request.get(`/api/points/exchange-goods`, {
            headers: { 'Authorization': `Bearer ${token}` },
        });

        if (response && response.success && response.data) {
            exchangeProducts.value = response.data.map(item => ({
                foodId: item.foodId,
                foodName: item.foodName,
                foodImg: item.foodImg || 'https://via.placeholder.com/150/cccccc/ffffff?text=Product',
                requiredPoints: item.requiredPoints,
                stockQuantity: item.stockQuantity,
            }));
        } else {
            toast.error('获取兑换商品失败: ' + (response ? response.message : '未知错误'));
        }
    } catch (e) {
        handleApiError(e, '获取兑换商品异常');
    }
};

/**
 * 3. 统计即将过期的积分总数
 */
const fetchExpiringCount = async () => {
    const token = getToken();
    if (!token) return;
    try {
        const response = await request.get(`/api/points/expiring/count`, {
            headers: { 'Authorization': `Bearer ${token}` },
        });
        
        if (response && response.success) {
            expiringCount.value = response.data || 0; 
        } else {
            // 不进行 toast.error，避免频繁弹窗，但记录错误
            console.error('获取过期积分统计失败', response);
        }
    } catch (e) {
        // handleApiError(e, '获取过期积分统计异常'); // 避免对主页面的过度干扰
    }
};

/**
 * 4. 获取用户地址列表 (新增)
 */
const fetchUserAddresses = async () => {
    const token = getToken();
    const userId = getUserId();
    if (!token || !userId) {
        userAddresses.value = [];
        return;
    }

    try {
        const response = await request.get(`/api/addresses/listDeliveryAddressByUserId`, {
            params: { userId },
            headers: { 'Authorization': `Bearer ${token}` },
        });

        if (response && response.success && Array.isArray(response.data)) {
            userAddresses.value = response.data;
            // 默认选择第一个地址
            if (userAddresses.value.length > 0) {
                selectedAddressId.value = userAddresses.value[0].id;
            }
        } else {
            toast.error('获取收货地址失败: ' + (response ? response.message : '未知错误'));
            userAddresses.value = [];
        }
    } catch (e) {
        handleApiError(e, '获取收货地址异常');
        userAddresses.value = [];
    }
};

/**
 * 5. 提交积分兑换 (新增)
 */
const confirmExchange = async () => {
    if (isExchanging.value) return;
    if (!currentProduct.value || !selectedAddressId.value || exchangeQuantity.value <= 0) {
        toast.error('请选择商品、数量和配送地址。');
        return;
    }
    const requiredPoints = currentProduct.value.requiredPoints * exchangeQuantity.value;
    if (totalPoints.value < requiredPoints) {
        toast.error('积分不足，无法完成兑换！');
        return;
    }

    isExchanging.value = true;
    const token = getToken();

    const payload = {
        foodId: currentProduct.value.foodId,
        quantity: exchangeQuantity.value,
        addressId: selectedAddressId.value
    };

    try {
        const response = await request.post(`/api/points/exchange`, payload, {
            headers: { 'Authorization': `Bearer ${token}` },
        });

        if (response && response.success) {
            toast.success('积分兑换成功！');
            showExchangeModal.value = false;
            // 兑换成功后刷新所有数据
            await refreshAllData();
        } else {
            toast.error('兑换失败: ' + (response ? response.message : '未知错误'));
        }
    } catch (e) {
        handleApiError(e, '兑换商品异常');
    } finally {
        isExchanging.value = false;
    }
};


// --- 逻辑处理 ---

// 点击兑换按钮，显示弹窗并初始化数据
const handleExchange = (product) => {
    if (totalPoints.value < product.requiredPoints) {
        toast.error('积分不足，无法兑换！');
        return;
    }
    if (product.stockQuantity <= 0) {
        toast.error('该商品库存不足！');
        return;
    }

    currentProduct.value = product;
    exchangeQuantity.value = 1;
    // 计算最大可兑换数量 (取决于库存和积分)
    const maxByStock = product.stockQuantity;
    const maxByPoints = Math.floor(totalPoints.value / product.requiredPoints);
    maxQuantity.value = Math.min(maxByStock, maxByPoints);
    
    // 确保默认数量不大于最大值
    if (exchangeQuantity.value > maxQuantity.value) {
           exchangeQuantity.value = maxQuantity.value;
    }
    
    // 如果没有地址，提示添加地址
    if (userAddresses.value.length === 0) {
        toast.warning('请先添加收货地址！');
    }
    
    // 重置地址选择 (如果地址被删除，可能需要重新选择)
    if (userAddresses.value.findIndex(a => a.id === selectedAddressId.value) === -1) {
        selectedAddressId.value = userAddresses.value.length > 0 ? userAddresses.value[0].id : '';
    }

    showExchangeModal.value = true;
};

// 增加兑换数量
const increaseQuantity = () => {
    if (exchangeQuantity.value < maxQuantity.value) {
        exchangeQuantity.value++;
    }
};

// 减少兑换数量
const decreaseQuantity = () => {
    if (exchangeQuantity.value > 1) {
        exchangeQuantity.value--;
    }
};

// 跳转到积分明细页面
const goToDetails = () => {
    router.push({ name: 'PointsDetails' }); 
};

// 跳转到过期积分筛选
const goToExpiringDetails = () => {
    // 假设 PointsDetails 页面可以接收 query 参数进行筛选
    router.push({ name: 'PointsDetails', query: { filter: 'expiring' } });
}

// ⭐ 新增：跳转到积分抽奖页面
const goToLottery = () => {
    router.push({ name: 'PointsLottery' }); 
}

// 页面加载时调用所有接口
onMounted(() => {
    const token = getToken();
    const userId = getUserId();

    if (!token || !userId) {
        toast.error('用户未登录或用户ID缺失，请先登录！');
        router.push({ path: '/login' });
        return;
    }

    // 初始加载所有数据
    refreshAllData();
});
</script>

<style scoped>
/* CSS 样式部分 - 保持不变，仅新增 Lottery Card 样式 */

/* 容器和全局样式 */
.points-container {
    padding: 0;
    max-width: 600px;
    margin: 0 auto;
    background-color: #f4f7f9;
    min-height: 100vh;
}

/* 页面头部 */
.header {
    background-color: #fff;
    padding: 15px 20px;
    box-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
    position: sticky;
    top: 0;
    z-index: 10;
    text-align: center;
}
.header h3 {
    margin: 0;
    font-size: 1.2rem;
    color: #333;
}

.tab-content {
    padding: 0 15px 20px 15px;
}

/* 公共卡片样式 */
.card {
    background-color: #fff;
    border-radius: 12px;
    padding: 15px;
    margin-bottom: 15px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}
.card h4 {
    color: #333;
    font-size: 1.05rem;
    margin-bottom: 15px;
    border-left: 4px solid #2979ff;
    padding-left: 8px;
    font-weight: 600;
}

/* 1. 积分概览样式 */
.current-points-card {
    background: linear-gradient(135deg, #2979ff, #64b5f6);
    color: white;
    padding: 25px;
    border-radius: 12px;
    box-shadow: 0 8px 20px rgba(41, 121, 255, 0.3);
    display: flex;
    align-items: center;
    justify-content: space-between;
    cursor: pointer;
    margin-bottom: 15px;
    transition: transform 0.2s ease;
}
.current-points-card:active {
    transform: scale(0.98);
}
.points-icon-large {
    font-size: 3rem;
    opacity: 0.9;
    flex-shrink: 0;
}
.points-detail {
    display: flex;
    flex-direction: column;
    flex-grow: 1;
    margin-left: 15px;
}
.points-detail .label {
    font-size: 0.9rem;
    opacity: 0.8;
}
.points-detail .value {
    font-size: 2.5rem;
    font-weight: 700;
    line-height: 1.1;
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}
.detail-link-icon {
    font-size: 1.2rem;
    color: rgba(255, 255, 255, 0.8);
    flex-shrink: 0;
    margin-left: 10px;
}

/* B. 即将过期积分提醒 */
.card-small {
    padding: 10px 15px;
    border-radius: 8px;
    margin-bottom: 15px; /* 统一小卡片间距 */
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
    cursor: pointer; 
}
.recent-expiry-detail {
    background-color: #fff3e0;
    border: 1px solid #ffcc80;
    font-size: 0.9rem;
    color: #e65100;
    line-height: 1.5;
    display: flex;
    align-items: center;
    justify-content: flex-start;
}
.recent-expiry-detail i {
    margin-right: 8px;
    color: #ff9800;
}
.recent-expiry-detail strong {
    font-weight: 700;
    color: #b74a00;
}
.recent-expiry-detail.no-warning {
    background-color: #e8f5e9;
    border-color: #a5d6a7;
    color: #388e3c;
}
.recent-expiry-detail.no-warning i {
    color: #4caf50;
}

/* ⭐ 新增：积分抽奖入口样式 ⭐ */
.lottery-entry-card {
    /* 使用渐变背景使其醒目 */
    background: linear-gradient(90deg, #ffeb3b, #ffc107);
    border: 1px solid #ff9800;
    color: #333;
    cursor: pointer;
    padding: 12px 15px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    transition: transform 0.2s;
    overflow: hidden; 
}

.lottery-entry-card:active {
    transform: scale(0.98);
}

.lottery-entry-card .main-info {
    display: flex;
    align-items: center;
    font-size: 1rem;
    font-weight: 700;
    color: #d84315; /* 深橙红色 */
}

.lottery-icon {
    font-size: 1.4rem;
    margin-right: 10px;
    /* 添加旋转动画吸引眼球 */
    animation: pulse 1.5s infinite;
}

.lottery-entry-card .title {
    flex-grow: 1;
}

.lottery-entry-card .extra-tip {
    display: flex;
    align-items: center;
    justify-content: space-between;
    font-size: 0.8rem;
    color: #795548; /* 棕色系小字 */
    padding-top: 5px;
    border-top: 1px dashed rgba(255, 255, 255, 0.5); /* 浅色虚线分割 */
}

.lottery-entry-card .extra-tip i {
    color: #e91e63; /* VIP图标使用粉色 */
    margin-right: 5px;
}
.lottery-entry-card .extra-tip .fa-chevron-right {
    color: #d84315;
    margin-left: 10px;
}

/* 脉冲动画 */
@keyframes pulse {
    0% { transform: scale(1); }
    50% { transform: scale(1.05); }
    100% { transform: scale(1); }
}
/* ⭐ 新增样式结束 ⭐ */


/* C. 积分兑换商品 */
.exchange-section {
    padding: 15px;
}
.product-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr); 
    gap: 15px;
}
.product-card {
    background-color: #fff;
    border: 1px solid #eee;
    border-radius: 10px;
    box-shadow: none;
    overflow: hidden;
    transition: transform 0.2s;
}
.product-card:hover {
    transform: translateY(-2px);
}
.product-image {
    width: 100%;
    height: 100px;
    background-size: cover;
    background-position: center;
    border-bottom: 1px solid #f0f0f0;
}
.product-info {
    padding: 8px;
    display: flex;
    flex-direction: column;
    gap: 3px;
}
.product-info .name {
    font-size: 0.85rem;
    font-weight: 500;
    color: #333;
    height: 32px; 
    overflow: hidden;
    line-height: 1.1;
}
.price {
    font-size: 0.9rem;
    color: #ff9800;
    font-weight: 700;
}
.price i {
    margin-right: 3px;
    font-size: 0.8rem;
}
.stock-info {
    font-size: 0.75rem;
    color: #777;
    margin-bottom: 5px;
}
.stock-info .low-stock {
    color: #e65100;
    font-weight: 600;
}

.exchange-btn {
    padding: 6px 0;
    font-size: 0.85rem;
    border-radius: 6px;
    background-color: #2979ff;
    color: white;
    border: none;
    cursor: pointer;
    transition: background-color 0.3s;
}
.exchange-btn:disabled {
    background-color: #ccc;
    color: #666;
    font-weight: 500;
    cursor: not-allowed;
}
.empty-state-small {
    text-align: center;
    padding: 20px;
    color: #999;
    font-size: 0.9rem;
}

/* ==================================== */
/* 兑换弹窗 Modal 样式 */
/* ==================================== */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background-color: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}
.modal-content {
    background: white;
    padding: 20px;
    border-radius: 12px;
    width: 90%;
    max-width: 400px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
    animation: fadeIn 0.3s ease-out;
}
@keyframes fadeIn {
    from { opacity: 0; transform: translateY(-20px); }
    to { opacity: 1; transform: translateY(0); }
}
.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 15px;
    border-bottom: 1px solid #eee;
    padding-bottom: 10px;
}
.modal-header h4 {
    margin: 0;
    font-size: 1.2rem;
    color: #333;
}
.close-btn {
    background: none;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
    color: #999;
    padding: 0;
}
.modal-body p {
    margin: 10px 0;
    font-size: 0.95rem;
    color: #555;
}
.modal-body strong {
    color: #333;
    font-weight: 600;
}
.required-points {
    color: #f44336;
    font-weight: 700;
}
.form-group {
    margin-bottom: 15px;
}
.form-group label {
    display: block;
    margin-bottom: 5px;
    font-weight: 500;
    color: #333;
}

/* 数量控制器 */
.quantity-control {
    display: flex;
    align-items: center;
    width: 120px;
    border: 1px solid #ddd;
    border-radius: 8px;
    overflow: hidden;
}
.quantity-control button {
    width: 30px;
    height: 30px;
    background-color: #f0f0f0;
    border: none;
    font-size: 1rem;
    cursor: pointer;
    transition: background-color 0.2s;
}
.quantity-control button:disabled {
    background-color: #eee;
    color: #ccc;
    cursor: not-allowed;
}
.quantity-control input[type="number"] {
    flex-grow: 1;
    text-align: center;
    border: none;
    height: 30px;
    font-size: 0.95rem;
    font-weight: 600;
    color: #333;
    -moz-appearance: textfield; /* Firefox */
}
.quantity-control input::-webkit-outer-spin-button,
.quantity-control input::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
}

/* 地址选择器 */
.address-select {
    width: 100%;
    padding: 10px;
    border-radius: 8px;
    border: 1px solid #ccc;
    font-size: 0.9rem;
    background-color: white;
}
.address-tip {
    font-size: 0.8rem;
    color: #ff9800;
    margin-top: 5px;
}

/* 警告/确认按钮 */
.warning-text {
    color: #f44336 !important;
    font-weight: 600;
    margin-top: 15px;
}
.warning-text i {
    margin-right: 5px;
}

.confirm-btn {
    width: 100%;
    padding: 12px;
    margin-top: 20px;
    border-radius: 8px;
    background-color: #4caf50;
    color: white;
    border: none;
    font-size: 1rem;
    font-weight: 600;
    cursor: pointer;
    transition: background-color 0.3s;
}
.confirm-btn:disabled {
    background-color: #ccc;
    cursor: not-allowed;
}

/* 移动端适配 */
@media (max-width: 480px) {
    /* ... (保留移动端适配) ... */
    .modal-content {
        width: 95%;
    }
}
</style>