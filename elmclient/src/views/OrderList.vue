<template>
	<div class="wrapper">
		<!-- 顶部蓝色栏 -->
		<header class="topbar">
			<p>订单</p>
		</header>

		<!-- 页面标题 -->
		<div class="page-title">订单中心</div>

		<!-- 筛选标签栏 -->
		<ul class="tabs">
			<li
				v-for="(t, idx) in tabs"
				:key="t"
				:class="{ active: activeTab === idx }"
				@click="changeTab(idx)"
			>
				{{ t }} <span v-if="orderCounts[idx] > 0">({{ orderCounts[idx] }})</span>
			</li>
		</ul>

		<!-- 加载提示 -->
		<div v-if="loading" class="loading">
			<p>加载中...</p>
		</div>

		<!-- 空状态提示 -->
		<div v-else-if="displayedOrders.length === 0" class="empty-state">
			<img src="../assets/empty-order.png" alt="暂无订单">
			<p>暂无订单</p>
		</div>

		<!-- 订单列表 -->
		<ul v-else class="order-list">
			<li v-for="item in displayedOrders" :key="item.id" class="order-item" @click="goDetail(item.id)" title="查看详情">
				<div class="order-header">
					<span class="order-id">订单号: {{ item.id }}</span>
					<span class="status-badge" :class="getStatusClass(item.orderState)">{{ getStatusText(item.orderState) }}</span>
				</div>
				
				<div class="order-content">
					<img class="thumb" :src="item.businessImg" alt="商家图片">
					<div class="meta">
						<p class="name">{{ item.businessName || '未知商家' }}</p>
						<p class="price">¥ {{ Number(item.orderTotal).toFixed(2) }}</p><br>
						<p class="time">下单时间: {{ item.orderDate }}</p>
					</div>
				</div>
				
				<div class="actions">
					<!-- 待支付：取消 + 付款 -->
					<template v-if="item.orderState === 0">
						<button class="cancel-btn" @click.stop="cancelOrder(item.id)">取消订单</button>
						<button class="pay-btn" @click.stop="payOrder(item.id)">立即支付</button>
					</template>
					
					<!-- 待接单：取消订单 -->
					<template v-else-if="item.orderState === 1">
						<button class="cancel-btn" @click="cancelOrder(item.id)">取消订单</button>
					</template>
					
					<!-- 已接单：确认收货 -->
					<template v-else-if="item.orderState === 2">
						<button class="confirm-btn" @click.stop="confirmOrder(item.id)">确认收货</button>
					</template>
					
					<!-- 已完成/已取消：查看详情 -->
					<template v-else>
						<button class="detail-btn" @click="goDetail(item.id)">查看详情</button>
					</template>
				</div>
			</li>
		</ul>

		<!-- 底部导航 -->
		<Footer />
	</div>
</template>
  
<script>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import request from "../utils/request";
import Footer from '../components/Footer.vue';

export default {
  name: "OrderList",
  components: {
    Footer
  },
  setup() {
    const orderArr = ref([]);
    const userInfo = ref({});
    const router = useRouter();
    const loading = ref(false);
    
    // 标签定义 - 与API状态对应
    const tabs = ["全部", "待支付", "待接单", "已接单", "已完成", "已取消"];
    const activeTab = ref(0);
    
    // 标签对应的API状态值
    const tabStatusMap = {
      0: null,     // 全部
      1: 0,        // 待支付
      2: 1,        // 待接单
      3: 2,        // 已接单
      4: 3,        // 已完成
      5: 4         // 已取消
    };

    // 获取订单列表
    const fetchOrders = async (status = null) => {
      loading.value = true;
      try {
        const params = {};
        if (status !== null) {
          params.orderState = status;
        }
        
        const response = await request.get("/api/orders/list/user"
        + (status === null ? "" : ("?orderState=" + status)));
        
        if (response.success) {
          orderArr.value = response.data || [];
          console.log("获取订单列表成功:", orderArr.value);
        } else {
          console.error('获取订单列表失败:', response.data.message);
          alert('获取订单列表失败: ' + response.data.message);
        }
      } catch (error) {
        console.error("请求订单列表失败:", error);
        alert("获取订单列表失败，请稍后重试！");
      } finally {
        loading.value = false;
      }
    };


	// 计算各状态订单数量 - 基于完整订单列表
	const orderCounts = computed(() => {
  // 初始化一个数组，长度与tabs一致，初始值为0
  const counts = new Array(tabs.length).fill(0);
  
  // 获取完整的订单列表（从后端API获取的所有订单）
  const allOrders = orderArr.value;
  
  // 遍历所有订单进行统计
  allOrders.forEach(order => {
    const state = order.orderState;
    
    // 全部订单数
    counts[0]++; 
    
    // 根据订单状态，增加对应标签的计数
    if (state === 0) counts[1]++;       // 待支付 -> 索引1
    else if (state === 1) counts[2]++;  // 待接单 -> 索引2
    else if (state === 2) counts[3]++;  // 已接单 -> 索引3
    else if (state === 3) counts[4]++;  // 已完成 -> 索引4
    else if (state === 4) counts[5]++;  // 已取消 -> 索引5
  });
  
  return counts;
});


    // 计算显示的订单 - 基于当前选中的标签
    const displayedOrders = computed(() => {
      if (activeTab.value === 0) return orderArr.value; // 全部
      
      const targetStatus = tabStatusMap[activeTab.value];
      return orderArr.value.filter(order => order.orderState === targetStatus);
    });

    // 切换标签 - 只需要改变activeTab，displayedOrders会自动更新
    const changeTab = (index) => {
      activeTab.value = index;
      // 不再需要在这里调用fetchOrders，因为displayedOrders是计算属性
    };

    // 获取状态文本
    const getStatusText = (state) => {
      const statusMap = {
        0: "待支付",
        1: "待接单", 
        2: "已接单",
        3: "已完成",
        4: "已取消"
      };
      return statusMap[state] || "未知状态";
    };

    // 获取状态样式类
    const getStatusClass = (state) => {
      const classMap = {
        0: "unpaid",
        1: "pending",
        2: "accepted", 
        3: "done",
        4: "canceled"
      };
      return classMap[state] || "";
    };


    // 格式化时间
    const formatTime = (timeString) => {
      if (!timeString) return "";
      const date = new Date(timeString);
      return date.toLocaleString();
    };

    // 取消订单
    const cancelOrder = async (id) => {
      if (!confirm("确定要取消此订单吗？")) return;
      
      try {
        const response = await request.put("/api/orders/status?orderState=4&orderId=" + id);
        
        if (response.success) {
          alert("订单取消成功");
          // 重新加载当前标签的订单
          fetchOrders();
          router.push({path: '/orderList'});
        } else {
          alert("取消失败: " + response.message);
          router.push({path: '/orderList'});
        }
      } catch (error) {
        console.error("取消订单失败:", error);
        alert("取消订单失败，请稍后重试");
      }
    };

    // 支付订单
    const payOrder = (orderId) => {
      router.push({ path: "/payment", query: { orderId } });
    };

    // 确认收货
    const confirmOrder = async (id) => {
      if (!confirm("确定要确认收货吗？")) return;
      
      try {
        const response = await request.put("/api/orders/status?orderState=3&orderId=" + id);
        
        if (response.success) {
          alert("订单完成");
          // 重新加载当前标签的订单
          fetchOrders(); // 重新加载订单
          router.push({path: '/orderList'});
        } else {
          alert("确认完成失败: " + response.message);
          router.push({path: '/orderList'});
        }
      } catch (error) {
        alert("确认完成订单失败，请稍后重试");
      }
    };

    // 查看订单详情
    const goDetail = (id) => {
      router.push({
        path: '/ListDetail',
        query: { orderId: id }
      });
    };

    onMounted(() => {
      // 获取用户信息
      const userData = sessionStorage.getItem("userInfo") || localStorage.getItem("userInfo");
      userInfo.value = userData ? JSON.parse(userData) : null;

      if (!userInfo.value) {
        alert("用户未登录，请先登录！");
        router.push({ path: "/login" });
        return;
      }

      // 初始加载全部订单
      fetchOrders();
    });



    return {
      orderArr,
      userInfo,
      tabs,
      activeTab,
      displayedOrders,
      loading,
      changeTab,
      getStatusText,
      getStatusClass,
      formatTime,
      cancelOrder,
      payOrder,
      confirmOrder,
      goDetail,
	  orderCounts,
	  displayedOrders
    };
  }
};
</script>
  
<style scoped>
/****************** 容器与顶部 ******************/
.wrapper {
	width: 100%;
	height: 100%;
	background: #f5f7fa;
	min-height: 100vh;
}

.topbar {
	width: 100%;
	height: 12vw;
	background-color: #409eff;
	color: #fff;
	font-size: 4.8vw;
	font-weight: 600;
	position: fixed;
	left: 0;
	top: 0;
	z-index: 1000;
	display: flex;
	justify-content: center;
	align-items: center;
}

.page-title {
	margin-top: 12vw;
	padding: 4vw;
	font-size: 4.5vw;
	color: #333;
	font-weight: bold;
	background: white;
}

/****************** 标签栏 ******************/
.tabs {
	display: flex;
	align-items: center;
	padding: 0 4vw;
	background: white;
	border-bottom: 1px solid #f0f0f0;
	overflow-x: auto;
	white-space: nowrap;
}
.tabs li {
	margin-right: 6vw;
	padding: 3vw 0;
	font-size: 3.8vw;
	color: #666;
	position: relative;
	cursor: pointer;
}
.tabs li.active {
	color: #409eff;
	font-weight: 600;
}
.tabs li.active::after {
	content: "";
	position: absolute;
	left: 0;
	bottom: 0;
	width: 100%;
	height: 0.8vw;
	background: #409eff;
	border-radius: 0.4vw;
}

/****************** 加载和空状态 ******************/
.loading, .empty-state {
	display: flex;
	justify-content: center;
	align-items: center;
	padding: 10vw;
	font-size: 4vw;
	color: #999;
}

.empty-state {
	flex-direction: column;
}
.empty-state img {
	width: 30vw;
	height: 30vw;
	margin-bottom: 4vw;
	opacity: 0.5;
}

/****************** 订单列表 ******************/
.order-list {
	padding: 4vw;
}

.order-item {
	background: #fff;
	border-radius: 2vw;
	box-shadow: 0 1vw 2vw rgba(0,0,0,.05);
	padding: 4vw;
	margin-bottom: 4vw;
}

.order-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding-bottom: 3vw;
	border-bottom: 1px solid #f5f5f5;
	margin-bottom: 3vw;
}

.order-id {
	font-size: 3.6vw;
	color: #999;
}

.status-badge {
  padding: 1vw 2vw;
  border-radius: 1vw;
  font-size: 3.2vw;
  font-weight: 500;
  position: relative;
  z-index: 10; /* 确保在最上层 */
}

.status-badge.unpaid {
	background: #fff0f0;
	color: #ff4d4f;
}
.status-badge.pending {
	background: #e6f7ff;
	color: #1890ff;
}
.status-badge.accepted {
	background: #f6ffed;
	color: #52c41a;
}
.status-badge.done {
	background: #fdf4de;
	color: #ffa700;
}
.status-badge.canceled {
	background: #f9f9f9;
	color: #999;
}

.order-content {
	display: flex;
	align-items: center;
	margin-bottom: 4vw;
}

.thumb {
	width: 20vw;
	height: 20vw;
	object-fit: cover;
	border-radius: 1.2vw;
	margin-right: 3vw;
}

.meta {
	flex: 1;
}

.name {
	font-size: 4.2vw;
	color: #333;
	font-weight: 500;
	margin-bottom: 1vw;
}

.items, .time {
	font-size: 3.4vw;
	color: #999;
	margin-bottom: 1vw;
}

.price {
	font-size: 4.5vw;
	color: #ff6b00;
	font-weight: bold;
	margin-top: 2vw;
}

.actions {
	display: flex;
	justify-content: flex-end;
	gap: 2vw;
}

.actions button {
	padding: 2vw 4vw;
	border-radius: 1.6vw;
	font-size: 3.6vw;
	cursor: pointer;
	border: none;
}

.cancel-btn {
	background: #fff;
	color: #666;
	border: 1px solid #ddd !important;
}

.pay-btn {
	background: #409eff;
	color: #fff;
}

.confirm-btn {
	background: #52c41a;
	color: #fff;
}

.detail-btn {
	background: #f5f5f5;
	color: #666;
}
</style>