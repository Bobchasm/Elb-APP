<template>
	<div class="wrapper">
		<header class="topbar"><p>订单详情</p></header>
		<div class="content">
			<!-- 顶部下单人信息 -->
			<div class="user-info">
				<p><span>下单人：</span>{{ userName || '-' }}</p>
				<p><span>手机号：</span>{{ userPhone || '-' }}</p>
				<p><span>地址：</span>{{ userAddress || '-' }}</p>
				<p><span>状态：</span>{{ statusText }}</p>
			</div>

			<!-- 商家与订单信息 -->
			<div class="shop-info">
				<p class="shop-name">{{ shopName || '-' }}</p>
				<p class="shop-addr">{{ shopAddress || '地址未知' }}</p>
			</div>

			<!-- 菜品明细 -->
			<ul class="items">
				<li v-for="(it, idx) in items" :key="idx" class="row">
					<span class="food-name">{{ it.foodName }}</span>
					<span class="food-qty">× {{ it.quantity }}</span>
					<span class="food-price">¥ {{ Number(it.foodPrice).toFixed(2) }}</span>
				</li>
				<li class="row fee" v-if="deliveryPrice != null">
					<span class="food-name">配送费</span>
					<span class="spacer"></span>
					<span class="food-price">¥ {{ Number(deliveryPrice).toFixed(2) }}</span>
				</li>
			</ul>

			<!-- 合计 -->
			<div class="total">
				<span>合计</span>
				<strong>¥ {{ Number(total).toFixed(2) }}</strong>
			</div>
		</div>
	</div>
</template>

<script>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import axios from 'axios';

export default {
	name: 'ListDetail',
	setup() {
		const route = useRoute();
		const router = useRouter();
		const orderId = ref(null);
		const order = ref(null);
		const items = ref([]);
		const shopName = ref('');
		const shopAddress = ref('');
		const deliveryPrice = ref(0);
		const userName = ref('');
		const userPhone = ref('');
		const userAddress = ref('');
		const orderState = ref(null);

		const statusText = computed(() => {
			// 优先字符串状态
			if (order.value && typeof order.value.status === 'string') return order.value.status;
			if (orderState.value === 1) return '已接单';
			if (orderState.value === -1) return '已取消';
			if (orderState.value === 2) return '已完成';
			return '待支付';
		});

		onMounted(async () => {
			orderId.value = parseInt(route.query.orderId);
			if (!orderId.value) {
				router.push({ path: '/orderList' });
				return;
			}
			// 先尝试从 sessionStorage 回填（虚拟订单或接口不可用时）
			let stored = null;
			try { stored = JSON.parse(sessionStorage.getItem('selectedOrder') || 'null'); } catch (e) { stored = null; }
			if (stored && stored.orderId === orderId.value) {
				order.value = stored;
				orderState.value = order.value.orderState ?? null;
				userName.value = order.value.userName || (order.value.user && order.value.user.userName) || '';
				userPhone.value = order.value.userPhone || (order.value.user && order.value.user.userTel) || '';
				userAddress.value = order.value.userAddress || (order.value.user && order.value.user.userAddress) || '';
				if (order.value.business) {
					shopName.value = order.value.business.businessName || '';
					shopAddress.value = order.value.business.businessAddress || '';
					deliveryPrice.value = order.value.business.deliveryPrice ?? 0;
				}
				items.value = Array.isArray(order.value.detailet) ? order.value.detailet : [];
			}
			try {
				// 基本订单信息
				const orderResp = await axios.post('OrdersController/getOrderById', { orderId: orderId.value });
				if (orderResp && orderResp.data) {
					order.value = orderResp.data;
					orderState.value = order.value.orderState ?? orderState.value;
					// 顶部用户信息（兼容字段）
					userName.value = order.value.userName || (order.value.user && order.value.user.userName) || userName.value;
					userPhone.value = order.value.userPhone || (order.value.user && order.value.user.userTel) || userPhone.value;
					userAddress.value = order.value.userAddress || (order.value.user && order.value.user.userAddress) || userAddress.value;
				}

				// 商家信息
				if (order.value && order.value.business) {
					shopName.value = order.value.business.businessName || '';
					shopAddress.value = order.value.business.businessAddress || '';
					deliveryPrice.value = order.value.business.deliveryPrice ?? 0;
				}

				// 明细
				try {
					const detailResp = await axios.post('OrdersController/listOrderDetailetByOrderId', { orderId: orderId.value });
					if (detailResp && Array.isArray(detailResp.data)) {
						items.value = detailResp.data;
					} else if (!items.value.length && stored && Array.isArray(stored.detailet)) {
						items.value = stored.detailet;
					}
				} catch (e) {
					// 使用本地存储明细兜底
					if (!items.value.length && stored && Array.isArray(stored.detailet)) {
						items.value = stored.detailet;
					}
				}

				// 如果总价或配送费缺失，尽量从订单对象兜底
				if ((deliveryPrice.value == null || isNaN(deliveryPrice.value)) && order.value.business) {
					deliveryPrice.value = order.value.business.deliveryPrice ?? 0;
				}
			} catch (e) {
				console.warn('接口获取失败，已尝试使用本地数据', e);
				if (!order.value) {
					alert('加载订单详情失败');
				}
			}
		});

		const itemsTotal = computed(() => items.value.reduce((sum, it) => sum + Number(it.foodPrice) * Number(it.quantity), 0));
		const total = computed(() => Number(itemsTotal.value) + Number(deliveryPrice.value || 0));

		return {
			orderId,
			order,
			items,
			shopName,
			shopAddress,
			deliveryPrice,
			userName,
			userPhone,
			userAddress,
			statusText,
			total
		};
	}
};
</script>

<style scoped>
.wrapper {
	width: 100%;
	min-height: 100vh;
	background: #fff;
}
.topbar {
	width: 100%;
	height: 12vw;
	background-color: #409eff;
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
	margin-top: 12vw;
	padding: 4vw;
}
.user-info p {
	font-size: 3.6vw;
	color: #333;
	margin: 1vw 0;
}
.user-info span {
	color: #999;
	margin-right: 1vw;
}
.shop-info {
	margin-top: 3vw;
	padding: 3vw;
	background: #fafafa;
	border-radius: 1.2vw;
}
.shop-name {
	font-size: 4vw;
	color: #333;
}
.shop-addr {
	font-size: 3.2vw;
	color: #888;
	margin-top: 1vw;
}
.items {
	margin-top: 4vw;
}
.row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 2.4vw 0;
	border-bottom: 1px solid #f2f2f2;
}
.food-name { font-size: 3.6vw; color: #333; }
.food-qty { font-size: 3.2vw; color: #666; }
.food-price { font-size: 3.6vw; color: #333; }
.fee .spacer { flex: 1; }
.total {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding-top: 3.2vw;
	font-size: 4vw;
}
.total strong { color: #1e80ff; }
</style>


