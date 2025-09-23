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
    <div class="button-section">
      <button class="switch-btn" @click="switchToCustomer">
        <i class="fas fa-user"></i> 切换为顾客
      </button>
      <button class="logout-btn" @click="logout">
        <i class="fas fa-sign-out-alt"></i> 退出登录
      </button>
    </div>
    <!-- 修改为显示商铺列表 -->
    <div class="stores-container">
      <div class="store-card" v-for="store in stores" :key="store.merchantId">
        <div class="store-name">{{ store.merchantName }}</div>
        <div class="store-data-bar">
          <div class="data-item">
            <div class="data-value">{{ store.likeCount }}</div>
            <div class="data-label">点赞</div>
          </div>
          <div class="data-item">
            <div class="data-value">{{ store.collectCount }}</div>
            <div class="data-label">收藏</div>
          </div>
          <div class="data-item">
            <div class="data-value">{{ store.rating }}</div>
            <div class="data-label">评分</div>
          </div>
        </div>
      </div>
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
import { toast } from '../utils/toast'; 
import request from '../utils/request';

export default {
  name: 'MerchantProfile',
  setup() {
    const router = useRouter();
    const defaultAvatar = 'https://via.placeholder.com/100'; // 备用默认头像

    const merchant = ref(null);
    const stores = ref([]); // 存储商铺列表
    
    const loading = ref(false);

    // 格式化手机号显示
    const formattedPhone = computed(() => {
      if (!merchant.value?.phone) return '未绑定手机';
      return merchant.value.phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
    });

    onMounted(async () => {
      await loadMerchantData();
      // 在获取到 merchant.id 后，再加载统计数据
      if (merchant.value?.id) {
        await loadMerchantStats(merchant.value.id);
      }
    });

    // 加载商家基本信息
    const loadMerchantData = async () => {
      loading.value = true;
      try {
        const data = await request.get('/api/person');
        
        if (data && data.id) {
          merchant.value = {
            id: data.id,
            name: data.username,
            phone: data.phone,
            avatar: data.photo,
          };
        } else {
          toast.error('获取商家信息失败：服务器返回数据为空或格式不正确！');
        }
      } catch (error) {
        console.error('获取商家信息失败:', error);
        toast.error('获取商家信息失败，请重试！');
      } finally {
        loading.value = false;
      }
    };
    
    // 修改：加载商家统计数据，现在获取的是商铺列表
    const loadMerchantStats = async (userId) => {
      try {
        const response = await request.get(`http://localhost:8080/api/merchant/interaction/statsByUserId/${userId}`);
        
        if (response && response.success && response.data) {
          stores.value = response.data;
        } else {
          toast.error('获取商家统计数据失败！');
        }
      } catch (error) {
        console.error('获取商家统计数据失败:', error);
        toast.error('获取商家统计数据失败，请重试！');
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
      stores,
      formattedPhone,
      loading,
      defaultAvatar,
      logout,
      switchToCustomer
    };
  }
};
</script>

<style scoped>
/* 原有样式保持不变 */
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

/* 新增商铺列表样式 */
.stores-container {
  width: 92%;
  max-width: 500px;
  margin: 20px auto;
  display: flex;
  flex-direction: column;
  gap: 15px;
  transform: translateY(-50px);
}

.store-card {
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  padding: 15px;
}

.store-name {
  font-size: 1.1rem;
  font-weight: 500;
  color: #333;
  margin-bottom: 10px;
  text-align: center;
}

/* 店铺数据栏样式 */
.store-data-bar {
  display: flex;
  justify-content: space-around;
  width: 100%;
  padding: 15px 0;
  border-radius: 12px;
  background: #f8f9fa;
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
  background: linear-gradient(135deg,#8e2de2, #4a00e0);
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