<template>
  <div class="container">
    <div class="card">
      <div class="header-section">
        <div class="icon-section">
          <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="check-icon">
            <path d="M22 11.08V12a10 10 0 1 1-5.93-8.82"></path>
            <polyline points="22 4 12 14.01 9 11.01"></polyline>
          </svg>
        </div>
        <h2 class="title">支付成功</h2>
      </div>

      <div class="details">
        <div class="detail-item">
          <span class="label">商家名称</span>
          <span class="value">{{ paymentDetails.business?.businessName || '未知商家' }}</span>
        </div>

        <div class="detail-item total-section">
          <span class="label">订单总额</span>
          <span class="value">¥{{ paymentDetails.orderTotal }}</span>
        </div>

        <div class="detail-item deduction-section">
          <span class="label deduction-label">积分抵扣</span>
          <span class="value deduction-value">- ¥{{ pointsDeductionAmount }}</span>
        </div>

        <div class="detail-item actual-paid-section">
          <span class="label actual-paid-label">实付金额</span>
          <span class="value actual-paid-amount">¥{{ actualPaidAmount }}</span>
        </div>

        <div class="detail-item">
          <span class="label">支付时间</span>
          <span class="value">{{ paymentDetails.orderDate }}</span>
        </div>
      </div>

      <div class="actions">
        <button @click="goBack" class="btn-back">去查看订单</button>
      </div>
    </div>
  </div>
</template>

<script>
import { onMounted, ref, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import request from '@/utils/request'; // 假设这是您的网络请求工具

export default {
  setup() {
    const route = useRoute();
    const router = useRouter();
    // paymentDetails 现在将直接存储从订单接口获取的数据
    const paymentDetails = ref({
      business: null,
      orderTotal: '0.00',
      pointsDeduction: '0.00', // 存储积分抵扣金额
      orderDate: ''
    });
    const orderId = ref(route.query.orderId);

    // 计算实付金额
    const actualPaidAmount = computed(() => {
      const total = parseFloat(paymentDetails.value.orderTotal) || 0;
      // 积分抵扣金额
      const deduction = parseFloat(paymentDetails.value.pointsDeduction) || 0; 
      // 确保实付金额不为负
      return Math.max(0, total - deduction).toFixed(2);
    });

    // 格式化积分抵扣金额
    const pointsDeductionAmount = computed(() =>
      (parseFloat(paymentDetails.value.pointsDeduction) || 0).toFixed(2)
    );

    onMounted(async () => {
      // **【调试】** 打印获取到的订单 ID
      console.log('--- 开始获取订单详情 ---');
      console.log('订单ID:', orderId.value);
      
      if (!orderId.value) {
        console.error('缺少订单ID，无法查询详情');
        return;
      }

      try {
        // 1. 调用订单详情接口
        const apiUrl = `/api/orders/${orderId.value}`;
        console.log('请求订单详情 API:', apiUrl);
        const orderResponse = await request.get(apiUrl);
        
        // **【调试】** 打印 API 原始返回
        console.log('订单 API 原始返回:', orderResponse);
        
        if (!orderResponse.success || !orderResponse.data) {
          console.error('订单 API 返回异常或 data 为空', orderResponse.message);
          return;
        }

        const orderData = orderResponse.data;
        
        // 确保 orderTotal 是一个数字
        const totalAmount = parseFloat(orderData.orderTotal) || 0;
        
        // **核心逻辑：直接从订单数据中读取 pointsDiscountAmount**
        // 根据您的接口文档，这是积分抵扣的现金金额（元）
        const deductionAmount = parseFloat(orderData.pointsDiscountAmount) || 0;

        // **【调试】** 打印提取的关键数据
        console.log('提取到的订单总额 (orderTotal):', totalAmount.toFixed(2));
        console.log('提取到的积分抵扣金额 (pointsDiscountAmount):', deductionAmount.toFixed(2));
        console.log('--- 订单详情获取结束 ---');


        // 2. 写入页面数据
        paymentDetails.value = {
          business: orderData.business,
          orderTotal: totalAmount.toFixed(2),
          pointsDeduction: deductionAmount.toFixed(2), // 使用接口返回的抵扣金额
          orderDate: orderData.orderDate
        };
        
      } catch (err) {
        console.error('Error fetching order details:', err);
      }
    });

    const goBack = () => router.push('/orderList');

    return {
      paymentDetails,
      goBack,
      actualPaidAmount,
      pointsDeductionAmount
    };
  }
};
</script>

<style scoped>
:root {
  --primary-color: #2e7d32;
  --secondary-color: #f5f7fa;
  --text-color-primary: #1a202c;
  --text-color-secondary: #4a5568;
  --card-bg-color: #ffffff;
  --button-color: #2563eb;
  --button-hover-color: #1e40af;
}

html, body {
  margin: 0;
  padding: 0;
  height: 100%;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "Roboto", "PingFang SC", "Microsoft YaHei", sans-serif;
  background-color: var(--secondary-color);
}

.container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  padding: 1.5rem;
  box-sizing: border-box;
}

.card {
  background-color: var(--card-bg-color);
  border-radius: 20px;
  box-shadow: 0 12px 24px rgba(0, 0, 0, 0.05), 0 4px 8px rgba(0, 0, 0, 0.02);
  padding: 2.5rem 2rem;
  width: 100%;
  max-width: 420px;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 2rem;
}

.header-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.25rem;
}

.icon-section {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 90px;
  height: 90px;
  border-radius: 50%;
  background-color: #e6f6e8;
  animation: scale-in 0.5s ease both;
}

.check-icon {
  width: 55px;
  height: 55px;
  color: #2e7d32;
  animation: fade-in 0.8s ease 0.2s both;
}

.title {
  font-size: 2rem;
  font-weight: 700;
  color: var(--text-color-primary);
  margin: 0;
  white-space: nowrap;
  animation: slide-up 0.6s ease 0.3s both;
}

.details {
  display: flex;
  flex-direction: column;
  gap: 1rem;
  text-align: left;
  border-top: 1px solid #e2e8f0;
  padding-top: 1.5rem;
}

.detail-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 1rem;
}

.label {
  color: var(--text-color-secondary);
  font-weight: 500;
}

.value {
  color: var(--text-color-primary);
  font-weight: 600;
}

.deduction-value {
  color: #f56565;
}

.actual-paid-section {
  border-top: 2px solid #e2e8f0;
  padding-top: 1rem;
  margin-top: 0.5rem;
}

.actual-paid-label {
  font-size: 1.1rem;
  font-weight: 700;
}

.actual-paid-amount {
  font-size: 2.8rem;
  color: var(--primary-color);
  font-weight: 900;
  line-height: 1;
}

.actions {
  margin-top: 0.5rem;
}

.btn-back {
  width: 100%;
  padding: 1rem;
  background-color: #0493f2da;
  color: #e2e8f0;
  border: none;
  border-radius: 12px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background-color 0.3s ease, box-shadow 0.3s ease;
  box-shadow: 0 4px 12px rgba(37, 99, 235, 0.2);
}

.btn-back:hover {
  background-color: var(--button-hover-color);
  box-shadow: 0 6px 16px rgba(37, 99, 235, 0.3);
}

@keyframes scale-in {
  from { transform: scale(0.8); opacity: 0; }
  to   { transform: scale(1); opacity: 1; }
}

@keyframes fade-in {
  from { opacity: 0; }
  to   { opacity: 1; }
}

@keyframes slide-up {
  from { transform: translateY(20px); opacity: 0; }
  to   { transform: translateY(0); opacity: 1; }
}

@media (min-width: 600px) {
  .card { padding: 3rem; }
  .title { font-size: 2.5rem; }
}
</style>
