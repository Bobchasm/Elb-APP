<template>
  <div class="shop-management-page">
    <div class="header">
      <h1>我的商铺</h1>
    </div>

    <!-- 状态筛选标签 -->
    <div class="status-tabs">
      <button 
        v-for="tab in tabs" 
        :key="tab.status" 
        :class="{ active: activeTab === tab.status }"
        @click="changeTab(tab.status)"
      >
        {{ tab.label }}
      </button>
    </div>

    <div class="container wrapper">
      <ul class="business-list">
        <li v-for="shop in filteredShops" :key="shop?.id || index">
          <!-- <div class="status-badge" :class="getStatusClass(shop.status)">
            {{ getStatusText(shop.status) }}
          </div> -->
          <img
            :src="shop?.businessImg || 'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/6a48eb69-23ba-473b-8755-3efb4f3d14a7.png'"
            :alt="shop?.businessName || '未命名商铺'" class="logo" @error="handleImageError">
          <div class="business-info-detail">
            <h3>{{ shop?.businessName || '未命名商铺' }}</h3>
            <div class="delivery-info-container">
              <div class="business-info-delivery">
                <p>配送费{{ shop?.deliveryPrice || 0 }}元</p>
              </div>
              <div class="business-info-delivery">
                <p>起送费{{ shop?.startPrice || 0 }}元</p>
              </div>
            </div>
            <div class="business-info-delivery">
              <p>商家地址：{{ shop?.businessAddress || 暂无地址信息 }}</p>
            </div>
          </div>
          <div class="action-buttons">
            <button class="edit-btn" @click="editShop(shop?.id || index)" :disabled="shop.status === 2">编辑</button>
            <button class="delete-btn" @click="deleteShop(shop?.id || index)">删除</button>
          </div>
        </li>
      </ul>
    </div>

    <div class="footer-button-container">
      <button class="apply-button" @click="applyNewShop">申请新店</button>
    </div>

    <div class="footer-nav">
      <router-link to="/merchant/business" class="nav-item active">
        <i class="fa fa-store-alt"></i>
        <span>商铺</span>
      </router-link>
      <router-link to="/merchant/orders" class="nav-item">
        <i class="fa fa-clipboard-list"></i>
        <span>订单</span>
      </router-link>
      <router-link to="/merchant/profile" class="nav-item">
        <i class="fa fa-user"></i>
        <span>我的</span>
      </router-link>
    </div>
  </div>
</template>

<script>
import Swal from 'sweetalert2';
import { ref, onMounted, computed } from 'vue';
import Footer from '../components/Footer.vue';
import AddressManager from '../components/AddressManager.vue';
import request from '../utils/request';
import { useRouter } from 'vue-router';
import { toast } from '../utils/toast';

export default {
  name: 'MyApplication',
  components: {
    AddressManager,
    AddressManager
  },
  setup() {
    const router = useRouter();
    const shops = ref([]);
    const loading = ref(false);
    const errorMessage = ref('');
    const activeTab = ref(null); // 当前选中的状态标签

    // 状态标签配置
    const tabs = [
      { status: null, label: '全部' },
      { status: 0, label: '审核中' },
      { status: 1, label: '已上线' },
      { status: 2, label: '未通过' }
    ];

    // 获取 token 的函数
    const getToken = () => {
      return localStorage.getItem('token') || sessionStorage.getItem('token');
    };

    // 状态文本映射
    const getStatusText = (status) => {
      switch (status) {
        case 0: return '审核中';
        case 1: return '已上线';
        case 2: return '审核未通过';
        default: return '未知状态';
      }
    };

    // 状态样式映射
    const getStatusClass = (status) => {
      switch (status) {
        case 0: return 'status-pending';
        case 1: return 'status-approved';
        case 2: return 'status-rejected';
        default: return '';
      }
    };

    // 根据当前选中的标签筛选商铺
    const filteredShops = computed(() => {
      if (activeTab.value === null) {
        return shops.value;
      }
      return shops.value.filter(shop => shop.status === activeTab.value);
    });

    // 切换标签
    const changeTab = (status) => {
      activeTab.value = status;
      loadShops(status);
    };

    // 加载商铺列表
    const loadShops = async (status = null) => {
      loading.value = true;
      errorMessage.value = '';

      try {
        const token = getToken();
        if (!token) {
          toast.warning('用户未登录，请先登录！');
          router.push({ path: '/login' });
          return;
        }

        // 获取用户信息
        const userResponse = await request.get('/api/person', {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });

        if (userResponse && userResponse.id) {
          // 根据状态参数获取商铺列表
          const params = { userId: userResponse.id };
          if (status !== null) {
            params.status = status;
          }

          const shopResponse = await request.get('/api/businesses/merchant', {
            params,
            headers: {
              'Authorization': `Bearer ${token}`
            }
          });

          if (shopResponse) {
            shops.value = shopResponse.data;
            console.log('商铺列表加载成功:', shops.value);
          }
        }
      } catch (error) {
        console.error('获取商铺列表失败:', error);

        if (error.response && error.response.status === 401) {
          // Token 过期或无效
          toast.error('登录已过期，请重新登录！');
          localStorage.removeItem('token');
          sessionStorage.removeItem('token');
          router.push({ path: '/login' });
        } else {
          errorMessage.value = '获取商铺列表失败，请重试！';
          toast.error('获取商铺列表失败，请重试！');
        }
      } finally {
        loading.value = false;
      }
    };

    // 删除商铺
    const deleteShop = async (shopId) => {
      const result = await Swal.fire({
        title: '确定删除此店铺？',
        text: "删除后将无法恢复！",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#dc3545',
        cancelButtonColor: '#6c757d',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消'
      });

      if (result.isConfirmed) {
        try {
          const token = getToken();
          if (!token) {
            toast.warning('用户未登录，请先登录！');
            router.push({ path: '/login' });
            return;
          }

          await request.delete(`/api/businesses/${shopId}`, {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          });

          // 删除成功后更新本地列表
          shops.value = shops.value.filter(shop => shop.id !== shopId);
          toast.success('店铺删除成功！');
        } catch (error) {
          console.error('删除店铺失败:', error);
          toast.error('删除店铺失败，请重试！');
        }
      }
    };

    // 编辑商铺
    const editShop = (shopId) => {
      if (router) {
        router.push(`/merchant/businessinfo?businessId=${shopId}`);
      } else {
        console.warn('Vue Router 未配置。将执行模拟跳转。');
        alert(`跳转到 /merchant/businessinfo?businessId=${shopId}`);
      }
    };

    // 申请新店
    const applyNewShop = async () => {
      try {
        // 获取 token 放在最前面
        const token = getToken();
        if (!token) {
          toast.warning('用户未登录，请先登录！');
          router.push({ path: '/login' });
          return;
        }

        // 使用一个弹窗同时收集图片和其他信息
        const { value: formValues } = await Swal.fire({
          title: '申请新店',
          html: `
            <div style="text-align: left;">
              <div style="margin-bottom: 15px;">
                <label for="businessImg" style="display: block; margin-bottom: 5px;">商铺图片</label>
                <input id="businessImg" type="file" accept="image/*" style="width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
              </div>
              <input id="businessName" class="swal2-input" placeholder="商铺名称" required>
              <input id="businessAddress" class="swal2-input" placeholder="商铺地址" required>
              <textarea id="businessExplain" class="swal2-textarea" placeholder="商铺介绍"></textarea>
              <input id="deliveryPrice" class="swal2-input" placeholder="配送费(元)" type="number" min="0" step="0.1" required>
              <input id="startPrice" class="swal2-input" placeholder="起送价(元)" type="number" min="0" step="0.1" required>
              <select id="orderTypeId" class="swal2-input" required style="width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
                <option value="" disabled selected>请选择商铺类型</option>
                <option value="1">美食</option>
                <option value="2">早餐</option>
                <option value="3">跑腿代购</option>
                <option value="4">汉堡披萨</option>
                <option value="5">甜品饮品</option>
                <option value="6">速食简食</option>
                <option value="7">地方小吃</option>
                <option value="8">米粉面馆</option>
                <option value="9">包子粥铺</option>
                <option value="10">炸鸡炸串</option>
              </select>
            </div>
          `,
          focusConfirm: false,
          showCancelButton: true,
          confirmButtonText: '提交申请',
          cancelButtonText: '取消',
          preConfirm: async () => {
            // 获取表单值
            const businessName = document.getElementById('businessName').value;
            const businessAddress = document.getElementById('businessAddress').value;
            const businessExplain = document.getElementById('businessExplain').value;
            const deliveryPrice = parseFloat(document.getElementById('deliveryPrice').value) || 0;
            const startPrice = parseFloat(document.getElementById('startPrice').value) || 0;
            const orderTypeId = document.getElementById('orderTypeId').value;
            const imageFile = document.getElementById('businessImg').files[0];

            // 验证必填字段
            if (!businessName || !businessAddress || !orderTypeId) {
              Swal.showValidationMessage('请填写必填项');
              return false;
            }

            let imageUrl = '';
            // 如果有上传图片，先上传图片
            if (imageFile) {
              const formData = new FormData();
              formData.append('file', imageFile);
              
              try {
                const uploadResponse = await request.post('/upload', formData, {
                  headers: {
                    'Content-Type': 'multipart/form-data',
                    'Authorization': `Bearer ${token}` // 使用外部的 token
                  }
                });

                if (uploadResponse && uploadResponse.success && uploadResponse.data) {
                  imageUrl = uploadResponse.data;
                } else {
                  Swal.showValidationMessage(uploadResponse?.message || '图片上传失败');
                  return false;
                }
              } catch (error) {
                console.error('图片上传出错:', error.response || error);
                Swal.showValidationMessage(
                  error.response?.data?.message || 
                  error.message || 
                  '图片上传出错'
                );
                return false;
              }
            }

            return {
              businessName,
              businessAddress,
              businessExplain,
              deliveryPrice,
              startPrice,
              businessImg: imageUrl,
              orderTypeId: parseInt(orderTypeId)
            };
          }
        });

        if (formValues) {
          // 获取用户ID
          const userResponse = await request.get('/api/person', {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          });

          if (!userResponse || !userResponse.id) {
            toast.error('获取用户信息失败');
            return;
          }

          // 提交申请
          const applicationData = {
            ...formValues,
            userId: userResponse.id
          };

          const response = await request.post('/api/businesses/apply', applicationData, {
            headers: {
              'Authorization': `Bearer ${token}`
            }
          });

          if (response && response.success) {
            toast.success('新店申请提交成功！');
            await loadShops(activeTab.value);
          } else {
            toast.error(response?.message || '申请提交失败');
          }
        }
      } catch (error) {
        console.error('申请新店出错:', error);
        toast.error(error.response?.data?.message || '申请新店过程中出错，请重试');
      }
    };

    // 页面加载时获取商铺列表
    onMounted(() => {
      // 默认加载全部商铺
      changeTab(null);
    });

    return {
      shops,
      loading,
      errorMessage,
      tabs,
      activeTab,
      filteredShops,
      deleteShop,
      editShop,
      applyNewShop,
      getStatusText,
      getStatusClass,
      changeTab
    };
  }
};
</script>

<style>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css');

/* ----------------------- 基础样式 ----------------------- */
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: Arial, sans-serif;
  background-color: #f8f8f8;
  color: #333;
  line-height: 1.6;
}

/* ----------------------- 顶部标题栏 ----------------------- */
.header {
  width: 100%;
  height: 12vw;
  max-height: 60px;
  background-color: #007bff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: center;
}

.header h1 {
  font-size: 5vw;
  font-size: clamp(18px, 5vw, 24px);
  color: #fff;
  margin: 0;
}

/* ----------------------- 状态标签栏 ----------------------- */
.status-tabs {
  display: flex;
  justify-content: space-around;
  padding: 10px 0;
  background-color: #fff;
  border-bottom: 1px solid #eee;
  position: sticky;
  top: 60px;
  z-index: 99;
}

.status-tabs button {
  padding: 8px 12px;
  border: none;
  background: none;
  cursor: pointer;
  font-size: 14px;
  color: #666;
  border-radius: 4px;
  transition: all 0.3s;
}

.status-tabs button.active {
  color: #007bff;
  background-color: #e6f2ff;
  font-weight: bold;
}

.status-tabs button:hover {
  background-color: #f0f0f0;
}

/* ----------------------- 店铺列表 ----------------------- */
.container {
  max-width: 600px;
  margin: 0 auto;
  padding: 0 4vw;
  padding-bottom: 140px;
}

.wrapper .business-list {
  width: 100%;
  padding: 0;
  margin: 15px 0;
  list-style: none;
}

.wrapper .business-list li {
  padding: 12px;
  border-bottom: 1px solid #f0f0f0;
  background-color: #fff;
  border-radius: 8px;
  margin-bottom: 12px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
}

.wrapper .business-list li:hover {
  background-color: #f9f9f9;
}

.business-item {
  display: flex;
  align-items: center;
  width: 100%;
}

.business-image-container {
  width: 20vw;
  height: 20vw;
  max-width: 100px;
  max-height: 100px;
  min-width: 80px;
  min-height: 80px;
  flex-shrink: 0;
  margin-right: 12px;
  position: relative;
  overflow: hidden;
  border-radius: 6px;
}

.business-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 6px;
}

.logo {
  width: 20vw;
  height: 20vw;
  object-fit: cover;
  border-radius: 1vw;
  margin-right: 3vw;
}

.business-info-detail {
  flex: 1;
}

.business-info-detail h3 {
  font-size: 4vw;
  font-size: clamp(16px, 4vw, 20px);
  margin: 0 0 8px 0;
  color: #333;
  font-weight: 600;
}

.business-info-delivery {
  font-size: 3.5vw;
  font-size: clamp(14px, 3.5vw, 16px);
  color: #666;
  margin: 4px 0;
  display: flex;
}

.delivery-info-container {
  display: flex;
  gap: 10px;
}

.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-left: 10px;
}

.action-buttons button {
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 6px;
  padding: 8px 12px;
  cursor: pointer;
  font-size: 3.5vw;
  font-size: clamp(12px, 3.5vw, 14px);
  transition: all 0.3s;
  white-space: nowrap;
}

.action-buttons button.edit-btn {
  color: #007bff;
  border-color: #007bff;
}

.action-buttons button.delete-btn {
  color: #dc3545;
  border-color: #dc3545;
}

.action-buttons button:hover {
  color: #fff;
}

.action-buttons button.edit-btn:hover {
  background-color: #007bff;
}

.action-buttons button.delete-btn:hover {
  background-color: #dc3545;
}

.action-buttons button:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* ----------------------- 状态标签 ----------------------- */
.status-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 4px 8px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  color: white;
  z-index: 1;
}

.status-pending {
  background-color: #ffc107; /* 黄色，表示审核中 */
}

.status-approved {
  background-color: #28a745; /* 绿色，表示已上线 */
}

.status-rejected {
  background-color: #dc3545; /* 红色，表示审核未通过 */
}

/* ----------------------- 底部按钮 ----------------------- */
.footer-button-container {
  position: fixed;
  bottom: 80px;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  padding: 0 4vw;
  box-sizing: border-box;
  z-index: 99;
}

.apply-button {
  width: 100%;
  max-width: 500px;
  background-color: #007bff;
  color: #fff;
  padding: 12px 0;
  border-radius: 10px;
  text-decoration: none;
  font-size: 4.5vw;
  font-size: clamp(16px, 4.5vw, 18px);
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  border: none;
  cursor: pointer;
  transition: background-color 0.3s;
}

.apply-button:hover {
  background-color: #0069d9;
}

/* ----------------------- 底部导航栏 ----------------------- */
.footer-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: space-around;
  align-items: center;
  height: 60px;
  background-color: #fff;
  border-top: 1px solid #f0f0f0;
  z-index: 100;
}

.footer-nav .nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  color: #666;
  text-decoration: none;
  font-size: 12px;
  flex-grow: 1;
  text-align: center;
  padding: 8px 0;
}

.footer-nav .nav-item i {
  font-size: 20px;
  margin-bottom: 4px;
}

.footer-nav .nav-item.active {
  color: #007bff;
}

/* 加载状态 */
.loading {
  text-align: center;
  padding: 20px;
  color: #666;
}

.error-message {
  color: #dc3545;
  text-align: center;
  padding: 15px;
  background-color: #f8d7da;
  border-radius: 6px;
  margin: 15px;
}
</style>