<template>
    <div class="details-container">
        
        <div class="header">
            <h3>积分明细</h3>
        </div>

        <div class="tab-content details-header-content">
            <div class="current-points-card">
                <i class="fas fa-star points-icon-large"></i>
                <div class="points-detail">
                    <span class="label">当前积分</span>
                    <span class="value">{{ totalPoints }}</span> 
                    
                    <div class="available-points-info">
                        <span class="available-label">可用积分:</span>
                        <span class="available-value">{{ availablePoints }}</span>
                    </div>
                </div>
            </div>
        </div>

        <div class="tab-content transaction-content">
            <div class="status-filters">
                <div
                    v-for="status in transactionStatuses"
                    :key="status.value"
                    :class="['filter-item', { 
                        active: activeStatusFilter === status.value, 
                        'expiring-animated': status.value === 'expiring' 
                    }]"
                    @click="activeStatusFilter = status.value"
                >
                    {{ status.label }}
                </div>
            </div>

            <div 
                v-if="activeStatusFilter === 'gain' || activeStatusFilter === 'deduct'" 
                class="secondary-filters"
            >
                <div v-if="activeStatusFilter === 'gain'">
                    <span class="filter-label">来源:</span>
                    <div
                        v-for="source in gainSources"
                        :key="source.value"
                        :class="['filter-item secondary', { active: activeGainSourceFilter === source.value }]"
                        @click="activeGainSourceFilter = source.value"
                    >
                        {{ source.label }}
                    </div>
                </div>

                <div v-if="activeStatusFilter === 'deduct'">
                    <span class="filter-label">类型:</span>
                    <div
                        v-for="type in deductTypes"
                        :key="type.value"
                        :class="['filter-item secondary', { active: activeDeductTypeFilter === type.value }]"
                        @click="activeDeductTypeFilter = type.value"
                    >
                        {{ type.label }}
                    </div>
                </div>
            </div>

            <ul class="transaction-list">
                <li v-if="loading" class="loading-state">
                    <i class="fas fa-spinner fa-spin"></i> 正在加载明细...
                </li>

                <template v-else-if="filteredTransactions.length > 0">
                    <li 
                        v-for="item in filteredTransactions" 
                        :key="item.id" 
                        :class="['transaction-item', {'item-expired': item.isExpired}]"
                        @click="showItemDetails(item)"
                    >
                        <div class="left">
                            <span class="desc">{{ item.description }}</span>
                            <div class="tag-group">
                                <span v-if="item.isFrozen" class="frozen-tag-item">冻结中</span>
                                <span v-else-if="item.isThawed" class="thaw-tag-item">已解冻</span>
                                <span class="date">{{ item.date }}</span>
                            </div>
                        </div>
                        <span :class="['points-change', getPointClass(item.type), {'text-expired': item.isExpired}]">
                            {{ getPointPrefix(item.type) }}{{ item.points }}
                        </span>
                    </li>
                </template>

                <li v-else class="empty-state">
                    <i class="fas fa-search-minus"></i> 暂无符合条件的积分收支记录。
                </li>
            </ul>
        </div>

        <div v-if="showDetailsModal" class="modal-overlay" @click.self="showDetailsModal = false">
            <div class="modal-content">
                <div class="modal-header">
                    <h4>积分变动详情</h4>
                    <button class="close-btn" @click="showDetailsModal = false">&times;</button>
                </div>
                <div v-if="selectedItemDetails" class="modal-body">
                    <p><strong>变动类型:</strong> {{ selectedItemDetails.transactionTypeName }}</p>
                    <p><strong>变动来源:</strong> {{ selectedItemDetails.pointsSourceName }}</p>
                    <p>
                        <strong>变动积分:</strong> 
                        <span :class="['points-change', getPointClass(selectedItemDetails.type)]">
                            {{ getPointPrefix(selectedItemDetails.type) }}{{ selectedItemDetails.points }}
                        </span>
                    </p>
                    <p><strong>变动后余额:</strong> {{ selectedItemDetails.pointsBalance }}</p>
                    <p><strong>变动时间:</strong> {{ selectedItemDetails.date }}</p>
                    <p v-if="selectedItemDetails.expireTime"><strong>过期时间:</strong> {{ selectedItemDetails.expireTime }}</p>
                    <p v-if="selectedItemDetails.isFrozen || selectedItemDetails.isThawed">
                        <strong>状态:</strong> 
                        <span v-if="selectedItemDetails.isFrozen" class="frozen-tag-item small">冻结中</span>
                        <span v-else-if="selectedItemDetails.isThawed" class="thaw-tag-item small">已解冻</span>
                    </p>
                    
                    <hr>
                    
                    <p v-if="selectedItemDetails.relatedOrderId"><strong>关联订单ID:</strong> {{ selectedItemDetails.relatedOrderId }}</p>
                    <p v-if="selectedItemDetails.relatedFoodId"><strong>关联商品ID:</strong> {{ selectedItemDetails.relatedFoodId }}</p>

                    <p><strong>详细描述:</strong></p>
                    <p class="detail-description">{{ selectedItemDetails.rawDescription }}</p>
                </div>
            </div>
        </div>
        
    </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import { useRouter } from 'vue-router';
// 假设这些工具已存在于您的项目中
import request from '../utils/request';
import { toast } from '../utils/toast';

const router = useRouter();

// --- 常量映射 (根据新要求更新) ---
const TRANSACTION_TYPE_MAP = {
    'gain': 0,      
    'deduct': 1,    
    'expired': 2,   
    'frozen': 3,    
    'thaw': 4,      
};

const POINTS_SOURCE_MAP = {
    'consume_frozen': 0,      
    'vip': 2,               
    'interact': 3,          
    'exchange': 4,          
    'deduction_deducted': 5, 
    'deduction_frozen': 5,   
    'thaw_order_complete': 0, 
};

// --- 状态和 Tab 切换 ---
const loading = ref(true);
const totalPoints = ref(0); 
const availablePoints = ref(0); 

const activeStatusFilter = ref('all'); 
const activeGainSourceFilter = ref('all'); 
const activeDeductTypeFilter = ref('all'); 

const showDetailsModal = ref(false);
const selectedItemDetails = ref(null);

// 筛选列表
const transactionStatuses = [
    { label: '全部', value: 'all' },
    { label: '已获得', value: 'gain' },
    { label: '已消耗', value: 'deduct' },
    { label: '过期积分', value: 'expiring' }, 
];

const gainSources = [
    { label: '全部', value: 'all' },
    { label: '消费奖励', value: 'consume' }, 
    { label: 'VIP奖励', value: 'vip' },
    { label: '互动奖励', value: 'interact' },
];

const deductTypes = [
    { label: '全部', value: 'all' },
    { label: '兑换商品', value: 'exchange' },
    { label: '积分抵扣', value: 'deduction' }, 
];

const mainTransactions = ref([]); 
const expiringTransactions = ref([]); 

// --- 公共方法 ---

const getToken = () => {
    return localStorage.getItem('token') || sessionStorage.getItem('token');
};

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
};

const getPointClass = (type) => {
    if (type === '获得' || type === '解冻') return 'gain';
    if (type === '消费' || type === '过期' || type === '冻结') return 'deduct';
    return '';
};

const getPointPrefix = (type) => {
    if (type === '获得' || type === '解冻') return '+';
    if (type === '消费' || type === '冻结' || type === '过期') return '-'; 
    return '';
};


// -------------------------------------------------------------
// 1/2. 查询积分账户 (获取总积分和可用积分)
// -------------------------------------------------------------
const fetchTotalPoints = async () => {
    const token = getToken();
    if (!token) return;
    try {
        const response = await request.get(`/api/points/account`, {
            headers: { 'Authorization': `Bearer ${token}` },
        });

        if (response && response.success && response.data) {
            totalPoints.value = response.data.totalPoints || response.data.availablePoints || 0; 
            availablePoints.value = response.data.availablePoints || 0; 
        } else {
            toast.error('获取积分余额失败');
        }
    } catch (e) {
        handleApiError(e, '获取积分余额异常');
    }
};


// -------------------------------------------------------------
// 3. 映射 API 响应数据（核心逻辑）
// -------------------------------------------------------------

/**
 * 映射 API 响应数据到前端所需的字段
 * @param {object} item - PointsTransactionVO 结构
 */
const mapTransactionItem = (item) => {
    const points = Math.abs(item.pointsChange);
    const txType = item.transactionType;
    const ptSource = item.pointsSource;

    let type;
    let isFrozen = false;
    let isThawed = false;

    // 冻结逻辑判断：(0, 0) 或 (3, 5)
    if (
        (txType === TRANSACTION_TYPE_MAP.gain && ptSource === POINTS_SOURCE_MAP.consume_frozen) || 
        (txType === TRANSACTION_TYPE_MAP.frozen && ptSource === POINTS_SOURCE_MAP.deduction_frozen) 
    ) {
        isFrozen = true;
    }

    // 解冻逻辑判断：(4, 0)
    if (txType === TRANSACTION_TYPE_MAP.thaw && ptSource === POINTS_SOURCE_MAP.thaw_order_complete) {
        isThawed = true;
    }

    // 确定列表显示的类型 (type)
    if (txType === TRANSACTION_TYPE_MAP.gain) {
        type = '获得'; 
    } else if (txType === TRANSACTION_TYPE_MAP.deduct) {
        type = '消费'; 
    } else if (txType === TRANSACTION_TYPE_MAP.frozen) {
        type = '冻结'; 
    } else if (txType === TRANSACTION_TYPE_MAP.thaw) {
        type = '解冻'; 
    } else if (txType === TRANSACTION_TYPE_MAP.expired) {
        type = '过期';
    } else {
        type = item.transactionTypeName;
    }

    // --- 列表描述优化 (新修改) ---
    const TARGET_FROZEN_DESC = '订单消费获得积分（冻结中，订单完成后解冻）';
    const SIMPLIFIED_DESC = '订单消费获得积分';
    
    let listDescription = item.description || item.transactionTypeName || '无描述';
    
    // 如果描述精确匹配目标字符串，则替换为简化版描述
    if (item.description === TARGET_FROZEN_DESC) {
        listDescription = SIMPLIFIED_DESC;
    }
    // --- 列表描述优化结束 ---


    const mappedItem = {
        id: item.id,
        type: type, 
        points: points,
        // 列表展示的 description 字段 (使用替换后的描述)
        description: listDescription, 
        date: item.createTime,
        isFrozen: isFrozen, 
        isThawed: isThawed, 
        isExpired: false, 
        
        // 增强弹窗详情信息 (rawDescription 存储完整描述)
        pointsBalance: item.pointsBalance,
        expireTime: item.expireTime,
        relatedOrderId: item.relatedOrderId,
        relatedFoodId: item.relatedFoodId,
        transactionTypeName: item.transactionTypeName || type,
        pointsSourceName: item.pointsSourceName || 'N/A',
        // rawDescription 存储完整描述，用于弹窗详情
        rawDescription: item.description || item.transactionTypeName || '无详细描述',
        originalItem: item
    };

    return mappedItem;
};

/**
 * 点击列表项，显示弹窗
 * @param {object} item - 映射后的交易记录
 */
const showItemDetails = (item) => {
    // 适配过期积分列表项的详情处理
    if (activeStatusFilter.value === 'expiring') {
        selectedItemDetails.value = {
            ...item,
            pointsBalance: 'N/A', 
            transactionTypeName: '过期',
            pointsSourceName: '系统处理',
            rawDescription: item.description 
        };
    } else {
        selectedItemDetails.value = item;
    }
    
    showDetailsModal.value = true;
};

// --- 查询过期积分记录 ---
const fetchExpiringTransactions = async () => {
    const token = getToken();
    if (!token) return;
    
    expiringTransactions.value = [];

    const fetchExpiredList = async (isExpiredStatus, apiUrl) => {
        try {
            const response = await request.get(apiUrl, {
                params: { isExpired: isExpiredStatus }, 
                headers: { 'Authorization': `Bearer ${token}` },
            });
            return (response && response.success && response.data) ? response.data : [];
        } catch (e) {
            console.error(`获取积分状态=${isExpiredStatus}异常`, e);
            return [];
        }
    };

    const expiringSoon = await fetchExpiredList(0, '/api/points/expiring'); 
    const expiredAlready = await fetchExpiredList(1, '/api/points/expired'); 

    // 映射即将过期
    const mappedExpiring = expiringSoon.map(item => ({
        id: `expiring-${item.id}`,
        type: '过期',
        points: item.pointsAmount,
        description: `即将过期积分 (到期日: ${item.expireDate})`,
        date: item.expireTime,
        isExpired: false, 
        rawDescription: `到期时间: ${item.expireTime}，距离过期天数: ${item.daysUntilExpiration}`,
    }));

    // 映射已过期
    const mappedExpired = expiredAlready.map(item => ({
        id: `expired-${item.id}`,
        type: '过期',
        points: item.pointsAmount,
        description: `已过期积分 (过期日: ${item.expireDate})`,
        date: item.expireTime,
        isExpired: true, 
        rawDescription: `已过期积分，过期时间: ${item.expireTime}`,
    }));

    let combinedList = [...mappedExpiring, ...mappedExpired];
    combinedList.sort((a, b) => {
        if (a.isExpired !== b.isExpired) {
            return a.isExpired ? 1 : -1;
        }
        return new Date(a.date) - new Date(b.date);
    });

    expiringTransactions.value = combinedList;
};

// --- 查询积分明细 ---
const fetchTransactions = async (params = {}) => {
    const token = getToken();
    if (!token) return;

    mainTransactions.value = [];
    
    const status = activeStatusFilter.value;
    let queryParams = { ...params, pageNum: 1, pageSize: 50 }; 
    let selectedTransactionType = null;
    let selectedPointsSource = null;

    if (status === 'gain') {
        selectedTransactionType = TRANSACTION_TYPE_MAP.gain; 
        if (activeGainSourceFilter.value === 'vip') {
            selectedPointsSource = POINTS_SOURCE_MAP.vip; 
        } else if (activeGainSourceFilter.value === 'interact') {
            selectedPointsSource = POINTS_SOURCE_MAP.interact; 
        }
    } else if (status === 'deduct') {
        selectedTransactionType = TRANSACTION_TYPE_MAP.deduct; 
        if (activeDeductTypeFilter.value === 'exchange') {
            selectedPointsSource = POINTS_SOURCE_MAP.exchange; 
        } else if (activeDeductTypeFilter.value === 'deduction') {
            selectedPointsSource = POINTS_SOURCE_MAP.deduction_deducted; 
        }
    } 
    
    if (selectedTransactionType !== null) {
        queryParams.transactionType = selectedTransactionType;
    }
    if (selectedPointsSource !== null) {
        queryParams.pointsSource = selectedPointsSource;
    }

    loading.value = true;
    try {
        const response = await request.get(`/api/points/transactions`, {
            params: queryParams,
            headers: { 'Authorization': `Bearer ${token}` },
        });

        if (response && response.success && response.data && Array.isArray(response.data)) {
            mainTransactions.value = response.data
                .filter(item => item.transactionType !== TRANSACTION_TYPE_MAP.expired)
                .map(mapTransactionItem); 
        } else {
            toast.error('获取积分明细失败');
        }
    } catch (e) {
        handleApiError(e, '获取积分明细异常');
    } finally {
        loading.value = false;
    }
};

// --- 数据触发和监听 ---

watch([activeStatusFilter, activeGainSourceFilter, activeDeductTypeFilter], () => {
    if (activeStatusFilter.value !== 'expiring') {
        if (activeStatusFilter.value !== 'gain') activeGainSourceFilter.value = 'all';
        if (activeStatusFilter.value !== 'deduct') activeDeductTypeFilter.value = 'all';
        fetchTransactions();
    }
}, { deep: true });

// --- 计算属性：积分收支明细筛选 ---
const filteredTransactions = computed(() => {
    const status = activeStatusFilter.value;
    let listToFilter = [];

    if (status === 'expiring') {
        listToFilter = expiringTransactions.value; 
        return listToFilter;
    } else { 
        listToFilter = mainTransactions.value;
    }

    return listToFilter.sort((a, b) => new Date(b.date) - new Date(a.date));
});

// --- 生命周期钩子 ---

onMounted(() => {
    const token = getToken();
    if (!token) {
        toast.warning('用户未登录，请先登录！');
        router.push({ path: '/login' });
        return;
    }

    Promise.all([
        fetchTotalPoints(),
        fetchExpiringTransactions(), 
        fetchTransactions(), 
    ]);
});
</script>

<style scoped>
/* 容器样式 */
.details-container {
    padding: 20px;
    max-width: 800px;
    margin: 0 auto;
    font-family: Arial, sans-serif;
    background-color: #f8f8f8;
}

.header {
    margin-bottom: 20px;
    border-bottom: 1px solid #eee;
    padding-bottom: 10px;
}

/* 顶部积分卡片样式 */
.current-points-card {
    background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
    color: #fff;
    padding: 25px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    margin-bottom: 20px;
}

.points-icon-large {
    font-size: 2.5rem;
    margin-right: 20px;
    color: #ffd700; /* 金色 */
}

.points-detail {
    display: flex;
    flex-direction: column;
}

.points-detail .label {
    font-size: 0.9rem;
    opacity: 0.8;
    margin-bottom: 5px;
}

.points-detail .value {
    font-size: 2.5rem;
    font-weight: bold;
    line-height: 1;
}

/* 可用积分样式 */
.available-points-info {
    font-size: 0.75rem; 
    margin-top: 5px; 
    padding-top: 5px;
    border-top: 1px solid rgba(255, 255, 255, 0.2);
    display: flex;
    gap: 5px;
    align-items: center;
}

.available-label {
    opacity: 0.7;
    font-weight: 300;
}

.available-value {
    font-weight: 600;
}


/* 筛选器样式 */
.status-filters {
    display: flex;
    gap: 10px;
    margin-bottom: 15px;
    overflow-x: auto;
    padding-bottom: 5px;
}

.secondary-filters {
    padding: 10px 0;
    margin-bottom: 15px;
    border-top: 1px solid #eee;
}

.filter-label {
    font-size: 0.85rem;
    color: #666;
    margin-right: 10px;
}

.filter-item {
    cursor: pointer;
    padding: 8px 15px;
    border: 1px solid #ccc;
    border-radius: 20px;
    font-size: 0.9rem;
    white-space: nowrap;
    transition: all 0.2s;
    display: inline-block;
}

.filter-item:hover {
    border-color: #007bff;
    color: #007bff;
}

.filter-item.active {
    background-color: #007bff;
    color: #fff;
    border-color: #007bff;
}

.filter-item.secondary {
    padding: 5px 10px;
    font-size: 0.8rem;
    margin-right: 5px;
}

/* 列表样式 */
.transaction-list {
    list-style: none;
    padding: 0;
}

.transaction-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 0;
    border-bottom: 1px solid #eee;
    cursor: pointer;
    transition: background-color 0.2s;
}

.transaction-item:hover {
    background-color: #f0f0f0;
}

.left {
    display: flex;
    flex-direction: column;
}

.desc {
    font-weight: 600;
    font-size: 1rem;
    color: #333;
    margin-bottom: 5px;
}

.tag-group {
    display: flex;
    align-items: center;
    font-size: 0.8rem;
}

.date {
    color: #999;
}

/* 积分变动颜色 */
.points-change {
    font-weight: bold;
    font-size: 1.1rem;
}

.points-change.gain {
    color: #28a745; /* 绿色 */
}

.points-change.deduct {
    color: #dc3545; /* 红色 */
}

/* 冻结/解冻标签 */
.frozen-tag-item {
    background-color: #ffc107;
    color: #333;
    padding: 2px 6px;
    border-radius: 4px;
    font-size: 0.75rem;
    font-weight: 500;
    margin-right: 8px;
}

.thaw-tag-item {
    background-color: #17a2b8;
    color: #fff;
    padding: 2px 6px;
    border-radius: 4px;
    font-size: 0.75rem;
    font-weight: 500;
    margin-right: 8px;
}

/* 弹窗中的小标签 */
.frozen-tag-item.small, .thaw-tag-item.small {
    font-size: 0.8rem;
    padding: 2px 8px;
    margin-left: 5px;
}

/* 过期积分样式 */
.item-expired {
    opacity: 0.6;
}
.item-expired .desc {
    color: #666;
}
.points-change.text-expired {
    color: #666 !important;
}

/* 状态和空状态 */
.loading-state, .empty-state {
    text-align: center;
    padding: 40px 0;
    color: #999;
}

/* 弹窗 Modal 样式 */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.modal-content {
    background: #fff;
    padding: 25px;
    border-radius: 8px;
    width: 90%;
    max-width: 450px;
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    border-bottom: 1px solid #eee;
    padding-bottom: 10px;
}

.modal-header h4 {
    margin: 0;
    font-size: 1.2rem;
}

.close-btn {
    background: none;
    border: none;
    font-size: 1.5rem;
    cursor: pointer;
    color: #999;
}

.modal-body p {
    margin: 8px 0;
    font-size: 0.9rem;
    color: #555;
}

.modal-body strong {
    color: #333;
    font-weight: 600;
    display: inline-block;
    min-width: 90px;
}

.modal-body hr {
    border: 0;
    border-top: 1px dashed #eee;
    margin: 15px 0;
}

.detail-description {
    background-color: #f9f9f9;
    padding: 10px;
    border-radius: 4px;
    white-space: pre-wrap;
    word-break: break-word;
    font-style: italic;
}
</style>