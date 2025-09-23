<template>
  <div class="container">
    <div class="top-blue"></div>
    <div class="gou">
      <img :src='myimage' class="goutu">
    </div>
    <div class="logo">
      <h2>支付成功</h2>
    </div>
    <div class="details">
      <p>商家名称：{{ paymentDetails.businessName || '未知商家' }}</p>
      <p>金额：¥{{ paymentDetails.orderTotal }}</p>
      <p>支付时间：{{ paymentDetails.orderDate }}</p>
    </div>
    <div class="back-home">
      <button @click="goBack">去订单列表</button>
    </div>
  </div>
</template>

<script>
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import request from '@/utils/request';
import myimage from '/src/assets/R-C.png';

export default {
  setup() {
    const route = useRoute();
    const router = useRouter();
    const paymentDetails = ref({});
    const businessId = ref();
    const businessName = ref();

    // 从路由参数中获取 orderId
    const orderId = ref(route.query.orderId);

    onMounted(async () => {
      // 1. 检查路由参数中是否存在 orderId
      if (!orderId.value) {
        console.error('缺少订单ID参数,无法查询支付详情。');
        return;
      }

      businessId.value = route.query.value;
      
      try {
        // 2. 发起 GET 请求，将 orderId 作为路径参数
        const response = await request.get("/api/orders/detail?orderId=" + orderId.value);

        // 3. 检查请求是否成功且返回了数据
        if (response.success && response.data) {
          // 4. 如果找到了订单，直接将返回的数据对象赋值给 paymentDetails
          paymentDetails.value = {
            businessName: response.data.businessName,
            orderTotal: response.data.orderTotal,
            orderDate: response.data.orderDate
          };
          
          console.log('支付详情已成功加载:', paymentDetails.value);
        } else {
          console.error('API 请求失败或返回数据格式不正确', response.message);
          // 可以根据需要添加其他错误处理逻辑，如跳转到错误页面
        }

      } catch (error) {
        console.error('Error fetching orders:', error);
        // 网络请求失败或服务器返回非 2xx 状态码
      }
    });

    const goBack = () => {
      router.push('/orderList');
    };

    return {
      paymentDetails,
      myimage,
      goBack,
    };
  }
};
</script>

<style scoped>
/* CSS 代码保持不变 */
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: Arial, sans-serif;
  background-color: #f0f0f5;
}

.container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  background-color: #f0f0f5;
  padding-top: 0;
}

.top-blue {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 12vw;
  background-color: #2196F3; /* Changed to a blue color */
}

.gou {
  margin-bottom: 5vh;
  margin-top: 15vw;
}

.goutu {
  width: 12vw;
  height: 12vw;
  border-radius: 50%;
  background-color: #2196F3; /* Changed to a blue color */
  padding: 3vw;
  box-shadow: 0 0 1vw rgba(0, 0, 0, 0.1);
}

.logo {
  text-align: center;
  margin-bottom: 5vh;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.logo h2 {
  font-size: 3.5vw;
  color: black;
}

.details {
  text-align: center;
  margin-bottom: 5vh;
}

.details p {
  font-size: 4vw;
  color: #555;
  margin-bottom: 1vh;
}

.back-home {
  margin-top: 5vh;
}

.back-home button {
  padding: 2vw 4vw;
  background-color: #2196F3; /* Changed to a blue color */
  color: white;
  border: none;
  border-radius: 1vw;
  cursor: pointer;
  font-size: 4vw;
  transition: background-color 0.3s ease;
}

.back-home button:hover {
  background-color: #1976D2; /* A slightly darker blue for hover effect */
}
</style>