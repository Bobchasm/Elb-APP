<template>
    <div class="back-btn-container">
        <BackButton style="margin-top: 2vw;"/>
    </div>
    <div class="wrapper">
        <header>
            <p>在线支付</p>
        </header>

        <div v-if="loading" class="loading">
            <p>加载中...</p>
        </div>

        <template v-else>
            <div class="content">
                <div class="section order-section">
                    
                    <div class="section-header">
                        <h3>订单总金额</h3>
                        <span class="total-amount">&#165;{{ originalOrderTotal.toFixed(2) }}</span>
                    </div>

                    <div class="section-header points-deduct-section" 
                         v-if="maxDeductibleAmount > 0 && availablePoints > 0">
                        
                        <div class="points-info">
                            <h3 class="points-label">
                                积分抵扣
                                <span class="points-available">({{ availablePoints }} 积分可用)</span>
                            </h3>
                            <span class="discount-amount" :class="{ disabled: !usePoints }">
                                 - &#165;{{ actualDiscount.toFixed(2) }}
                            </span>
                        </div>
                        
                        <label class="switch">
                            <input type="checkbox" v-model="usePoints">
                            <span class="slider round"></span>
                        </label>
                    </div>
                    <div class="section-header final-amount-section">
                        <h3 class="final-label">实际支付金额</h3>
                        <span class="final-amount">&#165;{{ finalPaymentAmount }}</span>
                    </div>

                    
                    <div class="delivery-info">
                        <div class="info-item">
                            <i class="fa fa-map-marker"></i>
                            <span>{{ orderDetail.address || '未选择地址' }}</span>
                        </div>
                        <div class="info-item">
                            <i class="fa fa-user"></i>
                            <span>{{ orderDetail.contactName }} {{ orderDetail.contactSex === 1 ? '先生' : '女士' }}</span>
                        </div>
                        <div class="info-item">
                            <i class="fa fa-phone"></i>
                            <span>{{ orderDetail.contactTel }}</span>
                        </div>
                    </div>

                    <div class="section-header" @click="detailetShow">
                        <h3>订单详情</h3>
                        <i class="fa fa-angle-down" :class="{ rotate: isShowDetailet }"></i>
                    </div>

                    <div class="merchant-details" v-show="isShowDetailet">
                        <div class="merchant-info">
                            <img :src="orderDetail.businessImg" :alt="orderDetail.businessName" class="merchant-logo">
                            <div class="merchant-name">
                                {{ orderDetail.businessName || '未知商家' }}
                            </div>
                        </div>

                        <div class="order-details">
                            <template v-if="orderDetail.foodList && orderDetail.foodList.length > 0">
                                <div class="detail-item" v-for="item in orderDetail.foodList" :key="item.id">
                                    <span class="item-name">{{ item.foodName || '未知商品' }} &#165;{{ item.foodPrice }} &nbsp; × {{ item.quantity || 0 }}</span>
                                    <span class="item-price">&#165;{{ (item.foodPrice * item.quantity).toFixed(2) }}</span>
                                </div>
                            </template>
                            <div class="detail-item delivery-fee">
                                <span>配送费</span>
                                <span>&#165;{{ orderDetail.deliveryPrice.toFixed(2) || '0.00' }}</span>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="section payment-section">
                    <h3>选择支付方式</h3>
                    <div class="payment-options">
                        <div class="payment-option" :class="{ active: selectedPayment === 'wallet' }"
                            @click="selectPayment('wallet')">
                            <div class="wallet-icon">
                                <i class="fa fa-wallet"></i>&nbsp;&nbsp;
                                <span>虚拟钱包</span>
                            </div>
                            <i class="fa fa-check-circle"></i>
                        </div>
                        <div class="payment-option" :class="{ active: selectedPayment === 'alipay' }"
                            @click="selectPayment('alipay')">
                            <img src="../assets/alipay.png" alt="支付宝支付">
                            <i class="fa fa-check-circle"></i>
                        </div>
                        <div class="payment-option" :class="{ active: selectedPayment === 'wechat' }"
                            @click="selectPayment('wechat')">
                            <img src="../assets/wechat.png" alt="微信支付">
                            <i class="fa fa-check-circle"></i>
                        </div>
                    </div>
                </div>

                <div class="payment-action">
                    <button class="pay-button" @click="handlePayment" :disabled="paying">
                        <span v-if="paying">支付中...</span>
                        <span v-else>确认支付 &#165;{{ finalPaymentAmount }}</span>
                    </button>
                </div>
            </div>
        </template>

        </div>

    <div v-if="showWalletCreateModal" class="modal-overlay" @click.self="showWalletCreateModal = false">
        <div class="modal-content wallet-modal">
            <div class="modal-header">
                <h3>开通虚拟钱包</h3>
                <i class="fa fa-times close-btn" @click="showWalletCreateModal = false"></i>
            </div>
            <div class="modal-body">
                <p class="wallet-tip">您还没有虚拟钱包账户，是否现在开通？</p>
                <p class="wallet-desc">开通后即可使用钱包余额进行支付，享受便捷的支付体验。</p>
            </div>
            <div class="modal-footer">
                <button class="cancel-btn" @click="showWalletCreateModal = false">取消</button>
                <button class="confirm-btn" @click="handleCreateWallet" :disabled="creatingWallet">
                    <span v-if="creatingWallet">开通中...</span>
                    <span v-else>确认开通</span>
                </button>
            </div>
        </div>
    </div>

    <div v-if="showOverdraftConfirmModal" class="modal-overlay" @click.self="showOverdraftConfirmModal = false">
        <div class="modal-content overdraft-modal">
            <div class="modal-header">
                <h3>余额不足提示</h3>
                <i class="fa fa-times close-btn" @click="showOverdraftConfirmModal = false"></i>
            </div>
            <div class="modal-body">
                <div class="overdraft-info">
                    <i class="fa fa-exclamation-triangle warning-icon"></i>
                    <p class="overdraft-tip">钱包余额不足，可能需要透支</p>
                    <div class="balance-details">
                        <div class="balance-item">
                            <span class="label">当前余额：</span>
                            <span class="value">&#165;{{ walletInfo?.balance?.toFixed(2) || '0.00' }}</span>
                        </div>
                        <div class="balance-item">
                            <span class="label">订单金额：</span>
                            <span class="value amount">&#165;{{ finalPaymentAmount }}</span>
                        </div>
                        <div class="balance-item">
                            <span class="label">透支额度：</span>
                            <span class="value">&#165;{{ walletInfo?.overdraftLimit?.toFixed(2) || '0.00' }}</span>
                        </div>
                        <div class="balance-item">
                            <span class="label">已透支：</span>
                            <span class="value">&#165;{{ walletInfo?.overdrawnAmount?.toFixed(2) || '0.00' }}</span>
                        </div>
                    </div>
                    <p class="overdraft-desc">继续支付将使用透支功能，可能会产生额外费用</p>
                </div>
            </div>
            <div class="modal-footer">
                <button class="cancel-btn" @click="cancelOverdraftPayment">取消支付</button>
                <button class="confirm-btn" @click="confirmOverdraftPayment" :disabled="paying">
                    <span v-if="paying">支付中...</span>
                    <span v-else>确认支付</span>
                </button>
            </div>
        </div>
    </div>
</template>
  
<script>
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import request from '../utils/request';
import { toast } from '../utils/toast';
import BackButton from '../components/BackButton.vue';

// --- 【新增：积分逻辑相关的 API 方法】 ---
const fetchPointsAccount = async () => {
    try {
        const res = await request.get('/api/points/account');
        if (res.success && res.data) {
            return res.data.availablePoints || 0;
        }
    } catch (error) {
        console.error('查询积分账户失败:', error);
    }
    return 0;
};

const calculateDeductibleAmount = async (orderAmount) => {
    if (!orderAmount || orderAmount <= 0) {
        return 0;
    }
    try {
        const res = await request.get('/api/points/deductible-amount', {
            params: { orderAmount: orderAmount }
        });
        
        if (res.success && typeof res.data === 'number') {
            return Math.min(res.data, orderAmount); 
        }
    } catch (error) {
        console.error('计算可抵扣金额失败:', error);
    }
    return 0;
};
// --- 【新增：积分逻辑相关的 API 方法 结束】 ---


export default {
    name: 'Payment',
    components: {
        BackButton
    },
    setup() {
        const orderDetail = ref(null);
        const isShowDetailet = ref(true);
        const route = useRoute();
        const router = useRouter();
        const orderId = ref();
        const loading = ref(true);
        const selectedPayment = ref('wallet'); 
        const paying = ref(false);
        const showWalletCreateModal = ref(false);
        const creatingWallet = ref(false);
        const walletExists = ref(false);
        const showOverdraftConfirmModal = ref(false);
        const walletInfo = ref(null);

        // --- 【新增：积分相关状态】 ---
        const availablePoints = ref(0);
        const maxDeductibleAmount = ref(0); // 最大可抵扣金额 (由后端计算)
        const usePoints = ref(true); // 是否使用积分抵扣 (默认开启)
        // --- 【新增：积分相关状态 结束】 ---


        // --- 【新增：计算属性 - 订单金额】 ---
        
        // 订单原始总金额 (含配送费)
        const originalOrderTotal = computed(() => {
            return orderDetail.value?.orderTotal || 0;
        });

        // 实际的优惠金额 (如果用户选择使用积分，则应用最大可抵扣金额)
        const actualDiscount = computed(() => {
            // 确保只有在订单和积分数据加载完毕后才计算
            if (!orderDetail.value || !availablePoints.value) return 0;
            return usePoints.value ? maxDeductibleAmount.value : 0;
        });

        // 最终需要现金支付的金额 (用于UI展示和支付接口)
        const finalPaymentAmount = computed(() => {
            // 最终支付金额 = 原始总金额 - 实际优惠金额
            const amount = originalOrderTotal.value - actualDiscount.value;
            // 确保不为负数，并保留两位小数
            return Math.max(0, amount).toFixed(2);
        });
        
        // --- 【新增：计算属性 结束】 ---


        // --- 【新增：积分数据初始化逻辑】 ---
        const initPointsLogic = async (orderTotal) => {
            if (!orderTotal || orderTotal <= 0) {
                loading.value = false;
                return;
            }

            // 1. 获取用户可用积分
            const points = await fetchPointsAccount();
            availablePoints.value = points;

            if (points > 0) {
                // 2. 如果有积分，计算最大可抵扣金额
                const deductible = await calculateDeductibleAmount(orderTotal);
                maxDeductibleAmount.value = deductible;
                
                // 3. 默认开启积分抵扣
                usePoints.value = true;
            } else {
                // 没有积分则重置状态
                maxDeductibleAmount.value = 0;
                usePoints.value = false;
            }
            loading.value = false; // 积分加载完成后再解除整个页面的 loading
        };
        // --- 【新增：积分数据初始化逻辑 结束】 ---

        // 获取订单详情
        const fetchOrderDetails = async () => {
            try {
                const response = await request.get("/api/orders/detail", {
                    params: { orderId: orderId.value }
                });
                
                if (response && response.success) {
                    orderDetail.value = response.data;
                    // 在订单详情获取成功后，初始化积分逻辑
                    await initPointsLogic(response.data.orderTotal); 
                } else {
                    console.error('获取订单详情失败:', response.data?.message);
                    toast.error("获取订单信息失败，请重试！");
                    router.push({ path: '/userAddress' });
                }
            } catch (error) {
                console.error('请求错误:', error);
                toast.error("获取订单信息失败，请重试！");
                router.push({ path: '/userAddress' });
            } 
        };

        // 检查钱包是否存在（后端）
        const checkWalletExists = async () => {
            try {
                const response = await request.get("/api/wallet/message");
                
                if (response && response.success && response.data) {
                    walletExists.value = true;
                    walletInfo.value = response.data; 
                    return true;
                }
                
                if (response && response.code === 'VIRTUAL_WALLET_MISSED') {
                    walletExists.value = false;
                    walletInfo.value = null;
                    return false;
                }
                
                walletExists.value = false;
                walletInfo.value = null;
                return false;
            } catch (error) {
                console.error('检查钱包失败:', error);
                walletExists.value = false;
                walletInfo.value = null;
                return false;
            }
        };

        // 获取钱包余额信息
        const fetchWalletBalance = async () => {
            try {
                const response = await request.get("/api/wallet/message");
                if (response && response.success) {
                    walletInfo.value = response.data;
                    return response.data;
                }
                return null;
            } catch (error) {
                console.error('获取钱包余额失败:', error);
                return null;
            }
        };

        // 创建钱包（后端）
        const handleCreateWallet = async () => {
            try {
                creatingWallet.value = true;
                const response = await request.get("/api/wallet/open");
                if (response && response.success) {
                    walletExists.value = true;
                    showWalletCreateModal.value = false;
                    toast.success('钱包开通成功');
                    await fetchWalletBalance();
                } else {
                    toast.error("钱包开通失败：" + (response?.message || '未知错误'));
                }
            } catch (error) {
                console.error('开通钱包失败:', error);
                toast.error("钱包开通失败，请重试");
            } finally {
                creatingWallet.value = false;
            }
        };

        // 钱包支付（后端）
        const performWalletPayment = async () => {
            try {
                paying.value = true;
                // 注意：在实际项目中，这里需要告知后端最终的支付金额和积分使用情况
                const response = await request.get("/api/wallet/transaction/payment", {
                    params: { 
                        orderId: orderId.value,
                        // 建议与后端约定传入以下参数
                        finalAmount: finalPaymentAmount.value, // 最终支付金额
                        pointsDeduction: actualDiscount.value, // 积分抵扣金额
                    }
                });

                if (response && response.success) {
                    router.push({
                        path: '/successfulPayment',
                        query: { orderId: orderId.value }
                    });
                } else {
                    toast.error("钱包支付失败：" + (response?.message || '余额不足或账户异常'));
                }
            } catch (error) {
                console.error('钱包支付失败:', error);
                toast.error("钱包支付失败，请重试！");
            } finally {
                paying.value = false;
            }
        };

        // 检查余额是否足够
        const checkBalanceSufficient = () => {
            if (!walletInfo.value || !orderDetail.value) {
                return false;
            }
            const balance = walletInfo.value.balance || 0;
            // 【修改点 4】：检查余额时使用最终支付金额
            const finalAmount = parseFloat(finalPaymentAmount.value);
            return balance >= finalAmount;
        };

        // 处理钱包支付前的余额检查
        const handleWalletPayment = async () => {
            const walletData = await fetchWalletBalance();
            if (!walletData) {
                toast.error("获取钱包信息失败");
                return;
            }

            if (checkBalanceSufficient()) {
                await performWalletPayment();
            } else {
                // 余额不足，显示透支确认对话框
                showOverdraftConfirmModal.value = true;
            }
        };

        // 确认透支支付
        const confirmOverdraftPayment = async () => {
            showOverdraftConfirmModal.value = false;
            await performWalletPayment();
        };

        // 取消透支支付
        const cancelOverdraftPayment = () => {
            showOverdraftConfirmModal.value = false;
            paying.value = false;
            toast.info("已取消支付");
        };

        // 支付处理
        const handlePayment = async () => {
            if (paying.value) return;

            if (selectedPayment.value === 'wallet') {
                const exists = await checkWalletExists();
                if (!exists) { 
                    showWalletCreateModal.value = true; 
                    return; 
                }
                await handleWalletPayment();
            } else {
                // 第三方支付逻辑
                try {
                    paying.value = true;
                    // 【注意】：此处是原有的PUT请求，如果是真实的第三方支付，
                    // 应调用生成支付链接的接口，并传入 finalPaymentAmount.value
                    const response = await request.put("/api/orders/status?orderState=1&orderId=" + orderId.value);
                    if (response && response.success) {
                        router.push({
                            path: '/successfulPayment',
                            query: { orderId: orderId.value }
                        });
                    } else {
                        toast.error("支付失败" + response.data.message);
                    }
                } catch (error) {
                    console.error('支付失败:', error);
                    toast.error("支付失败，请重试！");
                } finally {
                    paying.value = false;
                }
            }
        };

        const selectPayment = (type) => {
            selectedPayment.value = type;
        };
        
        const detailetShow = () => {
            isShowDetailet.value = !isShowDetailet.value;
        };

        onMounted(() => {
            orderId.value = route.query.orderId;
            fetchOrderDetails(); // 统一在 fetchOrderDetails 中处理 loading 和积分初始化
            checkWalletExists(); // 提前检查钱包状态
        });

        return {
            orderId,
            orderDetail,
            isShowDetailet,
            detailetShow,
            handlePayment,
            loading,
            selectedPayment,
            selectPayment,
            paying,
            showWalletCreateModal,
            creatingWallet,
            handleCreateWallet,
            showOverdraftConfirmModal,
            walletInfo,
            confirmOverdraftPayment,
            cancelOverdraftPayment,
            
            // 积分和金额相关
            availablePoints,
            maxDeductibleAmount,
            usePoints,
            actualDiscount,
            finalPaymentAmount,
            originalOrderTotal,
        };
    }
}
</script>
  
<style scoped>
/****************** 总容器 ******************/
.wrapper {
    min-height: 100vh;
    background-color: #f5f7fa;
}

/****************** header部分 ******************/
.wrapper header {
    width: 100%;
    height: 12vw;
    background-color: #0097FF;
    color: #fff;
    font-size: 4.8vw;
    position: fixed;
    left: 0;
    top: 0;
    z-index: 1000;
    display: flex;
    justify-content: center;
    align-items: center;
}

.content {
    padding-top: 14vw;
    padding-bottom: 32vw;
}

.section {
    background: white;
    border-radius: 3vw;
    margin: 3vw;
    padding: 4vw;
    box-shadow: 0 0.2vw 1vw rgba(0, 0, 0, 0.05);
}

.section-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 4vw;
}

.section-header h3 {
    font-size: 4.2vw;
    color: #333;
    font-weight: 500;
    margin: 0;
}

.total-amount {
    font-size: 5vw;
    color: #ff6b00;
    font-weight: bold;
}

/* 配送信息样式 */
.delivery-info {
    margin-bottom: 4vw;
    padding-bottom: 3vw;
    border-bottom: 1px solid #f0f0f0;
}

.info-item {
    display: flex;
    align-items: center;
    margin-bottom: 2vw;
    font-size: 3.6vw;
    color: #666;
}

.info-item i {
    margin-right: 2vw;
    color: #0097FF;
    width: 5vw;
    text-align: center;
}

.merchant-info {
    display: flex;
    align-items: center;
    padding: 3vw 0;
    cursor: pointer;
}

.merchant-logo {
    width: 12vw;
    height: 12vw;
    border-radius: 2vw;
    object-fit: cover;
    margin-right: 3vw;
}

.merchant-name {
    flex: 1;
    font-size: 4vw;
    color: #333;
    display: flex;
    align-items: center;
    gap: 2vw;
}

.fa-angle-down {
    transition: transform 0.3s ease;
}

.fa-angle-down.rotate {
    transform: rotate(180deg);
}

.order-details {
    margin-top: 3vw;
    padding-top: 3vw;
    border-top: 0.2vw solid #f5f7fa;
}

.detail-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 2vw 0;
    font-size: 3.6vw;
    color: #666;
}

.delivery-fee {
    border-top: 0.2vw dashed #eee;
    margin-top: 2vw;
    padding-top: 2vw;
    color: #333;
    font-weight: bold;
}

.payment-options {
    display: flex;
    gap: 10vw;
    margin-top: 4vw;
    display: inline-block;
}

.payment-option {
    flex: 1;
    padding: 4vw;
    border: 0.2vw solid #eee;
    border-radius: 2vw;
    display: flex;
    align-items: center;
    justify-content: space-between;
    cursor: pointer;
    transition: all 0.3s ease;
    background: #f9f9f9;
    width: 80vw;
}

.payment-option img {
    height: 8vw;
    width: auto;
    object-fit: contain;
}

.payment-option .wallet-icon {
    display: flex;
    align-items: center;
    gap: 2vw;
}

.payment-option .wallet-icon i {
    font-size: 6vw;
    color: #0097FF;
}

.payment-option .wallet-icon span {
    font-size: 3.6vw;
    color: #333;
    font-weight: 500;
}

.payment-option .fa-check-circle {
    font-size: 5vw;
    color: #ddd;
    transition: all 0.3s ease;
}

.payment-option.active {
    border-color: #38CA73;
    background: #f0fff5;
}

.payment-option.active .fa-check-circle {
    color: #38CA73;
}

.payment-action {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    padding: 4vw;
    background: white;
    box-shadow: 0 -0.2vw 1vw rgba(0, 0, 0, 0.05);
}

.pay-button {
    width: 100%;
    height: 12vw;
    border: none;
    border-radius: 6vw;
    background: linear-gradient(to right, #38CA73, #2EAF62);
    color: white;
    font-size: 4.2vw;
    font-weight: bold;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    transition: all 0.3s ease;
}

.pay-button:disabled {
    background: #ccc;
    cursor: not-allowed;
}

.pay-button:not(:disabled):active {
    transform: scale(0.98);
}

.loading {
    width: 100%;
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 4vw;
    color: #666;
}
/* 1. 给 BackButton 父容器加固定定位，与 header 对齐 */
.back-btn-container {
    position: fixed; /* 固定定位，不随滚动移动 */
    left: 0vw; /* 距离左侧的距离，可根据需求调整 */
    top: 0vw; /* 距离顶部的距离，与 header 高度（12vw）适配，确保垂直居中 */
    z-index: 1001; /* 比 header 的 z-index:1000 高，避免被遮挡 */
}

/* 2. 样式穿透：确保 BackButton 内部图标/文字正常显示（可选，根据组件内部结构调整） */
/* 暂不添加，假设 BackButton 样式已处理 */


/* 钱包开通对话框样式 */
.modal-overlay {
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 2000;
}

.wallet-modal {
    background: white;
    border-radius: 4vw;
    width: 85%;
    max-width: 500px;
}

.wallet-modal .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 4vw;
    border-bottom: 1px solid #f0f0f0;
}

.wallet-modal .modal-header h3 {
    font-size: 4.5vw;
    margin: 0;
    color: #333;
}

.wallet-modal .close-btn {
    font-size: 5vw;
    color: #999;
    cursor: pointer;
}

.wallet-modal .modal-body {
    padding: 4vw;
}

.wallet-tip {
    font-size: 4vw;
    color: #333;
    margin-bottom: 3vw;
    text-align: center;
}

.wallet-desc {
    font-size: 3.6vw;
    color: #666;
    line-height: 1.6;
    text-align: center;
}

.wallet-modal .modal-footer {
    display: flex;
    gap: 3vw;
    padding: 4vw;
    border-top: 1px solid #f0f0f0;
}

.wallet-modal .modal-footer button {
    flex: 1;
    padding: 3.5vw;
    border: none;
    border-radius: 2vw;
    font-size: 4vw;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s;
}

.wallet-modal .cancel-btn {
    background: #f5f5f5;
    color: #666;
}

.wallet-modal .cancel-btn:active {
    background: #e0e0e0;
}

.wallet-modal .confirm-btn {
    background: #0097FF;
    color: white;
}

.wallet-modal .confirm-btn:active {
    background: #0080e6;
}

.wallet-modal .confirm-btn:disabled {
    background: #ccc;
    cursor: not-allowed;
}

/* 透支确认对话框样式 */
.overdraft-modal {
    background: white;
    border-radius: 4vw;
    width: 85%;
    max-width: 500px;
}

.overdraft-modal .modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 4vw;
    border-bottom: 1px solid #f0f0f0;
}

.overdraft-modal .modal-header h3 {
    font-size: 4.5vw;
    margin: 0;
    color: #333;
}

.overdraft-modal .close-btn {
    font-size: 5vw;
    color: #999;
    cursor: pointer;
}

.overdraft-modal .modal-body {
    padding: 4vw;
}

.overdraft-info {
    text-align: center;
}

.warning-icon {
    font-size: 10vw;
    color: #faad14;
    margin-bottom: 3vw;
}

.overdraft-tip {
    font-size: 4.2vw;
    color: #333;
    font-weight: 500;
    margin-bottom: 4vw;
}

.balance-details {
    background: #f8f9fa;
    border-radius: 2vw;
    padding: 3vw;
    margin-bottom: 3vw;
}

.balance-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 2vw 0;
    font-size: 3.6vw;
    color: #666;
    border-bottom: 1px solid #f0f0f0;
}

.balance-item:last-child {
    border-bottom: none;
}

.balance-item .label {
    color: #999;
}

.balance-item .value {
    color: #333;
    font-weight: 500;
}

.balance-item .value.amount {
    color: #ff4d4f;
    font-weight: bold;
}

.overdraft-desc {
    font-size: 3.4vw;
    color: #faad14;
    line-height: 1.6;
    text-align: center;
}

.overdraft-modal .modal-footer {
    display: flex;
    gap: 3vw;
    padding: 4vw;
    border-top: 1px solid #f0f0f0;
}

.overdraft-modal .modal-footer button {
    flex: 1;
    padding: 3.5vw;
    border: none;
    border-radius: 2vw;
    font-size: 4vw;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s;
}

.overdraft-modal .cancel-btn {
    background: #f5f5f5;
    color: #666;
}

.overdraft-modal .cancel-btn:active {
    background: #e0e0e0;
}

.overdraft-modal .confirm-btn {
    background: #ff4d4f;
    color: white;
}

.overdraft-modal .confirm-btn:active {
    background: #d9363e;
}

.overdraft-modal .confirm-btn:disabled {
    background: #ccc;
    cursor: not-allowed;
}


/* 【新增：积分抵扣区域样式】 */
.points-deduct-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 3vw 0;
    border-top: 1px solid #f0f0f0;
    margin-bottom: 2vw; /* 调整与下方最终金额的间距 */
}

.points-info {
    display: flex;
    align-items: center;
    flex-grow: 1;
}

.points-label {
    font-size: 3.8vw;
    color: #0097FF; /* 突出积分颜色 */
    font-weight: 500;
    margin: 0;
}

.points-available {
    font-size: 3.2vw;
    color: #999;
    margin-left: 2vw;
}

.discount-amount {
    font-size: 4.2vw;
    color: #FF6B00; /* 突出优惠金额 */
    font-weight: bold;
    margin-left: auto; /* 推到右侧 */
    margin-right: 4vw;
    transition: color 0.3s ease;
}

.discount-amount.disabled {
    color: #ccc;
    text-decoration: line-through;
}

/* 最终支付金额区域样式 */
.final-amount-section {
    padding-top: 2vw;
    border-top: 1px dashed #eee;
    margin-top: 2vw;
    margin-bottom: 0;
}

.final-label {
    font-size: 4.5vw;
    color: #333;
    font-weight: bold;
    margin: 0;
}

.final-amount {
    font-size: 6vw;
    color: #E51C23; /* 红色突出最终支付金额 */
    font-weight: bold;
}


/* Switch 切换开关样式 (参考 iOS 风格) */
.switch {
  position: relative;
  display: inline-block;
  width: 10vw;
  height: 6vw;
}

.switch input { 
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  -webkit-transition: .4s;
  transition: .4s;
  border-radius: 3.4vw; /* 圓角 */
}

.slider:before {
  position: absolute;
  content: "";
  height: 4.8vw;
  width: 4.8vw;
  left: 0.6vw;
  bottom: 0.6vw;
  background-color: white;
  -webkit-transition: .4s;
  transition: .4s;
  border-radius: 50%; /* 圓形按鈕 */
}

input:checked + .slider {
  background-color: #38CA73; /* 开启时的颜色 */
}

input:focus + .slider {
  box-shadow: 0 0 1px #38CA73;
}

input:checked + .slider:before {
  -webkit-transform: translateX(4vw);
  -ms-transform: translateX(4vw);
  transform: translateX(4vw);
}
</style>