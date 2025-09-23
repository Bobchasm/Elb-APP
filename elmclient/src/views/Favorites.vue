<template>
  <div class="container">
    <BackButton />
    <div class="header">
      <i class="fas fa-chevron-left back-icon" @click="goBack"></i>
      <h1 class="title">我的收藏</h1>
    </div>

    <div v-if="loading" class="loading-message">
      <i class="fas fa-spinner fa-spin"></i> 加载中...
    </div>

    <div v-else-if="errorMessage" class="error-message">
      <i class="fas fa-exclamation-circle"></i> {{ errorMessage }}
    </div>

    <div v-else-if="favoriteList.length === 0" class="empty-message">
      <i class="fas fa-heart-broken"></i>
      <p>还没有收藏的店铺，快去逛逛吧！</p>
    </div>

    <div v-else class="business-list">
      <div v-for="business in favoriteList" :key="business.businessId" class="business-item"
        @click="goToBusinessInfo(business.businessId)">
        <img :src="business.businessImg" :alt="business.businessName" class="business-img">
        <div class="business-details">
          <div class="business-name">{{ business.businessName }}</div>
          <div class="business-info-row">
            <span class="star-rating">
              <i class="fas fa-star"></i> {{ business.starRating }}
            </span>
            <span class="sales-volume">月售{{ business.salesVolume }}单</span>
          </div>
          <div class="business-delivery-row">
            <span class="delivery-fee">配送费￥{{ business.deliveryFee }}</span>
            <span class="start-price">起送费￥{{ business.startPrice }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from '@/utils/toast';
import request from '@/utils/request'; // 使用封装好的request
import BackButton from '../components/BackButton.vue';

export default {
  name: 'Favorites',
  components: { BackButton },
  setup() {
    const router = useRouter();
    const favoriteList = ref([]);
    const loading = ref(false);
    const errorMessage = ref('');

    const fetchFavorites = async () => {
      loading.value = true;
      errorMessage.value = '';
      try {
        const userInfo = JSON.parse(sessionStorage.getItem('userInfo'));
        if (!userInfo || !userInfo.id) {
          toast.warning('请先登录以查看收藏列表！');
          router.push('/login');
          return;
        }

        // 调用后端API获取收藏列表
        const response = await request.get(`/api/merchant/interaction/collections/${userInfo.id}`);

        // 将后端返回的数据映射到前端需要的格式
        favoriteList.value = response.data.map(business => ({
          businessId: business.id,
          businessName: business.businessName,
          businessImg: business.businessImg||'https://sunnybigevent.oss-cn-beijing.aliyuncs.com/b8b083fd-1bd5-4417-a790-476ffbef9de3.png',
          starRating: Number(business.score) || 0, // 确保是数字
          salesVolume: business.salesCount || 0,
          deliveryFee: Number(business.deliveryPrice) || 0,
          startPrice: Number(business.startPrice) || 0
        }));

      } catch (error) {
        console.error('获取收藏列表失败:', error);
        errorMessage.value = '获取收藏列表失败，请稍后重试。';
        toast.error(errorMessage.value);
      } finally {
        loading.value = false;
      }
    };

    const goToBusinessInfo = (businessId) => {
      router.push({
        path: '/businessInfo',
        query: { businessId: businessId }
      });
    };

    const goBack = () => {
      router.back();
    };

    onMounted(() => {
      fetchFavorites();
    });

    return {
      favoriteList,
      loading,
      errorMessage,
      goToBusinessInfo,
      goBack
    };
  }
};
</script>


<style scoped>
.container {
  width: 100%;
    height: 100%;
  /* max-width: 600px; */
  margin: 0;
  padding-bottom: 40px;
  background-color: #f0f2f5;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  display: flex;
  flex-direction: column;
}

.header {
  /* padding: 20px; */
  text-align: center;
  background: linear-gradient(to right, #3a7bd5, #00d2ff);
  color: white;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 16px 16px 0 0;
  position: relative;
  overflow: hidden;
  margin-bottom: 20px;
}
.title {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
  color:white;
}
.back-icon {
  position: absolute;
  left: 5px;
  font-size: 1.2rem;
  color: #666;
  cursor: pointer;
  padding: 5px;
}

.loading-message,
.error-message,
.empty-message {
  text-align: center;
  padding: 50px 20px;
  color: #777;
  font-size: 1rem;
}

.error-message {
  color: #e74c3c;
}

.empty-message p {
  margin-top: 10px;
  font-size: 0.9rem;
  color: #999;
}

.empty-message .fas {
  font-size: 2.5rem;
  color: #ccc;
  margin-bottom: 10px;
}

.loading-message .fas {
  color: #3498db;
  margin-right: 8px;
}

.business-list {
  /* padding: 25px; */
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.business-item {
  display: flex;
  align-items: center;
  background-color: #fff;
  padding: 15px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  cursor: pointer;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.business-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
}

.business-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 8px;
  margin-right: 15px;
  border: 1px solid #eee;
}

.business-details {
  flex: 1;
}

.business-name {
  font-size: 1rem;
  font-weight: 600;
  color: #333;
  margin-bottom: 8px;
}

.business-info-row,
.business-delivery-row {
  display: flex;
  align-items: center;
  font-size: 0.85rem;
  color: #777;
}

.business-info-row {
  margin-bottom: 5px;
}

.star-rating i {
  color: #f39c12;
  margin-right: 5px;
}

.sales-volume {
  margin-left: 15px;
}

.delivery-fee {
  margin-right: 15px;
}

/* 新增的起送费样式 */
.start-price {
  color: #ff5722;
  font-weight: 500;
}
</style>