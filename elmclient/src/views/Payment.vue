<template>
	<div class="back-btn-container">
    <BackButton style="margin-top: 2vw;"/>
  </div>
	<div class="wrapper">
		<!-- header部分 -->
		<header>
			<p>在线支付</p>
		</header>

		<!-- 加载中提示 -->
		<div v-if="loading" class="loading">
			<p>加载中...</p>
		</div>

		<template v-else>
			<div class="content">
				<!-- 订单信息部分 -->
				<div class="section order-section">
					<div class="section-header">
						<h3>订单基本信息</h3>
						<span class="total-amount">&#165;{{ orderDetail.orderTotal || '0.00' }}</span>
					</div>
					
					<!-- 配送信息 -->
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

					<div class="section-header">
						<h3>订单详情</h3>
					</div>

					<!-- 商家信息和订单明细部分 -->
					<div class="merchant-details" v-show="isShowDetailet">
						<div class="merchant-info">
							<img :src="orderDetail.businessImg" :alt="orderDetail.businessName" class="merchant-logo">
							<div class="merchant-name">
								{{ orderDetail.businessName || '未知商家' }}
							</div>
						</div>

						<!-- 订单明细部分 -->
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

				<!-- 支付方式部分 -->
				<div class="section payment-section">
					<h3>选择支付方式</h3>
					<div class="payment-options">
						<div class="payment-option" :class="{ active: selectedPayment === 'wallet' }"
							@click="selectPayment('wallet')">
							<div class="wallet-icon">
								<i class="fa fa-wallet"></i>
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

				<!-- 支付按钮 -->
				<div class="payment-action">
					<button class="pay-button" @click="handlePayment">
						<span v-if="paying">支付中...</span>
						<span v-else>确认支付 &#165;{{ orderDetail?.orderTotal || '0.00' }}</span>
					</button>
				</div>
			</div>
		</template>

		<!-- 底部菜单部分 -->
	</div>

	<!-- 钱包开通对话框 -->
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
</template>
  
<script>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import request from '../utils/request';
import { toast } from '../utils/toast';
import BackButton from '../components/BackButton.vue';

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
		const selectedPayment = ref('wallet'); // 默认选择钱包支付
		const paying = ref(false);
		const deliveryPrice = ref(5); // 默认配送费，可根据实际情况调整
		const showWalletCreateModal = ref(false); // 钱包开通对话框
		const creatingWallet = ref(false); // 正在创建钱包
		const walletExists = ref(false); // 钱包是否存在

		const getCurrentUser = () => {
			try {
				const localUser = localStorage.getItem('userInfo');
				if (localUser) return JSON.parse(localUser);
				const sessionUser = sessionStorage.getItem('userInfo');
				if (sessionUser) return JSON.parse(sessionUser);
			} catch (error) {
				console.error('解析用户信息失败:', error);
			}
			return null;
		};

		const getWalletInfoKey = () => {
			const user = getCurrentUser();
			return user ? `walletInfo_${user.id}` : 'walletInfo_guest';
		};

		const getWalletTransactionsKey = () => {
			const user = getCurrentUser();
			return user ? `walletTransactions_${user.id}` : 'walletTransactions_guest';
		};

		const addTransactionRecord = (record) => {
			try {
				const transactions = JSON.parse(localStorage.getItem(getWalletTransactionsKey()) || '[]');
				const transaction = {
					id: Date.now(),
					transactionTime: new Date().toISOString(),
					...record
				};
				transactions.unshift(transaction);
				localStorage.setItem(getWalletTransactionsKey(), JSON.stringify(transactions));
			} catch (error) {
				console.error('保存交易记录失败:', error);
			}
		};

		// 获取订单详情
		const fetchOrderDetails = async () => {
			try {
				// 使用动态的orderId，而不是硬编码的24
				const response = await request.get("/api/orders/detail", {
					params: { orderId: orderId.value }
				});
				
				console.log("订单详情响应:", response);
				
				if (response.success) {
					// 正确的数据访问方式
					orderDetail.value = response.data;
				} else {
					console.error('获取订单详情失败:', response.data?.message);
					toast.error("获取订单信息失败，请重试！");
					router.push({ path: '/userAddress' });
				}
			} catch (error) {
				console.error('请求错误:', error);
				toast.error("获取订单信息失败，请重试！");
				router.push({ path: '/userAddress' });
			} finally {
				loading.value = false;
			}
		};

		// 检查钱包是否存在
		const checkWalletExists = async () => {
			try {
				// ========== 前端模拟模式（用于测试，不连接后端） ==========
				const savedWalletInfo = localStorage.getItem(getWalletInfoKey());
				if (savedWalletInfo) {
					walletExists.value = true;
					return true;
				} else {
					walletExists.value = false;
					return false;
				}
				// ========== 前端模拟模式结束 ==========
				
				// ========== 后端调用逻辑（已注释，需要时取消注释） ==========
				// const response = await request.get("/api/wallet/info");
				// if (response && response.success) {
				//   walletExists.value = true;
				//   return true;
				// } else {
				//   walletExists.value = false;
				//   return false;
				// }
				// ========== 后端调用逻辑结束 ==========
			} catch (error) {
				console.error('检查钱包失败:', error);
				// ========== 前端模拟模式 ==========
				walletExists.value = false;
				return false;
				// ========== 后端调用逻辑（已注释） ==========
				// // 404 或其他错误，说明钱包不存在或接口不存在
				// const status = error.response?.status;
				// const message = error.message || '';
				// const responseData = error.response?.data || {};
				// 
				// if (status === 404 || 
				//   message.includes('No static resource') || 
				//   message.includes('api/wallet') ||
				//   JSON.stringify(responseData).includes('No static resource')) {
				//   walletExists.value = false;
				//   return false;
				// }
				// console.error('检查钱包失败:', error);
				// return false;
				// ========== 后端调用逻辑结束 ==========
			}
		};

		// 创建钱包
		const handleCreateWallet = async () => {
			// ========== 前端模拟模式（用于测试，不连接后端） ==========
			try {
				creatingWallet.value = true;
				
				// 模拟延迟，让用户看到"开通中"的状态
				await new Promise(resolve => setTimeout(resolve, 500));
				
				const newWalletInfo = {
					balance: 0,
					isVip: false,
					overdraftLimit: 0,
					usedOverdraft: 0
				};
				
				// 保存到localStorage
				try {
					localStorage.setItem(getWalletInfoKey(), JSON.stringify(newWalletInfo));
				} catch (storageError) {
					console.error('保存钱包信息到localStorage失败:', storageError);
				}
				
				walletExists.value = true;
				showWalletCreateModal.value = false;
				toast.success('钱包开通成功');
				addTransactionRecord({
					transactionType: 'create',
					amount: 0,
					reason: '钱包开通成功'
				});
				
				// 钱包开通后，不自动进行支付（支付需要用户手动操作）
				// await performWalletPayment();
				// ========== 前端模拟模式结束 ==========
				return; // 直接返回，不执行后面的代码
				
				// ========== 后端调用逻辑（已注释，需要时取消注释） ==========
				// const response = await request.post("/api/wallet/create");
				// if (response && response.success) {
				//   walletExists.value = true;
				//   showWalletCreateModal.value = false;
				//   toast.success('钱包开通成功');
				//   // 钱包开通后，自动进行支付
				//   await performWalletPayment();
				// } else {
				//   toast.error("钱包开通失败：" + (response?.message || '未知错误'));
				// }
				// ========== 后端调用逻辑结束 ==========
				
				// ========== 后端调用逻辑（已注释，需要时取消注释） ==========
				// try {
				//   const response = await request.post("/api/wallet/create");
				//   if (response && response.success) {
				//     walletExists.value = true;
				//     showWalletCreateModal.value = false;
				//     toast.success('钱包开通成功');
				//     // 钱包开通后，自动进行支付
				//     await performWalletPayment();
				//   } else {
				//     toast.error("钱包开通失败：" + (response?.message || '未知错误'));
				//   }
				// } catch (error) {
				//   console.error('创建钱包失败:', error);
				//   // 检查是否是接口不存在的错误
				//   const status = error.response?.status;
				//   const message = error.message || '';
				//   const responseData = error.response?.data || {};
				//   
				//   if (status === 404 || 
				//     message.includes('No static resource') || 
				//     message.includes('api/wallet') ||
				//     JSON.stringify(responseData).includes('No static resource')) {
				//     toast.error("钱包开通失败：后端接口未实现，请联系管理员");
				//   } else {
				//     toast.error("钱包开通失败：" + (error.response?.data?.message || '请重试'));
				//   }
				// }
				// ========== 后端调用逻辑结束 ==========
			} catch (error) {
				console.error('创建钱包失败:', error);
				toast.error("钱包开通失败，请重试");
			} finally {
				creatingWallet.value = false;
			}
		};

		// 执行钱包支付
		const performWalletPayment = async () => {
			// ========== 前端模拟模式（用于测试，不连接后端） ==========
			try {
				paying.value = true;
				
				// 模拟延迟
				await new Promise(resolve => setTimeout(resolve, 500));
				
				// 检查钱包余额
				const savedWalletInfo = localStorage.getItem(getWalletInfoKey());
				let walletData = savedWalletInfo ? JSON.parse(savedWalletInfo) : {
					balance: 0,
					isVip: false,
					overdraftLimit: 0,
					usedOverdraft: 0
				};
				
				const payAmount = orderDetail.value?.orderTotal || 0;
				
				// 检查余额是否足够（包括透支额度）
				const availableBalance = walletData.balance + (walletData.isVip ? (walletData.overdraftLimit - walletData.usedOverdraft) : 0);
				
				if (availableBalance < payAmount) {
					toast.error("钱包支付失败：余额不足");
					return;
				}
				
				// 扣除余额
				let usedOverdraftAmount = 0;
				if (walletData.balance >= payAmount) {
					walletData.balance -= payAmount;
				} else {
					// 使用透支额度
					const needOverdraft = payAmount - walletData.balance;
					walletData.balance = 0;
					walletData.usedOverdraft = (walletData.usedOverdraft || 0) + needOverdraft;
					usedOverdraftAmount = needOverdraft;
				}
				
				// 保存钱包信息
				localStorage.setItem(getWalletInfoKey(), JSON.stringify(walletData));

				addTransactionRecord({
					transactionType: 'payment',
					amount: -payAmount,
					orderId: orderId.value,
					reason: `订单支付${usedOverdraftAmount > 0 ? `，使用透支 ¥${usedOverdraftAmount.toFixed(2)}` : ''}`
				});
				
				// 支付成功，跳转到成功页面
				router.push({
					path: '/successfulPayment',
					query: { orderId: orderId.value }
				});
				// ========== 前端模拟模式结束 ==========
				return; // 直接返回，不执行后面的代码
				
				// ========== 后端调用逻辑（已注释，需要时取消注释） ==========
				// const response = await request.post("/api/wallet/pay", {
				//   orderId: orderId.value,
				//   amount: orderDetail.value.orderTotal
				// });
				// if (response && response.success) {
				//   // 支付成功，跳转到成功页面
				//   router.push({
				//     path: '/successfulPayment',
				//     query: { orderId: orderId.value }
				//   });
				// } else {
				//   toast.error("钱包支付失败：" + (response?.message || '余额不足或账户异常'));
				// }
				// ========== 后端调用逻辑结束 ==========
			} catch (error) {
				console.error('钱包支付失败:', error);
				toast.error("钱包支付失败，请重试");
			} finally {
				paying.value = false;
			}
		};

		// 支付处理
		const handlePayment = async () => {
			if (selectedPayment.value === 'wallet') {
				// 钱包支付：先检查钱包是否存在
				const exists = await checkWalletExists();
				if (!exists) {
					// 钱包不存在，弹出开通对话框
					showWalletCreateModal.value = true;
					return;
				}
				// 钱包存在，直接支付
				await performWalletPayment();
			} else {
				// 原有的第三方支付逻辑
				try {
					paying.value = true;
					const response = await request.put("/api/orders/status?orderState=1&orderId=" + orderId.value);
					if (response.success) {
						// 支付成功，跳转到成功页面
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

		const detailetShow = () => {
			isShowDetailet.value = !isShowDetailet.value;
		};

		const selectPayment = (type) => {
			selectedPayment.value = type;
		};

		onMounted(() => {
			orderId.value = route.query.orderId;
			console.log("获取到的orderId:", orderId.value);
			fetchOrderDetails();
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
			deliveryPrice,
			showWalletCreateModal,
			creatingWallet,
			handleCreateWallet,
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
	gap: 3vw;
	margin-top: 4vw;
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
::v-deep .back-button { /* 这里的 .back-button 是 BackButton 组件根元素的类名，需与组件内部一致 */
  width: 8vw; /* 调整按钮大小，按需修改 */
  height: 8vw;
  color: #fff; /* 按钮颜色，与 header 白色文字匹配 */
  /* 如果组件内部是图标，可加图标大小控制 */
}

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
</style>