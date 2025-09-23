<template>
  <div class="shop-management-page container-fluid">
    <div class="top-background">
      <h1>我的商铺</h1>
    </div>

    <div class="main-content-wrapper">
      <ul class="business-list">
        <li v-for="shop in shops" :key="shop.id" class="business-card">
          <div class="business-info">
            <img :src="shop.img" :alt="shop.name" class="business-img">
            <div class="business-info-detail">
              <h3 class="business-name">{{ shop.name }}</h3>
              <div class="business-info-delivery">
                <p>配送费 {{ shop.deliveryFee }} 元</p>
              </div>
            </div>
          </div>
          
          <div class="action-buttons">
            <button class="edit-btn" @click="editShop(shop.id)">编辑</button>
            <button class="delete-btn" @click="deleteShop(shop.id)">删除</button>
          </div>
        </li>
      </ul>

      <div class="footer-button-container">
        <button class="apply-button" @click="applyNewShop">申请新店</button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import Swal from 'sweetalert2';

export default {
  name: 'MerchantBusiness',
  setup() {
    const router = useRouter();
    const shops = ref([
      {
        id: 1,
        name: "万家饺子（软件园店）",
        img: "https://via.placeholder.com/100",
        deliveryFee: 4
      },
      {
        id: 2,
        name: "喜家德虾仁水饺（西安路店）",
        img: "https://via.placeholder.com/100",
        deliveryFee: 3
      }
    ]);

    const editShop = (shopId) => {
      router.push(`/merchant/businessinfo?shopId=${shopId}`);
    };

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
        const shopIndex = shops.value.findIndex(shop => shop.id === shopId);
        if (shopIndex > -1) {
          shops.value.splice(shopIndex, 1);
          Swal.fire('删除成功', '店铺已删除。', 'success');
        }
      }
    };

    const applyNewShop = async () => {
      const { value: formValues } = await Swal.fire({
        title: '申请新店',
        html:
          '<input id="swal-input1" class="swal2-input" placeholder="商家名称">' +
          '<input id="swal-input2" class="swal2-input" placeholder="商铺名称">' +
          '<input id="swal-input3" class="swal2-input" placeholder="商铺地址">' +
          '<textarea id="swal-input4" class="swal2-textarea" placeholder="简介"></textarea>',
        focusConfirm: false,
        showCancelButton: true,
        confirmButtonText: '确认提交',
        cancelButtonText: '取消',
        preConfirm: () => {
          const merchantName = document.getElementById('swal-input1').value;
          const shopName = document.getElementById('swal-input2').value;
          const shopAddress = document.getElementById('swal-input3').value;
          const description = document.getElementById('swal-input4').value;
          
          if (!merchantName || !shopName || !shopAddress) {
            Swal.showValidationMessage('请填写必填项');
            return false;
          }
          return { merchantName, shopName, shopAddress, description };
        }
      });
      
      if (formValues) {
        Swal.fire('提交成功', '新店申请已提交，等待审核。', 'success');
      }
    };

    return {
      shops,
      editShop,
      deleteShop,
      applyNewShop
    };
  }
};
</script>

<style scoped>
/* ----------------------- 整体布局 ----------------------- */
.shop-management-page {
  /* 使用全局容器样式，并覆盖背景色和阴影 */
  background-color: var(--background-light);
  min-height: 100vh;
  padding: 0;
  padding-bottom: 20vw;
  box-shadow: none;
}
.main-content-wrapper {
  max-width: 600px;
  margin: 0 auto;
  padding: 0 4vw;
}

/* ----------------------- 顶部标题栏 - 样式已完全修改为与参考样式相同 ----------------------- */
.top-background {
  position: sticky;
  top: 0;
  z-index: 100;
  width: 100%;
  height: 100px;
  background: #0097ff;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1); /* 使用参考阴影 */
  border-radius: 16px 16px 0 0; /* 添加圆角 */
  margin-bottom: 50px; /* 添加底部外边距 */
  display: flex;
  justify-content: center;
  align-items: center;
  position: relative;
  overflow: hidden;
}
.top-background::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.2) 0%, rgba(255,255,255,0) 70%);
  /* transform: rotate(30deg); */  /* 删除 transform */
  /* animation: shine 6s infinite linear; */ /* 删除 animation */
}
/* @keyframes shine { */  /* 删除 keyframes */
/*   0% { transform: rotate(30deg) translate(-10%, -10%); } */
/*   100% { transform: rotate(30deg) translate(10%, 10%); } */
/* } */
.top-background h1 {
  color: white;
  font-size: 1.8rem; /* 调整字体大小 */
  font-weight: 600; /* 调整字体粗细 */
  text-shadow: 0 2px 4px rgba(0,0,0,0.1); /* 添加文本阴影 */
  letter-spacing: 1px; /* 调整字间距 */
  margin: 0;
  z-index: 1;
}

/* ----------------------- 店铺列表 ----------------------- */
.business-list {
  width: 100%;
  padding: 0;
  margin: 20px 0;
  list-style: none;
}
.business-card {
  /* 使用统一的卡片样式，圆角、阴影、背景色 */
  background: white;
  border-radius: var(--border-radius-large);
  box-shadow: var(--shadow-large);
  margin-bottom: 15px;
  padding: 15px;
  transition: var(--transition-ease);
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.business-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.business-info {
  display: flex;
  align-items: center;
  gap: 15px;
}
.business-img {
  width: 20vw;
  height: 20vw;
  max-width: 80px;
  max-height: 80px;
  object-fit: cover;
  border-radius: var(--border-radius-small);
  box-shadow: var(--shadow-small);
}
.business-info-detail {
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.business-name {
  font-size: 1.1rem;
  font-weight: 500;
  color: var(--text-dark);
  margin: 0 0 5px 0;
}
.business-info-delivery p {
  font-size: 0.95rem;
  color: var(--text-medium);
  margin: 0;
}

.action-buttons {
  display: flex;
  gap: 10px;
}
.action-buttons button {
  background-color: transparent;
  border-radius: 8px; /* 添加圆角 */
  padding: 8px 16px;
  cursor: pointer;
  font-size: 0.95rem;
  transition: var(--transition-ease);
  font-weight: 500;
  box-shadow: var(--shadow-small);
}
.action-buttons button.edit-btn {
  color: #0097ff;
  border: 1px solid #0097ff;
  background-color: #fff;
}
.action-buttons button.delete-btn {
  color: #dc3545;
  border: 1px solid #dc3545;
  background-color: #fff;
}
.action-buttons button.edit-btn:hover {
  background-color: #0097ff;
  color: white;
  transform: translateY(-2px);
}
.action-buttons button.delete-btn:hover {
  background-color: #dc3545;
  color: white;
  transform: translateY(-2px);
}

/* ----------------------- 底部按钮 ----------------------- */
.footer-button-container {
  position: fixed;
  bottom: 14vw;
  left: 0;
  right: 0;
  max-width: 600px;
  margin: 0 auto;
  padding: 0 4vw;
  box-sizing: border-box;
  z-index: 99;
}
.apply-button {
  width: 100%;
  background-color: #0097ff;
  color: white;
  padding: 14px;
  border-radius: 8px; /* 添加圆角 */
  font-size: 0.95rem;
  font-weight: 600;
  box-shadow: var(--shadow-medium);
  border: none;
  cursor: pointer;
  transition: var(--transition-ease);
}
.apply-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
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
  height: 12vw;
  background-color: white;
  border-top: 1px solid var(--border-color);
  z-index: 100;
  box-shadow: var(--shadow-small);
}
.footer-nav .nav-item {
  color: var(--text-light);
  font-size: 0.9rem;
  transition: var(--transition-ease);
}
.footer-nav .nav-item i {
  font-size: 1.5rem;
  margin-bottom: 0.5vw;
}
.footer-nav .nav-item.active {
  color: #0097ff;
}

/* ----------------------- 媒体查询 ----------------------- */
@media (max-width: 480px) {
  .top-background {
    height: 90px;
    margin-bottom: 50px;
    border-radius: 0;
  }
  .top-background h1 {
    font-size: 1.5rem;
  }
  .business-name {
    font-size: 1rem;
  }
  .business-info-delivery p {
    font-size: 0.85rem;
  }
  .action-buttons button {
    font-size: 0.85rem;
    padding: 6px 12px;
  }
}
</style>