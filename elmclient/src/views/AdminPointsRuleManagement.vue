<template>
  <div class="sub-page-container">
    <div class="sub-page-header">
      <h1 class="sub-page-title">积分使用规则管理</h1>
      <p class="sub-page-description">在此创建、编辑和管理所有积分兑换与使用规则。</p>
    </div>
    
    <div class="content-panel">
      <p>规则管理功能即将在此处上线...</p>
      <button class="primary-btn">新增规则</button>
    </div>
  </div>
</template>

<script setup>
// import { ref } from 'vue';
// import { useRouter } from 'vue-router'; 
// const router = useRouter(); 
</script>

<style scoped>
/* 继承或自定义基础样式 */
.sub-page-container {
  max-width: 1200px;
  margin: 40px auto;
  padding: 0 20px;
  min-height: calc(100vh - 80px); /* 留出页边距 */
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
}

.sub-page-header {
  padding: 30px 0;
  border-bottom: 1px solid #e1e8f0;
  margin-bottom: 30px;
}

.sub-page-title {
  font-size: 30px;
  font-weight: 700;
  color: #3a7bd5;
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

.primary-btn {
  padding: 10px 25px;
  background-color: #3a7bd5;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  margin-top: 20px;
  transition: background-color 0.2s;
}

.primary-btn:hover {
  background-color: #2a6bcf;
}
</style>