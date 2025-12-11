<template>
  <div class="marketing-center-page">
    <div class="top-background">
      <div class="top-content">
        <h1>营销中心</h1>
      </div>
    </div>
    <div class="page-content">

      <div class="promotion-banner">
        <div class="banner-content">
          <div class="banner-icon">📢</div>
          <div class="banner-text">
            <p class="banner-label">当前生效促销规则</p>
            <h2 class="promotion-text">{{ activeRule.ruleName || '正在加载中...' }}</h2>
          </div>
          <div class="banner-actions">
            <button
              class="banner-action-btn"
              @click="showDetailsModal = true"
              :disabled="!activeRule.id"
            >
              查看详情
            </button>
          </div>
        </div>
      </div>

      <section class="functions-section">
        <h2 class="section-title">核心管理功能</h2>
        <div class="functions-grid">
          <div class="function-card" @mousedown="handleManageUsageRules">
            <div class="card-icon-wrapper">
              <span class="card-icon">📋</span>
            </div>
            <div class="card-content">
              <h3 class="card-title">规则管理</h3>
              <p class="card-description full-line">管理所有积分使用规则，包括新增、修改和启用/禁用。</p>
            </div>
            <div class="card-arrow">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 5l7 7-7 7" />
              </svg>
            </div>
          </div>

          <div class="function-card" @mousedown="handleManageWarningConfig">
            <div class="card-icon-wrapper warning-icon">
              <span class="card-icon">🚨</span>
            </div>
            <div class="card-content">
              <h3 class="card-title">预警配置</h3>
              <p class="card-description full-line">设置系统积分余额预警阈值，确保及时补充积分。</p>
            </div>
            <div class="card-arrow">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 5l7 7-7 7" />
              </svg>
            </div>
          </div>

          <div class="function-card primary" @mousedown="handleGoToExchangeManagement">
            <div class="card-icon-wrapper primary-icon">
              <span class="card-icon">🔄</span>
            </div>
            <div class="card-content">
              <h3 class="card-title">兑换管理</h3>
              <p class="card-description full-line">管理可兑换的商品目录、设置兑换比例与规则。</p>
            </div>
            <div class="card-arrow">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <path d="M9 5l7 7-7 7" />
              </svg>
            </div>
          </div>
        </div>
      </section>

    </div>
    <div class="page-footer">
      <p>营销中心  2024</p>
    </div>

    <div v-if="showDetailsModal && activeRule.id" class="modal-overlay" @click.self="showDetailsModal = false">
      <div class="modal-content">
        <h3>当前生效积分规则详情</h3>
        <p class="rule-title">{{ activeRule.ruleName }}</p>
        <div class="rule-details">
          <p><strong>规则类型：</strong>{{ activeRule.ruleTypeName || '无' }}</p>
          <p><strong>积分状态：</strong>{{ activeRule.ruleStatus === 1 ? '启用' : '禁用' }}</p>
          <p><strong>积分比例：</strong>{{ activeRule.pointsRatio || '无' }}</p>
          <p><strong>积分倍数：</strong>{{ activeRule.pointsMultiplier || '无' }}</p>
          <p v-if="activeRule.minOrderAmount"><strong>最低订单金额：</strong>¥{{ formatNumber(activeRule.minOrderAmount) }}</p>
          <p v-if="activeRule.maxOrderAmount"><strong>最高订单金额：</strong>¥{{ formatNumber(activeRule.maxOrderAmount) }}</p>
          <p v-if="activeRule.expireDays"><strong>有效期：</strong>{{ activeRule.expireDays }} 天</p>
          <p><strong>生效时间：</strong>{{ activeRule.startTime?.split('T')[0] || '无' }}</p>
          <p><strong>结束时间：</strong>{{ activeRule.endTime?.split('T')[0] || '无' }}</p>
        </div>
        <button class="close-btn" @click="showDetailsModal = false">关闭</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from '../utils/toast';
import request from '../utils/request';

const activeRule = ref({});
const showDetailsModal = ref(false);
const currentUser = ref({});

const router = useRouter();

const formatNumber = (num) => {
  if (num === null || num === undefined) return '0';
  return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ',');
};

const getCurrentUserInfo = async () => {
  try {
    const storage = localStorage.getItem('token') ? localStorage : sessionStorage;
    const savedUser = storage.getItem('userInfo');

    if (savedUser) {
      currentUser.value = JSON.parse(savedUser);
    } else {
      const res = await request.get('/api/user');
      if (res.success && res.data) {
        currentUser.value = res.data;
        storage.setItem('userInfo', JSON.stringify(res.data));
      } else {
        console.warn('无法获取用户信息，Token可能已失效');
      }
    }
  } catch (error) {
    console.error('获取管理员信息失败:', error);
  }
};

const fetchPointsRules = async () => {
  try {
    const params = {
      pageNum: 1,
      pageSize: 100,
    };

    const response = await request.get('/api/marketing/points/rules', { params });

    if (response.success && Array.isArray(response.data)) {
      const allRules = response.data;

      const promotionRules = allRules.filter(
        rule => rule.ruleType === 1 && rule.ruleStatus === 1
      );

      if (promotionRules.length > 0) {
        activeRule.value = promotionRules.sort((a, b) => b.priority - a.priority)[0];
      } else {
        activeRule.value = {
          ruleName: '暂无生效促销规则',
          id: null
        };
      }
    } else {
      toast.error(`查询积分规则失败: ${response.message || '未知错误'}`);
      activeRule.value = { ruleName: '加载失败', id: null };
    }
  } catch (error) {
    console.error("获取积分规则列表失败:", error);
    toast.error('网络或服务器错误，无法获取积分规则。');
    activeRule.value = { ruleName: '网络错误', id: null };
  }
};

const handleManageUsageRules = () => {
  router.push({ name: 'AdminPointsRuleManagement' }).catch(() => {});
};

const handleManageWarningConfig = () => {
  router.push({ name: 'AdminWarningConfig' }).catch(() => {});
};

const handleGoToExchangeManagement = () => {
  router.push({ name: 'AdminExchangeManagement' }).catch(() => {});
};

onMounted(() => {
  getCurrentUserInfo();
  fetchPointsRules();
});
</script>

<style scoped>
/* 顶部导航栏 - 简化版 */
/* .top-background {
  width: 100%;
  height: 100px;
  background: linear-gradient(to right, #3a7bd5, #00d2ff);
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 16px 16px 0 0;
  position: fixed;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1000;
  overflow: hidden;
  margin-bottom: 50px;
  max-width: 600px;
}

.top-content {
  max-width: 800px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.top-background h1 {
  color: white;
  font-size: 1.3rem;
  font-weight: 600;
  margin: 0;
  padding: 0;
} */

.top-subtitle,
.top-icon {
  display: none; /* 隐藏副标题和图标 */
}

/* 页面内容区域 */
.page-content {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px 16px 16px;
  position: relative;
  width: 100%;
  box-sizing: border-box;
}

/* 移除原有的顶部退出按钮样式 */
.top-logout-btn {
  display: none;
}
/* ======================================= */
/* ====== 核心样式保留 ====== */
/* ======================================= */
.marketing-center-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #f8fbff 0%, #f0f7ff 100%);
  padding: 0 20px 40px;
  width: 100%;
  max-width: 640px;
  margin: 0 auto;
}

/* 顶部固定标题区域 - 已注释掉
.top-background {
  width: 100%;
  height: 100px;
  background: linear-gradient(to right, #3a7bd5, #00d2ff);
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 4px 20px rgba(58, 123, 213, 0.3);
  border-radius: 0 0 16px 16px;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;
  overflow: hidden;
  margin-bottom: 50px;
  max-width: 100%;
}

.top-content {
  display: flex;
  align-items: center;
  gap: 20px;
  max-width: 1200px;
  width: 100%;
  padding: 0 30px;
}
*/

.top-icon {
  font-size: 42px;
  background: rgba(255, 255, 255, 0.2);
  width: 70px;
  height: 70px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(10px);
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.top-text {
  color: white;
  flex: 1; 
  min-width: 0; 
}

.top-title {
  font-size: 32px;
  font-weight: 700;
  margin: 0 0 8px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  overflow: hidden; 
  white-space: nowrap;
  text-overflow: ellipsis;
}

.top-subtitle {
  font-size: 16px;
  margin: 0;
  opacity: 0.95;
  font-weight: 400;
}

/* 促销规则横幅 */
.promotion-banner {
  max-width: 1200px;
  margin: 0 auto 40px;
  background: linear-gradient(135deg, #3a7bd5 0%, #2a6bcf 100%);
  border-radius: 16px;
  padding: 24px 32px;
  color: white;
  box-shadow: 0 8px 25px rgba(58, 123, 213, 0.25);
}

.banner-content {
  display: flex;
  align-items: center;
  gap: 24px;
}

.banner-icon {
  font-size: 40px;
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.15);
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.banner-text {
  flex: 1;
}

.banner-label {
  font-size: 14px;
  opacity: 0.9;
  margin: 0 0 8px;
  text-transform: uppercase;
  letter-spacing: 1px;
  font-weight: 500;
}

.promotion-text {
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  line-height: 1.4;
}

.banner-action-btn {
  padding: 12px 28px;
  background: rgba(255, 255, 255, 0.2);
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 10px;
  color: white;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  backdrop-filter: blur(10px);
  font-size: 14px;
}

.banner-action-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.banner-action-btn:hover:not(:disabled) {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(0, 0, 0, 0.2);
}

/* 核心功能区域 */
.functions-section {
  max-width: 1200px;
  margin: 0 auto 50px;
}

.section-title {
  font-size: 28px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 30px;
  text-align: center;
  position: relative;
  padding-bottom: 15px;
}

.section-title::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 3px;
  background: linear-gradient(to right, #3a7bd5, #00d2ff);
  border-radius: 2px;
}

.functions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 24px;
}

.function-card {
  background: white;
  border-radius: 16px;
  padding: 28px;
  cursor: pointer;
  transition: all 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  position: relative;
  overflow: hidden;
  border: 1px solid #e1e8f0;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: flex-start;
  gap: 20px;
}

.function-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 20px 40px rgba(58, 123, 213, 0.15);
  border-color: #3a7bd5;
}

.function-card:active {
  transform: translateY(-4px);
  box-shadow: 0 12px 24px rgba(58, 123, 213, 0.2);
}

.card-icon-wrapper {
  width: 70px;
  height: 70px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  background: linear-gradient(135deg, #3a7bd5 0%, #00d2ff 100%);
}

.card-icon-wrapper.warning-icon {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a52 100%);
}

.card-icon-wrapper.primary-icon {
  background: linear-gradient(135deg, #36d1dc 0%, #5b86e5 100%);
}

.card-icon {
  font-size: 32px;
  color: white;
}

.card-content {
  flex: 1;
  position: relative;
  z-index: 2;
}

.card-title {
  font-size: 22px;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 10px;
}

.card-description {
  /* 调整描述的底部外边距，因为 stats 区域被移除了 */
  font-size: 14px;
  color: #64748b;
  margin: 0; /* 移除底部外边距，使其紧贴下方内容 */
  line-height: 1.5;
}

/* **移除 stats 相关的样式**
.card-stats, .stats-value, .stats-value.warning-value, .stats-label 已移除
*/

.card-arrow {
  position: absolute;
  top: 28px;
  right: 28px;
  color: #cbd5e1;
  transition: all 0.3s ease;
}

.function-card:hover .card-arrow {
  color: #3a7bd5;
  transform: translateX(5px);
}


/* 页面底部 */
.page-footer {
  max-width: 1200px;
  margin: 60px auto 0;
  padding-top: 30px;
  border-top: 1px solid #e1e8f0;
  text-align: center;
  color: #94a3b8;
  font-size: 14px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .marketing-center-page {
    padding: 0 16px 30px;
    padding-top: 110px;
  }

  .top-background {
    height: 90px;
    border-radius: 0 0 12px 12px;
  }

  .top-content {
    padding: 0 20px;
    gap: 15px;
  }

  .top-icon {
    width: 60px;
    height: 60px;
    font-size: 36px;
  }

  .top-title {
    font-size: 26px;
  }

  .top-subtitle {
    font-size: 14px;
  }

  .functions-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }

  .function-card {
    padding: 24px;
  }

  .promotion-banner {
    padding: 20px;
    margin-bottom: 30px;
  }

  .banner-content {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .banner-action-btn {
    width: 100%;
  }

  .card-icon-wrapper {
    width: 60px;
    height: 60px;
  }

  .card-title {
    font-size: 20px;
  }
}

@media (max-width: 480px) {
  .top-background {
    height: 85px;
    margin-bottom: 40px;
    border-radius: 0;
  }

  .top-content {
    padding: 0 16px;
    gap: 12px;
  }

  .top-icon {
    width: 50px;
    height: 50px;
    font-size: 28px;
  }

  .top-title {
    font-size: 22px;
    margin-bottom: 4px;
  }

  .top-subtitle {
    font-size: 13px;
  }

  .marketing-center-page {
    padding-top: 95px;
  }

  .function-card {
    padding: 20px;
  }

  .section-title {
    font-size: 24px;
    margin-bottom: 25px;
  }

  .promotion-text {
    font-size: 18px;
  }

  .card-arrow {
    top: 20px;
    right: 20px;
  }
}

/* 添加动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.functions-section {
  animation: fadeInUp 0.6s ease-out;
}

.promotion-banner {
  animation: fadeInUp 0.5s ease-out 0.1s both;
}

/* ======================================= */
/* =========== 弹窗样式保留 ============== */
/* ======================================= */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.modal-content {
  background: white;
  padding: 30px;
  border-radius: 12px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  transform: scale(0.9);
  animation: modal-enter 0.3s forwards;
}

@keyframes modal-enter {
  to {
    transform: scale(1);
  }
}

.modal-content h3 {
  margin-top: 0;
  font-size: 22px;
  font-weight: 700;
  color: #3a7bd5;
  border-bottom: 2px solid #e0eafc;
  padding-bottom: 10px;
  margin-bottom: 20px;
}

.rule-title {
    font-size: 18px;
    font-weight: 600;
    margin-bottom: 15px;
    color: #2c3e50;
}

.rule-details p {
    margin: 8px 0;
    font-size: 14px;
    color: #64748b;
    line-height: 1.5;
}

.rule-details strong {
    color: #2c3e50;
    display: inline-block;
    min-width: 90px;
    font-weight: 600;
}

.close-btn {
  margin-top: 25px;
  width: 100%;
  padding: 12px;
  background-color: #3a7bd5;
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background-color: #2a6bcf;
}
</style>
