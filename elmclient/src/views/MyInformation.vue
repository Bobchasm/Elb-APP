<template>
  <div class="container">
    <div class="top-background">
      <h1>个人信息</h1>
    </div>

    <div class="user-card">
      <div class="avatar">
        <img :src="user?.userImg || require('@/assets/default-avatar.png')" alt="用户头像">
      </div>
      <div class="user-details">
        <div class="user-name">
          {{ user?.userName || '未设置昵称' }}
          <i class="fas fa-pencil-alt edit-icon" @click="openEditModal"></i>
        </div>
        <div class="user-full-name">
          <i class="fas fa-id-card-alt full-name-icon"></i>
          <span class="first-name">{{ user?.firstName || '未设置姓氏' }}</span>
          <span class="last-name">{{ user?.lastName || '未设置名字' }}</span>
        </div>
        <div class="user-phone">
          <i class="fas fa-phone phone-icon"></i>
          {{ formattedPhone }}
        </div>
        <div class="user-email">
          <i class="fas fa-envelope-open-text email-icon"></i>
          <span>{{ user?.email || '未设置邮箱' }}</span>
        </div>
      </div>
    </div>

    <div class="menu-section">
      <div class="section-title">常用功能</div>
      <div class="menu-list">
        <div class="menu-item" @click="showAddressSection = !showAddressSection">
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
        <div class="menu-item message-item" @click="navigateTo('notifications')">
          <div class="menu-icon">
            <i class="fas fa-bell"></i>
          </div>
          <span class="menu-text">消息与通知</span>
          <div class="notification-dot" v-if="hasNewMessages"></div>
          <i class="fas fa-chevron-right menu-arrow"></i>
        </div>
      </div>
    </div>

    <AddressManager v-if="showAddressSection" :userId="user?.userId" />
    
    <div class="button-section">
      <button class="switch-btn" @click="switchToMerchant">
        <i class="fas fa-store"></i>切换为商家
      </button>
      <button class="logout-btn" @click="logout">
        <i class="fas fa-sign-out-alt"></i>退出登录
      </button>
    </div>

    <div class="loading" v-if="loading">
      <i class="fas fa-spinner fa-spin"></i> 加载中...
    </div>

    <div class="error-message" v-if="errorMessage">
      <i class="fas fa-exclamation-circle"></i> {{ errorMessage }}
    </div>

    <Footer />

    <div v-if="showEditModal" class="modal-overlay">
      <div class="modal-content">
        <h3>编辑个人信息</h3>
        <div class="modal-item">
          <label>姓氏</label>
          <input v-model="editFormData.firstName" placeholder="输入姓氏" />
        </div>
        <div class="modal-item">
          <label>名字</label>
          <input v-model="editFormData.lastName" placeholder="输入名字" />
        </div>
        <div class="modal-item">
          <label>手机号</label>
          <input v-model="editFormData.userId" placeholder="输入手机号" />
        </div>
        <div class="modal-item">
          <label>邮箱</label>
          <input v-model="editFormData.email" placeholder="输入邮箱" type="email" />
        </div>
        <div class="modal-buttons">
          <button @click="submitEdits">提交</button>
          <button @click="closeEditModal">取消</button>
        </div>
      </div>
    </div>

    <div v-if="showMerchantApplyModal" class="modal-overlay">
      <div class="modal-content merchant-apply-modal">
        <div class="modal-icon">
          <i class="fas fa-store"></i>
        </div>
        <h3>申请成为商家</h3>
        <p class="modal-message">当前无商家权限，是否申请成为商家？</p>
        <div class="modal-buttons">
          <button class="apply-btn" @click="applyForMerchant">申请</button>
          <button class="cancel-btn" @click="closeMerchantApplyModal">否</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue';
import Footer from '../components/Footer.vue';
import AddressManager from '../components/AddressManager.vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { toast } from '../utils/toast';

export default {
  name: 'MyApplication',
  components: {
    Footer,
    AddressManager
  },
  setup() {
    const router = useRouter();
    const user = ref({});
    const loading = ref(false);
    const errorMessage = ref('');
    const showEditModal = ref(false);
    const showMerchantApplyModal = ref(false);
    const editFormData = ref({
      firstName: '',
      lastName: '',
      userId: '',
      email: ''
    });
    const showAddressSection = ref(false);
    const hasNewMessages = ref(true);

    const formattedPhone = computed(() => {
      if (!user.value.userId) return '未绑定手机';
      return user.value.userId.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2');
    });

    onMounted(async () => {
      await loadUserData();
      // 在这里调用获取未读消息数量的API
      await checkNewMessages();
    });

    const loadUserData = async () => {
      loading.value = true;
      errorMessage.value = '';
      
      try {
        const storedUser = sessionStorage.getItem('user') ? JSON.parse(sessionStorage.getItem('user')) : null;
        
        if (!storedUser) {
          toast.warning('用户未登录，请先登录！');
          router.push({ path: '/login' });
          return;
        }

        const response = await axios.post('UserController/getUserByIdByPass', {
          userId: storedUser.userId,
          password: storedUser.password
        });
        
        if (response.data) {
          user.value = { ...storedUser, ...response.data };
          sessionStorage.setItem('user', JSON.stringify(user.value));
        } else {
          user.value = storedUser;
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
        errorMessage.value = '获取用户信息失败，请重试！';
        toast.error('获取用户信息失败，请重试！');
      } finally {
        loading.value = false;
      }
    };

    const checkNewMessages = async () => {
      try {
        // 模拟一个API调用来获取未读消息状态
        // 实际项目中，你需要替换为真实的API请求
        // const response = await axios.get('MessagesController/getUnreadCount', { params: { userId: user.value.userId } });
        // hasNewMessages.value = response.data.unreadCount > 0;

        // 临时模拟数据，假定有未读消息
        hasNewMessages.value = true;
      } catch (error) {
        console.error('检查未读消息失败:', error);
        hasNewMessages.value = false;
      }
    };

    const logout = () => {
      sessionStorage.removeItem('user');
      router.push({ path: '/index' });
    };

    const switchToMerchant = () => {
      showMerchantApplyModal.value = true;
    };

    const openEditModal = () => {
      if (user.value) {
        editFormData.value.firstName = user.value.firstName || '';
        editFormData.value.lastName = user.value.lastName || '';
        editFormData.value.userId = user.value.userId || '';
        editFormData.value.email = user.value.email || '';
      }
      showEditModal.value = true;
    };

    const closeEditModal = () => {
      showEditModal.value = false;
    };

    const closeMerchantApplyModal = () => {
      showMerchantApplyModal.value = false;
    };

    const applyForMerchant = async () => {
      try {
        console.log('用户申请开店:', user.value.userId);
        
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        toast.success('申请开店成功！管理员将在1-3个工作日内审核您的申请。');
        closeMerchantApplyModal();
      } catch (error) {
        console.error('申请开店失败:', error);
        toast.error('申请开店失败，请重试！');
      }
    };

    const submitEdits = async () => {
      if (!editFormData.value.userId) {
        toast.warning('手机号不能为空！');
        return;
      }

      try {
        const response = await axios.post('UserController/updateUserInfo', {
          userId: user.value.userId,
          newUserId: editFormData.value.userId,
          firstName: editFormData.value.firstName,
          lastName: editFormData.value.lastName,
          email: editFormData.value.email,
        });
        
        if (response.data === 1) {
          user.value.userId = editFormData.value.userId;
          user.value.firstName = editFormData.value.firstName;
          user.value.lastName = editFormData.value.lastName;
          user.value.email = editFormData.value.email;

          sessionStorage.setItem('user', JSON.stringify(user.value));
          
          toast.success('个人信息修改成功！');
          closeEditModal();
        } else {
          toast.error('个人信息修改失败！');
        }
      } catch (error) {
        console.error(error);
        toast.error('个人信息修改失败！');
      }
    };

    const myfavorite = () => {
      router.push({ path: '/favorites' });
    };

    const navigateTo = (page) => {
      const pageRoutes = {
        'notifications': '/notifications'
      };
      if (pageRoutes[page]) {
        router.push({ path: pageRoutes[page] });
      } else {
        toast.warning('功能待开发');
      }
      // 如果进入了消息通知页面，则清除红点标记
      if (page === 'notifications') {
          hasNewMessages.value = false;
      }
    };
    
    return {
      user,
      formattedPhone,
      loading,
      errorMessage,
      showEditModal,
      showMerchantApplyModal,
      editFormData,
      logout,
      openEditModal,
      closeEditModal,
      closeMerchantApplyModal,
      applyForMerchant,
      submitEdits,
      myfavorite,
      navigateTo,
      switchToMerchant,
      showAddressSection,
      hasNewMessages
    };
  },
};
</script>

<style scoped>
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
.user-name, .user-full-name, .user-phone, .user-email {
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
.user-full-name .first-name {
  margin-right: 5px;
}
.user-name .edit-icon, 
.user-full-name .full-name-icon,
.user-phone .phone-icon,
.user-email .email-icon {
  margin-right: 8px;
  color: #3498db;
}
.edit-icon {
  margin-left: auto;
  color: #3498db;
  font-size: 16px;
  cursor: pointer;
}
.menu-section {
  width: 92%;
  max-width: 500px;
  margin: 20px auto;
  transform: translateY(-50px);
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
  background: linear-gradient(135deg, #7b2cce, #3a00b0);
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
  transform: translateY(-50px);
}
.error-message {
  text-align: center;
  padding: 10px;
  background: #ffecec;
  color: #e74c3c;
  border-radius: 8px;
  margin: 10px;
  font-size: 0.9rem;
  transform: translateY(-50px);
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
  max-width: 400px;
  width: 80%;
  box-sizing: border-box;
  text-align: center;
}
.modal-content h3 {
  margin-top: 0;
  color: #2c3e50;
  margin-bottom: 20px;
}
.modal-item {
  margin-bottom: 15px;
  text-align: left;
}
.modal-item label {
  display: block;
  font-weight: 500;
  color: #555;
  margin-bottom: 5px;
}
.modal-content input, .modal-content textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 16px;
  box-sizing: border-box;
}
.modal-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
.modal-buttons button {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
}
.modal-buttons button:first-child {
  background: #3498db;
  color: white;
  transition: background-color 0.3s;
}
.modal-buttons button:first-child:hover {
  background: #2980b9;
}
.modal-buttons button:last-child {
  background: #e0e0e0;
  color: #333;
  transition: background-color 0.3s;
}
.modal-buttons button:last-child:hover {
  background: #c7c7c7;
}

/* 申请开店弹窗样式 */
.merchant-apply-modal {
  text-align: center;
  max-width: 350px;
}

.modal-icon {
  font-size: 3rem;
  color: #ff6b6b;
  margin-bottom: 15px;
}

.modal-icon i {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

.modal-message {
  color: #666;
  font-size: 1rem;
  margin: 15px 0 25px 0;
  line-height: 1.5;
}

.apply-btn {
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e) !important;
  color: white !important;
  font-weight: 600;
  padding: 12px 24px !important;
  margin-right: 10px;
  transition: all 0.3s ease;
}

.apply-btn:hover {
  background: linear-gradient(135deg, #ff5252, #ff7979) !important;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

.cancel-btn {
  background: #e0e0e0 !important;
  color: #666 !important;
  font-weight: 500;
  padding: 12px 24px !important;
  transition: all 0.3s ease;
}

.cancel-btn:hover {
  background: #d0d0d0 !important;
  transform: translateY(-2px);
}

/* 新增：消息红点的样式 */
.menu-item.message-item {
  position: relative;
}

.notification-dot {
  position: absolute;
  top: 15px;
  right: 40px;
  width: 8px;
  height: 8px;
  background-color: #ff4d4f;
  border-radius: 50%;
  border: 1px solid white;
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
  
  .menu-item {
    padding: 14px 16px;
  }
}
</style>