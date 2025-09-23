<template>
  <div class="container">
    <div class="card">
      <div class="header-section">
        <div class="icon-section">
          <img :src='myimage' class="goutu">
        </div>
        <div class="logo">
          <h2 class="title">支付成功</h2>
        </div>
      </div>
      <div class="details">
        <p>商家名称：{{ paymentDetails.business?.name || '未知商家' }}</p>
        <p>支付金额：¥{{ paymentDetails.orderTotal }}</p>
        <p>支付时间：{{ paymentDetails.orderDate }}</p>
      </div>
      <div class="actions">
        <button @click="goBack" class="btn-back">返回首页</button>
      </div>
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

    const orderId = ref(route.query.orderId);

    onMounted(async () => {
      if (!orderId.value) {
        console.error('缺少订单ID参数，无法查询支付详情。');
        return;
      }
      
      try {
        const response = await request.get(`/api/orders/${orderId.value}`);
        if (response.success && response.data) {
          paymentDetails.value = {
            business: response.data.business,
            orderTotal: response.data.orderTotal,
            orderDate: response.data.orderDate
          };
        } else {
          console.error('API 请求失败或返回数据格式不正确', response.message);
        }
      } catch (error) {
        console.error('Error fetching orders:', error);
      }
    });

    const goBack = () => {
      router.push('/index');
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
/* 全局样式和容器 */
html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: 'Helvetica Neue', Arial, sans-serif;
  background-color: #f5f7fa;
}

.container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 2rem;
  box-sizing: border-box;
}

/* 核心卡片样式 */
.card {
  background-color: #ffffff;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.08);
  padding: 2rem;
  width: 90%;
  max-width: 400px;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

/* 头部部分，包含图标和标题 */
.header-section {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin-bottom: 0.5rem;
}

/* 图标部分 */
.icon-section {
  display: flex;
  justify-content: center;
  align-items: center;
}

.goutu {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #4CAF50;
  padding: 10px;
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.2);
}

/* 标题和详情 */
.logo {
  display: flex;
  align-items: center;
}

.title {
  /* 优化：使用vw作为基础单位，并限制最大尺寸 */
  font-size: 5vw;
  font-weight: 700;
  color: #333333;
  margin: 0;
}

.details p {
  /* 优化：使用相对单位rem */
  font-size: 1rem;
  color: #666666;
  line-height: 1.5;
  margin: 0;
}

/* 按钮 */
.actions {
  margin-top: 1rem;
}

.btn-back {
  width: 100%;
  padding: 1rem;
  background-color: #2196F3;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease, transform 0.2s ease;
}

.btn-back:hover {
  background-color: #1976D2;
  transform: translateY(-2px);
}

/* 媒体查询：在大屏幕设备上调整字体大小 */
@media (min-width: 600px) {
  .title {
    font-size: 2rem; /* 在大屏幕上固定字体大小，避免过大 */
  }

  .details p {
    font-size: 1.2rem;
  }

  .btn-back {
    font-size: 1.1rem;
  }
}
</style>