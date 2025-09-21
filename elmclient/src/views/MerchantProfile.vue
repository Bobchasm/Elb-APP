<template>
  <div class="container">
    <div class="top-background">
      <h1>商家信息</h1>
    </div>

    <div class="user-card">
      <div class="avatar">
        <img :src="merchant?.avatar || defaultAvatar" alt="商家头像">
      </div>
      <div class="user-details">
        <div class="user-name">
          <i class="fas fa-user-circle user-icon"></i>
          <span>{{ merchant?.name || '未设置商家名称' }}</span>
        </div>
        <div class="user-phone">
          <i class="fas fa-phone phone-icon"></i>
          <span>{{ formattedPhone }}</span>
        </div>
      </div>
    </div>

    <div class="store-data-bar">
      <div class="data-item">
        <div class="data-value">{{ merchantData.likes }}</div>
        <div class="data-label">点赞</div>
      </div>
      <div class="data-item">
        <div class="data-value">{{ merchantData.favorites }}</div>
        <div class="data-label">收藏</div>
      </div>
      <div class="data-item">
        <div class="data-value">{{ merchantData.rating }}</div>
        <div class="data-label">评分</div>
      </div>
    </div>

    <div class="button-section">
      <button class="switch-btn" @click="switchToCustomer">
        <i class="fas fa-user"></i> 切换为顾客
      </button>
      <button class="logout-btn" @click="logout">
        <i class="fas fa-sign-out-alt"></i> 退出登录
      </button>
    </div>

    <div class="bottom-nav">
      <router-link to="/merchant/business" class="nav-item">
        <i class="fas fa-store"></i>
        <span>商铺</span>
      </router-link>
      <router-link to="/merchant/orders" class="nav-item">
        <i class="fas fa-list-alt"></i>
        <span>订单</span>
      </router-link>
      <router-link to="/merchant/profile" class="nav-item active">
        <i class="fas fa-user-circle"></i>
        <span>我的</span>
      </router-link>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from '../utils/toast'; // 假设有toast工具函数
//import defaultAvatar from '@/assets/default-merchant-avatar.png'; // 默认头像

export default {
  name: 'MerchantProfile',
  setup() {
    const router = useRouter();

    // 商家数据（替换死数据）
    const merchant = ref({
      id: '12345',
      name: '美味小厨老板',
      phone: '13800135678',
      avatar: 'https://ts2.tc.mm.bing.net/th/id/OIP-C.d9wuz272AIKxwaQCaNOE4gHaHA?rs=1&pid=ImgDetMain&o=7&rm=3'
    });

    // 商家统计数据
    const merchantData = ref({
      likes: 999,
      favorites: 1234,
      rating: 4.8
    });

    const loading = ref(false);

    // 格式化手机号显示
    const formattedPhone = computed(() => {
      if (!merchant.value.phone) return '未绑定手机';
      return merchant.value.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
    });

    onMounted(async () => {
      await loadMerchantData();
    });

    // 加载商家数据
    const loadMerchantData = async () => {
      loading.value = true;
      try {
        // 这里应该是API调用，获取商家数据
        // const response = await axios.get('/api/merchant/profile');
        // merchant.value = response.data;

        // 模拟从sessionStorage获取数据
        const storedMerchant = sessionStorage.getItem('merchant');
        if (storedMerchant) {
          merchant.value = JSON.parse(storedMerchant);
        }
      } catch (error) {
        console.error('获取商家信息失败:', error);
        toast.error('获取商家信息失败，请重试！');
      } finally {
        loading.value = false;
      }
    };

    const logout = () => {
      sessionStorage.removeItem('merchant');
      sessionStorage.removeItem('user');
      router.push({ path: '/login' });
    };

    const switchToCustomer = () => {
      router.push({ path: '/myinformation' });
    };

    return {
      merchant,
      merchantData,
      formattedPhone,
      loading,
      // defaultAvatar,
      logout,
      switchToCustomer
    };
  }
};
</script>

<style scoped>
/* 保持所有原有样式不变 */
.container {
  max-width: 600px;
  margin: 0 auto;
  background: #fff;
  min-height: 100vh;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  border-radius: 16px;
  padding-bottom: 8vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  position: relative;
}
.top-background {
  width: 100%;
  height: 100px;
  background: linear-gradient(to right, #3a7bd5, #00d2ff);
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 16px 16px 0 0;
  position: relative;
  overflow: hidden;
  margin-bottom: 50px;
}
.top-background::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.2) 0%, rgba(255,255,255,0) 70%);
  transform: rotate(30deg);
  animation: shine 6s infinite linear;
}
@keyframes shine {
  0% { transform: rotate(30deg) translate(-10%, -10%); }
  100% { transform: rotate(30deg) translate(10%, 10%); }
}
.top-background h1 {
  color: white;
  font-size: 1.8rem;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
  letter-spacing: 1px;
  margin: 0;
  z-index: 1;
}
.user-card {
  width: 92%;
  max-width: 500px;
  margin: 0 auto 20px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  padding: 20px 0;
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  z-index: 2;
  transform: translateY(-50px);
}
.avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid white;
  box-shadow: 0 6px 20px rgba(0, 151, 255, 0.3);
  flex-shrink: 0;
  background: #f8f9fa;
  margin-left: 15px;
}
.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}
.user-details {
  flex: 1;
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #e9ecef;
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-right: 15px;
}
.user-name, .user-phone {
  font-size: 0.95rem;
  color: #495057;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  padding: 10px;
  display: flex;
  align-items: center;
}
.user-name {
  font-size: 1.1rem;
  font-weight: 500;
  color: #333;
  margin-bottom: 8px;
}
.user-name .user-icon, 
.user-phone .phone-icon {
  margin-right: 8px;
  color: #3498db;
}
/* 删除了 .edit-icon 样式 */
/* .edit-icon {
  margin-left: auto;
  color: #3498db;
  font-size: 16px;
  cursor: pointer;
} */
/* 店铺数据栏样式 */
.store-data-bar {
  display: flex;
  justify-content: space-around;
  width: 92%;
  max-width: 500px;
  padding: 24px 0;
  border-radius: 16px;
  background: #fff;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  margin: 20px auto;
  transform: translateY(-50px);
}

.data-item {
  text-align: center;
  flex: 1;
}

.data-value {
  font-size: 20px;
  font-weight: bold;
  color: #333;
  margin-bottom: 4px;
}

.data-label {
  font-size: 14px;
  color: #777;
}

.button-section {
  width: 92%;
  max-width: 500px;
  margin: 20px auto;
  display: flex;
  flex-direction: column;
  gap: 15px;
  transform: translateY(-50px);
}
.switch-btn {
  width: 100%;
  padding: 14px;
  text-align: center;
  background: linear-gradient(135deg, #ff9a9e, #fad0c4);
  color: white;
  font-weight: 600;
  border-radius: 12px;
  border: none;
  font-size: 0.95rem;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  margin-bottom: 10px;
}
.switch-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}
.logout-btn {
  width: 100%;
  padding: 14px;
  text-align: center;
  background: linear-gradient(135deg, #8e2de2, #4a00e0);
  color: white;
  font-weight: 600;
  border-radius: 12px;
  border: none;
  font-size: 0.95rem;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
  cursor: pointer;
  transition: all 0.3s ease;
}
.logout-btn:hover {
  background: #ffeaea;
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}
.logout-btn i {
  margin-right: 8px;
}

/* 底部导航栏样式 */
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 600px;
  background-color: #fff;
  box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-around;
  align-items: center;
  height: 8vh;
  border-radius: 16px 16px 0 0;
  z-index: 1000;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: #999;
  font-size: 12px;
  text-decoration: none;
  flex: 1;
  transition: color 0.3s;
}

.nav-item.active {
  color: #4CAF50;
}

.nav-item i {
  font-size: 20px;
  margin-bottom: 4px;
}

/* 删除了模态框相关的 CSS */
/* .modal-overlay, .modal-content, .modal-item, .modal-buttons, .modal-content input, .modal-content textarea, .modal-buttons button { ... } */

@media (max-width: 480px) {
  .container, .user-card, .button-section {
    max-width: 100vw;
    width: 100vw;
    border-radius: 0;
    padding: 0;
  }

  .top-background {
    height: 90px;
    margin-bottom: 50px;
    border-radius: 0;
  }
  .user-card {
    flex-direction: column;
    align-items: center;
    gap: 10px;
    padding: 20px 0;
    margin-top: 0;
    transform: translateY(-50px);
    width: 90%;
  }

  .avatar {
    width: 80px;
    height: 80px;
    margin-left: 0;
  }

  .user-details {
    width: 85%;
    padding: 10px;
    gap: 6px;
    margin-right: 0;
  }

  .bottom-nav {
    border-radius: 0;
  }
}
</style>