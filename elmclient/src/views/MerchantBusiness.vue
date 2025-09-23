<template>
  <div class="shop-management-page">
    <div class="header">
      <h1>我的商铺</h1>
    </div>

    <div class="container wrapper">
      <ul class="business-list">
        <li v-for="shop in shops" :key="shop?.id || index">
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
            <button class="edit-btn" @click="editShop(shop?.id || index)">编辑</button>
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
import { ref, onMounted } from 'vue';
import Footer from '../components/Footer.vue';
import AddressManager from '../components/AddressManager.vue';
import request from '../utils/request';
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
    const shops = ref([]);
    const loading = ref(false);
    const errorMessage = ref('');

    // 获取 token 的函数
    const getToken = () => {
      return localStorage.getItem('token') || sessionStorage.getItem('token');
    };

    // 加载商铺列表
    const loadShops = async (status = 1) => {
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
          // 使用用户ID获取商铺列表
          const shopResponse = await request.get('/api/businesses/merchant', {
            params: {
              userId: userResponse.id,
              status: status
            },
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
                    'Content-Type': 'multipart/form-data'
                  }
                });

                console.log('提交的数据:', {
                ...formValues,
                userId: userResponse.id
              });


                if (uploadResponse && uploadResponse.success) {
                  imageUrl = uploadResponse.data;
                } else {
                  Swal.showValidationMessage('图片上传失败');
                  return false;
                }
              } catch (error) {
                Swal.showValidationMessage('图片上传出错');
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
          const token = getToken();
          if (!token) {
            toast.warning('用户未登录，请先登录！');
            router.push({ path: '/login' });
            return;
          }

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
            // 刷新店铺列表
            console.log('刷新店铺列表response.data', response.data);
            console.log('刷新店铺列表response', response);
            await loadShops();
          } else {
            toast.error(response?.message || '申请提交失败');
          }
        }
      } catch (error) {
        console.error('申请新店出错:', error);
        toast.error('申请新店过程中出错，请重试');
      }
    };

    // 页面加载时获取商铺列表
    onMounted(() => {
      loadShops();
    });

    return {
      shops,
      loading,
      errorMessage,
      deleteShop,
      editShop,
      applyNewShop
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

/* ----------------------- 店铺列表 ----------------------- */
.container {
  max-width: 600px;
  margin: 0 auto;
  padding: 0 4vw;
  padding-bottom: 120px;
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
  /* 让子元素横向排列 */
  gap: 10px;
  /* 可选：设置间距 */
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

/* ----------------------- 底部按钮 ----------------------- */
.footer-button-container {
  position: fixed;
  bottom: 70px;
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