<template>
  <div class="admin-container">
    <div class="container">
      <div class="top-background">
        <h1>管理员管理平台</h1>
        <button class="top-logout-btn" @click="logout">
          <i class="fas fa-sign-out-alt"></i> 退出
        </button>
      </div>

      <div class="user-card">
        <div class="avatar">
          <i class="fas fa-user"></i>
        </div>
        <div class="user-details">
          <div class="user-name">
            <i class="fas fa-user-tag full-name-icon"></i>
            <span>张管理员</span>
          </div>
          <div class="user-full-name">
            <i class="fas fa-id-card full-name-icon"></i>
            <span>ID: ADMIN20240520001</span>
          </div>
        </div>
      </div>

      <div class="stats-container">
        <div class="stat-card users">
          <i class="fas fa-users"></i>
          <h3>总用户人数</h3>
          <div class="number">12,584</div>
        </div>

        <div class="stat-card shops">
          <i class="fas fa-store"></i>
          <h3>总店铺数</h3>
          <div class="number">3,267</div>
        </div>

        <div class="stat-card revenue">
          <i class="fas fa-yen-sign"></i>
          <h3>总营业额</h3>
          <div class="number">¥865万</div>
        </div>
      </div>

      <div class="review-section">
        <div class="review-tabs">
          <div
            class="review-tab"
            :class="{ active: activeTab === 'user-review' }"
            @click="activeTab = 'user-review'"
          >
            用户审核
          </div>
          <div
            class="review-tab"
            :class="{ active: activeTab === 'shop-review' }"
            @click="activeTab = 'shop-review'"
          >
            商铺审核
          </div>
        </div>

        <div class="review-content" :class="{ active: activeTab === 'user-review' }">
          <div v-for="(user, index) in userReviews" :key="'user-' + index" class="review-item">
            <div class="review-info">
              <h3>{{ user.name }}</h3>
              <p>{{ user.description }} · {{ user.date }}</p>
            </div>
            <button class="review-btn" @click="reviewItem('user', index)">审核</button>
          </div>
        </div>

        <div class="review-content" :class="{ active: activeTab === 'shop-review' }">
          <div v-for="(shop, index) in shopReviews" :key="'shop-' + index" class="review-item">
            <div class="review-info">
              <h3>{{ shop.name }}</h3>
              <p>{{ shop.description }} · {{ shop.date }}</p>
            </div>
            <button class="review-btn" @click="reviewItem('shop', index)">审核</button>
          </div>
        </div>
      </div>

      <div class="bottom-nav">
        <router-link to="/admin/home" class="nav-item" :class="{ active: activeNav === 'home' }" @click.native="setActiveNav('home')">
          <i class="fas fa-home"></i>
          <span>首页</span>
        </router-link>
        <router-link to="/admin/users" class="nav-item" :class="{ active: activeNav === 'users' }" @click.native="setActiveNav('users')">
          <i class="fas fa-user-friends"></i>
          <span>用户管理</span>
        </router-link>
        <router-link to="/admin/business" class="nav-item" :class="{ active: activeNav === 'shops' }" @click.native="setActiveNav('shops')">
          <i class="fas fa-store"></i>
          <span>商铺管理</span>
        </router-link>
      </div>

      <div v-if="showReviewModal" class="modal-overlay" @click.self="showReviewModal = false">
        <div class="modal-content">
          <div class="modal-header">
            <h3>{{ modalTitle }}</h3>
            <span class="close-btn" @click="showReviewModal = false">&times;</span>
          </div>
          <div class="modal-body">
            <div v-for="(value, key) in modalData" :key="key" class="modal-item">
              <label>{{ keyMap[key] || key }}:</label>
              <span>{{ value }}</span>
            </div>
          </div>
          <div class="modal-footer">
            <button class="modal-btn approve-btn" @click="showReviewModal = false; alert('已批准');">批准</button>
            <button class="modal-btn reject-btn" @click="showReviewModal = false; alert('已拒绝');">拒绝</button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminPlatform',
  data() {
    return {
      activeTab: 'user-review',
      activeNav: 'home',
      showReviewModal: false,
      modalTitle: '',
      modalData: {},
      keyMap: {
        username: '用户名',
        fullName: '姓名',
        phone: '手机号',
        email: '邮箱',
        gender: '性别',
        merchantName: '商家名称',
        shopName: '商铺名称',
        shopType: '商铺类型',
        address: '地址',
        description: '简介',
      },
      userReviews: [
        { name: '李明', description: '申请成为商家', date: '2023-05-20 14:30', username: 'liming123', fullName: '李明', phone: '13812345678', email: 'li.m@example.com', gender: '男' },
        { name: '王小红', description: '申请成为商家', date: '2023-05-19 10:15', username: 'wangxh', fullName: '王小红', phone: '13987654321', email: 'wang.xh@example.com', gender: '女' },
        { name: '赵四', description: '申请成为商家', date: '2023-05-18 16:45', username: 'zhaosi', fullName: '赵四', phone: '13555556666', email: 'zhao.si@example.com', gender: '男' }
      ],
      shopReviews: [
        { name: '阳光餐厅', description: '申请开店', date: '2023-05-20 09:20', merchantName: '陈老板', shopName: '阳光餐厅', shopType: '中餐', address: '光明路123号', description: '专注于家常菜的温馨小馆' },
        { name: '时尚服装店', description: '申请开店', date: '2023-05-19 15:40', merchantName: '张老板', shopName: '时尚服装店', shopType: '服装', address: '新华街45号', description: '最新潮流服饰，每日上新' },
        { name: '数码科技馆', description: '申请开店', date: '2023-05-18 11:30', merchantName: '王老板', shopName: '数码科技馆', shopType: '电子产品', address: '解放西路88号', description: '提供最新款的手机、电脑等电子产品' }
      ]
    }
  },
  methods: {
    reviewItem(type, index) {
      if (type === 'user') {
        const user = this.userReviews[index];
        this.modalTitle = `用户审核 - ${user.name}`;
        this.modalData = {
          username: user.username,
          fullName: user.fullName,
          phone: user.phone,
          email: user.email,
          gender: user.gender,
        };
      } else if (type === 'shop') {
        const shop = this.shopReviews[index];
        this.modalTitle = `商铺审核 - ${shop.name}`;
        this.modalData = {
          merchantName: shop.merchantName,
          shopName: shop.shopName,
          shopType: shop.shopType,
          address: shop.address,
          description: shop.description,
        };
      }
      this.showReviewModal = true;
    },
    logout() {
      if (confirm('确定要退出登录吗？')) {
        alert('已成功退出登录');
      }
    },
    setActiveNav(nav) {
      this.activeNav = nav;
    }
  }
}
</script>

<style scoped>
/* 保持所有原有样式不变 */
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.3/css/all.min.css');

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: 'Helvetica Neue', Arial, sans-serif;
}

.admin-container {
  background-color: #f5f5f5;
  color: #333;
  line-height: 1.6;
  padding: 0;
  max-width: 100%;
  overflow-x: hidden;
  padding-bottom: 70px;
}
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
.top-logout-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  border-radius: 20px;
  padding: 8px 15px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 5px;
  z-index: 10;
}
.top-logout-btn:hover {
  background: rgba(255, 255, 255, 0.3);
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
  display: flex;
  align-items: center;
  justify-content: center;
}
.avatar i {
  font-size: 2.5rem;
  color: #777;
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
.stats-container {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  margin-bottom: 25px;
  overflow-x: auto;
  padding-bottom: 5px;
  width: 92%;
  max-width: 500px;
  transform: translateY(-40px);
}
.stats-container::-webkit-scrollbar {
  display: none;
}
.stat-card {
  background: white;
  border-radius: 12px;
  padding: 15px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  flex: 1;
  min-width: 100px;
}
.stat-card i {
  font-size: 1.8rem;
  margin-bottom: 8px;
}
.stat-card.users i {
  color: #0097ff;
}
.stat-card.shops i {
  color: #ff6700;
}
.stat-card.revenue i {
  color: #4caf50;
}
.stat-card h3 {
  font-size: 0.85rem;
  margin-bottom: 8px;
  color: #555;
  font-weight: 500;
}
.stat-card .number {
  font-size: 1.4rem;
  font-weight: 700;
  color: #333;
}
.review-section {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  margin-bottom: 30px;
  width: 92%;
  max-width: 500px;
  transform: translateY(-40px);
}
.review-tabs {
  display: flex;
  border-bottom: 1px solid #f0f0f0;
}
.review-tab {
  flex: 1;
  text-align: center;
  padding: 15px 0;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #888;
}
.review-tab.active {
  color: #0097ff;
  border-bottom: 3px solid #0097ff;
}
.review-content {
  display: none;
  padding: 0;
}
.review-content.active {
  display: block;
}
.review-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #f0f0f0;
}
.review-item:last-child {
  border-bottom: none;
}
.review-info {
  flex: 1;
}
.review-info h3 {
  font-size: 1rem;
  margin-bottom: 5px;
  color: #333;
}
.review-info p {
  color: #888;
  font-size: 0.85rem;
}
.review-btn {
  background: #0097ff;
  color: white;
  border: none;
  border-radius: 20px;
  padding: 8px 15px;
  font-size: 0.9rem;
  cursor: pointer;
  transition: all 0.3s ease;
}
.review-btn:hover {
  background: #0085e0;
}
.bottom-nav {
  position: fixed;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 100%;
  max-width: 600px;
  background: white;
  display: flex;
  justify-content: space-around;
  padding: 12px 0;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  z-index: 1000;
}
.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-decoration: none;
  color: #888;
  font-size: 0.8rem;
}
.nav-item.active {
  color: #0097ff;
}
.nav-item i {
  font-size: 1.2rem;
  margin-bottom: 4px;
}
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}
.modal-content {
  background: white;
  border-radius: 12px;
  padding: 20px;
  width: 90%;
  max-width: 400px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.3);
  display: flex;
  flex-direction: column;
  gap: 15px;
  animation: fadeIn 0.3s ease-out;
}
@keyframes fadeIn {
  from { opacity: 0; transform: scale(0.95) translateY(20px); }
  to { opacity: 1; transform: scale(1) translateY(0); }
}
.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
  padding-bottom: 10px;
}
.modal-header h3 {
  margin: 0;
  font-size: 1.2rem;
  color: #333;
}
.close-btn {
  font-size: 1.5rem;
  color: #aaa;
  cursor: pointer;
  transition: color 0.2s;
}
.close-btn:hover {
  color: #666;
}
.modal-body {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.modal-item {
  display: flex;
  align-items: baseline;
  padding: 5px;
  border-radius: 8px;
  background: #f8f9fa;
  border: 1px solid #e9ecef;
}
.modal-item label {
  font-weight: bold;
  color: #555;
  margin-right: 10px;
  min-width: 80px;
}
.modal-item span {
  flex: 1;
  color: #333;
  word-break: break-all;
}
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 10px;
  border-top: 1px solid #eee;
}
.modal-btn {
  border: none;
  border-radius: 20px;
  padding: 10px 20px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.3s ease;
}
.approve-btn {
  background-color: #4caf50;
  color: white;
}
.approve-btn:hover {
  background-color: #45a049;
  box-shadow: 0 4px 12px rgba(76, 175, 80, 0.3);
}
.reject-btn {
  background-color: #e0e0e0;
  color: #333;
}
.reject-btn:hover {
  background-color: #c7c7c7;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}
@media (max-width: 480px) {
  .container, .user-card, .stats-container, .review-section {
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
  .stats-container, .review-section {
    width: 90%;
  }
  .top-logout-btn {
    top: 10px;
    right: 10px;
    padding: 6px 12px;
    font-size: 0.8rem;
  }
  .modal-item {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>