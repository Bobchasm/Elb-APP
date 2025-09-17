<template>
  <div class="container">
    <!-- 顶部背景 -->
    <div class="top-background">
      <h1>个人信息</h1>
    </div>
    
    <!-- 用户信息卡片 -->
    <div class="user-card">
      <div class="avatar">
        <img :src="user2?.userImg || require('@/assets/default-avatar.png')" alt="用户头像">
      </div>
      <div class="user-details">
        <div class="user-name">
          {{ user?.userName || '未设置昵称' }}
          <i class="fas fa-pencil-alt edit-icon" @click="editNickname"></i>
        </div>
        <div class="user-phone">
          <i class="fas fa-phone phone-icon"></i>
          {{ formattedPhone }}
        </div>
      </div>
    </div>
    
    <!-- 菜单部分 -->
    <div class="menu-section">
      <div class="section-title">常用功能</div>
      <div class="menu-list">
        <div class="menu-item" @click="navigateTo('orders')">
          <div class="menu-icon">
            <i class="fas fa-file-invoice"></i>
          </div>
          <span class="menu-text">我的订单</span>
          <i class="fas fa-chevron-right menu-arrow"></i>
        </div>
        <div class="menu-item" @click="navigateTo('address')">
          <div class="menu-icon">
            <i class="fas fa-map-marker-alt"></i>
          </div>
          <span class="menu-text">收货地址</span>
          <i class="fas fa-chevron-right menu-arrow"></i>
        </div>
        <div class="menu-item" @click="myfavorite">
          <div class="menu-icon">
            <i class="fas fa-heart"></i>
          </div>
          <span class="menu-text">我的收藏</span>
          <i class="fas fa-chevron-right menu-arrow"></i>
        </div>
        <div class="menu-item" @click="navigateTo('notifications')">
          <div class="menu-icon">
            <i class="fas fa-bell"></i>
          </div>
          <span class="menu-text">消息与通知</span>
          <i class="fas fa-chevron-right menu-arrow"></i>
        </div>
      </div>
    </div>
    
    <!-- 按钮区域 -->
    <div class="button-section">
      <button class="switch-btn" @click="switchToMerchant">
        <i class="fas fa-store"></i>切换为商家
      </button>
      <button class="logout-btn" @click="logout">
        <i class="fas fa-sign-out-alt"></i>退出登录
      </button>
    </div>
    
    <!-- 加载状态 -->
    <div class="loading" v-if="loading">
      <i class="fas fa-spinner fa-spin"></i> 加载中...
    </div>
    
    <!-- 错误提示 -->
    <div class="error-message" v-if="errorMessage">
      <i class="fas fa-exclamation-circle"></i> {{ errorMessage }}
    </div>
    
    <!-- 底部导航 -->
    <Footer />
    
    <!-- 编辑昵称模态框 -->
    <div v-if="showEditNickname" class="modal-overlay">
      <div class="modal-content">
        <h3>编辑昵称</h3>
        <input v-model="newNickname" placeholder="输入新昵称" />
        <div class="modal-buttons">
          <button @click="submitNickname">提交</button>
          <button @click="showEditNickname = false">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import Footer from '../components/Footer.vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { toast } from '../utils/toast';

export default {
  name: 'MyApplication',
  setup() {
    const router = useRouter();
    const user = ref({});
    const user2 = ref({});
    const showEditNickname = ref(false);
    const newNickname = ref('');
    const loading = ref(false);
    const errorMessage = ref('');
    
    // 格式化手机号显示
    const formattedPhone = computed(() => {
      if (!user.value.userId) return '未绑定手机';
      return user.value.userId.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
    });

    onMounted(async () => {
      await loadUserData();
    });

    // 加载用户数据
    const loadUserData = async () => {
      loading.value = true;
      errorMessage.value = '';
      
      try {
        // 从sessionStorage获取用户基本信息
        user.value = sessionStorage.getItem('user') ? JSON.parse(sessionStorage.getItem('user')) : null;
        
        if (!user.value) {
          toast.warning('用户未登录，请先登录！');
          router.push({ path: '/login' });
          return;
        }

        // 从API获取完整用户信息
        const response = await axios.post('UserController/getUserByIdByPass', {
          userId: user.value.userId,
          password: user.value.password
        });
        
        if (response.data) {
          user2.value = response.data;
          if (response.data.userImg) {
            user.value.userImg = response.data.userImg;
            sessionStorage.setItem('user', JSON.stringify(user.value));
          }
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
        errorMessage.value = '获取用户信息失败，请重试！';
        toast.error('获取用户信息失败，请重试！');
      } finally {
        loading.value = false;
      }
    };

    const logout = () => {
      sessionStorage.removeItem('user');
      router.push({ path: '/index' });
    };

    const editNickname = () => {
      newNickname.value = user.value.userName || '';
      showEditNickname.value = true;
    };

    const submitNickname = async () => {
      if (newNickname.value.trim() === '') {
        toast.warning('昵称不能为空！');
        return;
      }
      if (newNickname.value.length > 8) {
        toast.warning('昵称不能超过8个字符！');
        return;
      }
      
      try {
        const response = await axios.post('UserController/changeUserName', {
          userId: user.value.userId,
          userName: newNickname.value,
        });
        
        if (response.data === 1) {
          user.value.userName = newNickname.value;
          sessionStorage.setItem('user', JSON.stringify(user.value));
          toast.success('昵称修改成功！');
          showEditNickname.value = false;
          newNickname.value = '';
        } else {
          toast.error('昵称修改失败！');
        }
      } catch (error) {
        console.error(error);
        toast.error('昵称修改失败！');
      }
    };

    const myfavorite = () => {
      router.push({ path: '/favorites' });
    };

    const navigateTo = (page) => {
      const pageNames = {
        'orders': '我的订单',
        'address': '收货地址',
        'notifications': '消息与通知'
      };
      toast.info(`即将跳转到: ${pageNames[page]}页面`);
      // 实际项目中这里应该添加路由跳转逻辑
    };

    const switchToMerchant = () => {
      toast.info('切换商家模式功能待开发');
    };

    return {
      user,
      user2,
      formattedPhone,
      loading,
      errorMessage,
      showEditNickname,
      newNickname,
      logout,
      editNickname,
      submitNickname,
      myfavorite,
      navigateTo,
      switchToMerchant
    };
  },
  components: {
    Footer,
  },
};
</script>

<style scoped>
/* 这里放置你新HTML中的所有CSS样式 */
/* 注意：由于使用了scoped属性，可能需要深度选择器来修改子组件样式 */

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
  margin-bottom: 60px;
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
  margin: -80px auto 20px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  padding: 20px 0;
  display: flex;
  align-items: center;
  gap: 20px;
  position: relative;
  z-index: 2;
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

.user-name {
  font-size: 1.1rem;
  font-weight: 500;
  color: #333;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  padding: 10px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
}

.edit-icon {
  margin-left: 10px;
  color: #3498db;
  font-size: 16px;
  cursor: pointer;
}

.user-phone {
  font-size: 0.95rem;
  color: #495057;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  padding: 10px;
  display: flex;
  align-items: center;
}

.phone-icon {
  margin-right: 8px;
  color: #3498db;
}

.menu-section {
  width: 92%;
  max-width: 500px;
  margin: 20px auto;
}

.section-title {
  font-size: 1.1rem;
  color: #2c3e50;
  margin-bottom: 15px;
  padding-left: 10px;
  font-weight: 600;
  border-left: 4px solid #3498db;
}

.menu-list {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s ease;
}

.menu-item:hover {
  background-color: #f1f8ff;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-icon {
  width: 22px;
  height: 22px;
  margin-right: 15px;
  color: #3498db;
  display: flex;
  justify-content: center;
  align-items: center;
}

.menu-text {
  flex: 1;
  font-size: 0.95rem;
  color: #34495e;
  font-weight: 500;
}

.menu-arrow {
  color: #bdc3c7;
  font-size: 14px;
}

.button-section {
  width: 92%;
  max-width: 500px;
  margin: 20px auto;
  display: flex;
  flex-direction: column;
  gap: 15px;
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

.loading {
  text-align: center;
  padding: 15px;
  color: #3498db;
  font-size: 1rem;
}

.error-message {
  text-align: center;
  padding: 10px;
  background: #ffecec;
  color: #e74c3c;
  border-radius: 8px;
  margin: 10px;
  font-size: 0.9rem;
}

.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 12px;
  width: 80%;
  max-width: 400px;
}

.modal-content h3 {
  margin-top: 0;
  color: #2c3e50;
}

.modal-content input {
  width: 100%;
  padding: 10px;
  margin: 10px 0;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 16px;
}

.modal-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 15px;
}

.modal-buttons button {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
}

.modal-buttons button:first-child {
  background: #3498db;
  color: white;
}

.modal-buttons button:last-child {
  background: #e0e0e0;
  color: #333;
}

@media (max-width: 480px) {
  .container, .user-card, .menu-section, .button-section {
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
    margin-top: -70px;
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
  
  .menu-item {
    padding: 14px 16px;
  }
}
</style>