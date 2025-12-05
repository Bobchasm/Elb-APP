<template>
  <div class="sub-page-container">
    <div class="sub-page-header exchange-header">
      <h1 class="sub-page-title">积分兑换管理中心</h1>
      <p class="sub-page-description">在此配置可兑换的商品，设置兑换比例和活动。</p>
    </div>
    
    <div class="content-panel">
      <p>兑换管理功能即将在此处上线...</p>
      <button class="exchange-btn">新增兑换商品</button>
    </div>
  </div>
</template>

<script setup>
// import { ref } from 'vue';
// import { useRouter } from 'vue-router'; 
</script>

<style scoped>
/* 继承或自定义基础样式 */
.sub-page-container {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
  min-height: calc(100vh - 80px);
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.sub-page-header {
  padding: 30px 0;
  border-bottom: 1px solid #e1e8f0;
  margin-bottom: 30px;
}

.exchange-header .sub-page-title {
    color: #36d1dc; /* 兑换颜色 */
}

.sub-page-title {
  font-size: 30px;
  font-weight: 700;
  margin: 0 0 8px;
}

.sub-page-description {
  font-size: 16px;
  color: #64748b;
  margin: 0;
}

.content-panel {
  padding: 20px 0;
  min-height: 400px;
  text-align: center;
}

.exchange-btn {
  padding: 10px 25px;
  background-color: #36d1dc;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  margin-top: 20px;
  transition: background-color 0.2s;
}

.exchange-btn:hover {
  background-color: #5b86e5;
}
</style>