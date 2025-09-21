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
			<li v-for="(t, idx) in tabs" :key="t" :class="{ active: activeTab === idx }" @click="activeTab = idx">{{ t }}</li>
		</ul>

		<!-- 订单列表 -->
		<ul class="order-list">
			<!-- 用户端：点击订单项跳转到订单详情（共用 ListDetail） -->
			<li v-for="item in displayedOrders" :key="item.orderId" class="order-item" @click="goDetail(item)" title="查看详情">
				<img class="thumb" :src="getThumb(item)" alt="thumb">
				<div class="meta">
					<p class="name">{{ getName(item) }}</p>
					<p class="price">¥ {{ Number(item.orderTotal).toFixed(2) }}</p>
					<span class="status-badge" :class="statusClass(item.orderId, item)">{{ statusText(item.orderId, item) }}</span>
				</div>
				<div class="actions">
					<!-- 未支付：取消 + 付款 -->
					<template v-if="isUnpaid(item)">
						<button class="cancel-btn" @click="cancelOrder(item.orderId)">取消</button>
						<button class="pay-btn" @click="payOrder(item.orderId)">付款</button>
					</template>
					<!-- 已接单：完成按钮 -->
					<template v-else-if="isAccepted(item)">
						<button class="confirm-btn" @click="toggleConfirm(item.orderId)">完成</button>
					</template>
					<!-- 待接单/已取消/已完成：置灰状态按钮 -->
					<template v-else>
						<button v-if="isCanceled(item.orderId)" class="cancel-btn disabled" disabled>已取消</button>
						<button v-else-if="isConfirmed(item.orderId)" class="confirm-btn disabled" disabled>已完成</button>
						<button v-else class="confirm-btn disabled" disabled>等待中</button>
					</template>
				</div>
			</li>
		</ul>

		<!-- 底部导航 -->
		<nav class="bottom-nav">
			<div class="nav-item active" @click="navigateTo('/orderList')">
				<span class="icon">🍪</span>
				<span>订单</span>
			</div>
			<div class="nav-item" @click="navigateTo('/myInformation')">
				<span class="icon">👤</span>
				<span>我的</span>
			</div>
		</nav>
	</div>
</template>
  
<script>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import axios from "axios";

export default {
  name: "OrderList",
  setup() {
    const orderArr = ref([]);
    const user = ref({});
    const router = useRouter();
    const tabs = ["全部", "未支付", "待接单", "已接单", "已取消", "已完成"];
    const activeTab = ref(0);
    // 本地确认状态，键为orderId
    const confirmedMap = ref({}); // 已完成（完成按钮）
    const canceledMap = ref({});  // 已取消（接单前）
    const paidMap = ref({});      // 本地支付完成（进入待接单）
    // 已移除未支付倒计时

    const handleError = (error) => {
      console.error("Failed to fetch data:", error);
      alert("请求失败，请稍后重试！");
    };

    const detailetShow = async (order) => {
      console.log("获取订单详情，orderId:", order.orderId);
      
      try {
        // 如果已经加载过详情，直接切换显示状态
        if (order.detailet) {
          order.isShowDetailet = !order.isShowDetailet;
          return;
        }
        
        // 获取订单明细
        const detailResponse = await axios.post("/OrdersController/listOrderDetailetByOrderId", {
          orderId: order.orderId
        });
        console.log("订单明细:", detailResponse.data);
        
        // 获取订单项ID列表
        const odIdResponse = await axios.post("OrdersController/listOdIdByOrderId", {
          orderId: order.orderId
        });
        console.log("订单项ID列表:", odIdResponse.data);
        
        // 将详情数据存储到当前订单对象中
        order.detailet = detailResponse.data;
        order.index = odIdResponse.data;
        order.isShowDetailet = true;
        
      } catch (error) {
        console.error("获取订单详情失败:", error);
        handleError(error);
      }
    };

    const navigateToPayment = (orderId) => {
      console.log(orderId);
      router.push({ path: "/payment", query: { orderId } });
    };

    const navigateTo = (path) => {
      router.push(path);
    };

    const goDetail = (order) => {
      try { sessionStorage.setItem('selectedOrder', JSON.stringify(order)); } catch (e) {}
      router.push({
        path: '/listDetail',
        query: { orderId: order.orderId }
      });
    };

    // 展示名称：优先展示第一个明细商品名，否则商家名
    const getName = (order) => {
      const first = Array.isArray(order.detailet) && order.detailet.length > 0 ? order.detailet[0] : null;
      return first ? first.foodName : (order.business && order.business.businessName ? order.business.businessName : "商品");
    };

    // 配图：使用公共图片或商家默认图
    const getThumb = () => "/baozi.jpg";

    const isConfirmed = (orderId) => !!confirmedMap.value[orderId];
    const isCanceled = (orderId) => !!canceledMap.value[orderId];
    const isPaid = (order) => !!paidMap.value[order.orderId] || order.orderState === 1;
    const isAccepted = (order) => order.orderState === 1 && !isCanceled(order.orderId) && !isConfirmed(order.orderId);
    const isUnpaid = (order) => !isPaid(order) && !isCanceled(order.orderId) && !isConfirmed(order.orderId);
    const isPendingAccept = (order) => isPaid(order) && !isAccepted(order) && !isConfirmed(order.orderId) && !isCanceled(order.orderId);

    const toggleConfirm = (orderId) => {
      confirmedMap.value[orderId] = true;
    };
    const cancelOrder = (orderId) => {
      canceledMap.value[orderId] = true;
    };
    const payOrder = (orderId) => {
      paidMap.value[orderId] = true;
      router.push({ path: "/payment", query: { orderId } });
    };

    // 已移除倒计时格式化函数

    const statusText = (orderId, order) => {
      if (isConfirmed(orderId)) return "已完成";
      if (isCanceled(orderId)) return "已取消";
      if (isAccepted(order)) return "已接单";
      if (isPendingAccept(order)) return "待接单";
      if (isUnpaid(order)) return "待支付";
      return "";
    };
    const statusClass = (orderId, order) => {
      if (isConfirmed(orderId)) return "done";
      if (isCanceled(orderId)) return "canceled";
      if (isAccepted(order)) return "accepted";
      if (isPendingAccept(order)) return "pending";
      return "unpaid";
    };

    // 基于标签筛选
    const displayedOrders = computed(() => {
      const all = orderArr.value; // 展示全部，便于测试多条虚拟订单
      switch (activeTab.value) {
        case 1: // 未支付
          return all.filter(o => isUnpaid(o));
        case 2: // 待接单
          return all.filter(o => isPendingAccept(o));
        case 3: // 已接单
          return all.filter(o => isAccepted(o));
        case 4: // 已取消
          return all.filter(o => isCanceled(o.orderId) || o.orderState === -1);
        case 5: // 已完成
          return all.filter(o => isConfirmed(o.orderId));
        default:
          return all;
      }
    });

    onMounted(async () => {
      // 获取用户信息
      const userData = sessionStorage.getItem("user");
      user.value = userData ? JSON.parse(userData) : null;

      // 获取订单列表
      if (user.value) {
        try {
          const ordersResponse = await axios.post("OrdersController/listOrdersByUserId", {
            userId: user.value.userId
          });
          
          let result = ordersResponse.data;
          console.log("订单列表:", result);
          
          result.forEach((order) => {
            order.isShowDetailet = false;
            order.detailet = null; // 初始化详情数据
            order.index = null;    // 初始化索引数据
          });
          
          orderArr.value = result;

          // ===== 虚拟订单 开始 (仅用于前端联调与UI测试) =====
          const now = new Date().toLocaleString();
          const mockBusiness = (name, addr, delivery) => ({ businessName: name, businessAddress: addr, deliveryPrice: delivery });
          const mockDetail = (name, price, quantity = 1) => ([{ foodName: name, foodPrice: price, quantity }]);
          const mockUserName = user.value?.userName || '测试用户';
          const mockUserPhone = user.value?.userTel || '13800001111';
          const mockUserAddr = '天津市 南开区 测试街道 100 号';

          const mockOrders = [
            {
              orderId: 99901,
              orderTotal: 100.0,
              orderState: 0, // 未支付
              orderDate: now,
              business: mockBusiness("汉堡王测试店", "南开区海光寺街道1号", 5),
              detailet: mockDetail("汉堡", 100, 1),
              userName: mockUserName,
              userPhone: mockUserPhone,
              userAddress: mockUserAddr,
              isShowDetailet: false,
              index: null
            },
            {
              orderId: 99902,
              orderTotal: 48.0,
              orderState: 0, // 未支付
              orderDate: now,
              business: mockBusiness("川菜测试店", "河西区围堤道88号", 4),
              detailet: mockDetail("回锅肉", 48, 1),
              userName: mockUserName,
              userPhone: mockUserPhone,
              userAddress: mockUserAddr,
              isShowDetailet: false,
              index: null
            },
            {
              orderId: 99903,
              orderTotal: 58.0,
              orderState: 1, // 已接单(以1代表商家已接单)
              orderDate: now,
              business: mockBusiness("披萨测试店", "和平区南京路188号", 6),
              detailet: mockDetail("披萨", 58, 1),
              userName: mockUserName,
              userPhone: mockUserPhone,
              userAddress: mockUserAddr,
              isShowDetailet: false,
              index: null
            },
            {
              orderId: 99904,
              orderTotal: 32.0,
              orderState: 0, // 待接单（通过本地 paidMap 标记为已支付）
              orderDate: now,
              business: mockBusiness("粉面测试店", "津南区海教园测试路9号", 3),
              detailet: mockDetail("牛肉粉", 32, 1),
              userName: mockUserName,
              userPhone: mockUserPhone,
              userAddress: mockUserAddr,
              isShowDetailet: false,
              index: null
            },
            {
              orderId: 99905,
              orderTotal: 22.0,
              orderState: -1, // 已取消（接单前）
              orderDate: now,
              business: mockBusiness("小吃测试店", "红桥区丁字沽三号路66号", 2),
              detailet: mockDetail("烤肠", 11, 2),
              userName: mockUserName,
              userPhone: mockUserPhone,
              userAddress: mockUserAddr,
              isShowDetailet: false,
              index: null
            },
            {
              orderId: 99906,
              orderTotal: 66.0,
              orderState: 1, // 已接单
              orderDate: now,
              business: mockBusiness("寿司测试店", "西青区学府工业区22号", 5),
              detailet: mockDetail("寿司拼盘", 66, 1),
              userName: mockUserName,
              userPhone: mockUserPhone,
              userAddress: mockUserAddr,
              isShowDetailet: false,
              index: null
            },
            {
              orderId: 99907,
              orderTotal: 18.0,
              orderState: 1, // 已接单 -> 我们将其标记为已完成
              orderDate: now,
              business: mockBusiness("甜品测试店", "东丽区空港经济区3号", 4),
              detailet: mockDetail("蛋挞", 9, 2),
              userName: mockUserName,
              userPhone: mockUserPhone,
              userAddress: mockUserAddr,
              isShowDetailet: false,
              index: null
            }
           
          ];

          // 追加到现有订单数组末尾
          orderArr.value = [...orderArr.value, ...mockOrders];
          // ===== 虚拟订单 结束 =====

          // 初始化本地状态：将 99904 设为已支付(待接单)，将 99907 设为已完成
          paidMap.value[99904] = true; // 待接单
          confirmedMap.value[99907] = true; // 已完成
          
        } catch (error) {
          console.error("获取订单列表失败:", error);
          handleError(error);
        }
      } else {
        alert("用户未登录，请先登录！");
        router.push({ path: "/login" });
      }
      // 已移除倒计时心跳与清理
    });

    return {
      orderArr,
      user,
      tabs,
      activeTab,
      displayedOrders,
      getName,
      getThumb,
      isConfirmed,
      isCanceled,
      isUnpaid,
      isPendingAccept,
      isAccepted,
      toggleConfirm,
      payOrder,
      cancelOrder,
      statusText,
      statusClass,
      detailetShow,
      navigateTo,
      navigateToPayment,
      goDetail
    };
  }
};
</script>
  
<style scoped>
/****************** 容器与顶部 ******************/
.wrapper {
	width: 100%;
	height: 100%;
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

.page-title {
	margin-top: 12vw;
	padding: 4vw;
	font-size: 4vw;
	color: #333;
}

/****************** 标签栏 ******************/
.tabs {
	display: flex;
	align-items: center;
	padding: 0 4vw;
	border-bottom: 1px solid #f0f0f0;
}
.tabs li {
	margin-right: 6vw;
	padding: 2vw 0;
	font-size: 3.8vw;
	color: #666;
	position: relative;
	cursor: pointer;
}
.tabs li.active {
	color: #333;
	font-weight: 600;
}
.tabs li.active::after {
	content: "";
	position: absolute;
	left: 0;
	bottom: 0;
	width: 100%;
	height: .8vw;
	background: #409eff;
	border-radius: .4vw;
}

/****************** 列表 ******************/
.order-list {
	padding: 4vw;
}
.order-item {
	display: flex;
	align-items: center;
	margin-bottom: 4vw;
	background: #fff;
	border-radius: 1.6vw;
	box-shadow: 0 2px 10px rgba(0,0,0,.05);
	padding: 3vw;
}
.thumb {
	width: 28vw;
	height: 28vw;
	object-fit: cover;
	border-radius: 1.2vw;
	margin-right: 3vw;
}
.meta {
	flex: 1;
}
.name {
	font-size: 4vw;
	color: #333;
}
.sub {
	font-size: 3.2vw;
	color: #999;
	margin-top: 1vw;
}
.price {
	font-size: 4.2vw;
	color: #3a78ff;
	margin-top: 2vw;
}
.status-badge {
	display: inline-block;
	margin-top: 1.2vw;
	padding: .6vw 1.6vw;
	border-radius: 1.2vw;
	font-size: 3vw;
	line-height: 1;
	background: #e6eefb;
	color: #8aa8e5;
}
.status-badge.done { background: #eef0f3; color: #97a0af; }
.status-badge.pending { background: #e6f4ff; color: #1e80ff; }
.status-badge.canceled { background: #fdeeee; color: #e15656; }
.actions {
	display: flex;
	flex-direction: column;
	align-items: center;
}
.status {
	background: #e6eefb;
	color: #8aa8e5;
	padding: 1.6vw 3vw;
	border-radius: 1vw;
	font-size: 4.2vw;
	margin-bottom: 2vw;
}
.status.done {
	background: #eef0f3;
	color: #97a0af;
}
.confirm-btn {
	background: #1e80ff;
	color: #fff;
	border: none;
	border-radius: 1.6vw;
	padding: 2.4vw 5vw;
	font-size: 4.6vw;
	cursor: pointer;
}
.confirm-btn.disabled {
	background: #e6e9ef;
	color: #fff;
	cursor: not-allowed;
}
.pay-btn {
	background: #1e80ff;
	color: #fff;
	border: none;
	border-radius: 1.6vw;
	padding: 2.4vw 5vw;
	font-size: 4.6vw;
	margin-top: 1.6vw;
	cursor: pointer;
}
.cancel-btn {
	background: #ffffff;
	color: #e15656;
	border: 1px solid #f3caca;
	border-radius: 1.6vw;
	padding: 2vw 5vw;
	font-size: 4.2vw;
	margin-top: 1.6vw;
	cursor: pointer;
}
.cancel-btn.disabled {
	background: #f5f5f5;
	color: #bbb;
	border-color: #eee;
	cursor: not-allowed;
}

/****************** 底部导航 ******************/
.bottom-nav {
	position: fixed;
	left: 0;
	bottom: 0;
	width: 100%;
	height: 14vw;
	background: #409eff;
	display: flex;
	justify-content: space-around;
	align-items: center;
	color: #fff;
}
.nav-item {
	display: flex;
	flex-direction: column;
	align-items: center;
	font-size: 3.6vw;
	opacity: .7;
}
.nav-item.active {
	opacity: 1;
}
.icon {
	font-size: 5vw;
	margin-bottom: .6vw;
}
</style>