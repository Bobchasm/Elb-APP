<template>
  <div class="sub-page-container">
    <div class="sub-page-header warning-header">
      <h1 class="sub-page-title">积分余额预警配置</h1>
      <p class="sub-page-description">设置低余额提醒阈值，确保及时发现并补充积分。</p>
    </div>
    
    <div class="content-panel">
      <p>预警配置功能即将在此处上线...</p>
      <button class="warning-btn">保存配置</button>
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

.warning-header .sub-page-title {
    color: #ff6b6b; /* 预警颜色 */
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

.warning-btn {
  padding: 10px 25px;
  background-color: #ff6b6b;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  margin-top: 20px;
  transition: background-color 0.2s;
}

.warning-btn:hover {
  background-color: #ee5a52;
}
</style>