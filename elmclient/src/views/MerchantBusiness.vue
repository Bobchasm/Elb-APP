<template>
  <div class="shop-management-page">
    <div class="header">
      <h1>我的商铺</h1>
    </div>

    <div class="container wrapper">
      <ul class="business-list">
        <li v-for="shop in shops" :key="shop.id">
          <div class="business-info">
            <img :src="shop.img" :alt="shop.name">
            <div class="business-info-detail">
              <h3>{{ shop.name }}</h3>
              <div class="business-info-delivery">
                <p>配送费{{ shop.deliveryFee }}元</p>
              </div>
            </div>
          </div>
          
          <div class="action-buttons">
            <button class="edit-btn" @click="editShop(shop.id)">编辑</button>
            <button class="delete-btn" @click="deleteShop(shop.id)">删除</button>
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

export default {
  name: 'MerchantBusiness',
  data() {
    return {
      shops: [
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
      ]
    };
  },
  methods: {
    editShop(shopId) {
      // 1. 点击编辑按钮后跳转到 /merchant/businessinfo 页面
      // 假设你已经配置了 Vue Router
      if (this.$router) {
        this.$router.push(`/merchant/businessinfo?shopId=${shopId}`);
      } else {
        console.warn('Vue Router 未配置。将执行模拟跳转。');
        alert(`跳转到 /merchant/businessinfo?shopId=${shopId}`);
      }
    },
    async deleteShop(shopId) {
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
        console.log(`正在发送删除店铺请求，ID: ${shopId}`);
        const shopIndex = this.shops.findIndex(shop => shop.id === shopId);
        if (shopIndex > -1) {
          this.shops.splice(shopIndex, 1);
          Swal.fire('删除成功', '店铺已删除。', 'success');
        }
      }
    },
    async applyNewShop() {
      // 2. 点击申请新店后弹出模态框表单
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
        // 表单提交成功
        console.log('新店申请提交成功：', formValues);
        Swal.fire('提交成功', '新店申请已提交，等待审核。', 'success');
        // 在实际应用中，这里会发送 API 请求
        // 例如: axios.post('/api/apply-shop', formValues);
      }
    }
  }
};
</script>

<style scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css');

/* ----------------------- 顶部标题栏 ----------------------- */
.shop-management-page {
  font-family: Arial, sans-serif;
  background-color: #f8f8f8;
  padding-bottom: 20vw;
}
.header {
  width: 100%;
  height: 12vw;
  background-color: #007bff;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  position: sticky;
  top: 0;
  z-index: 100;
  display: flex;
  align-items: center;
  justify-content: center;
}
.header h1 {
  font-size: 5vw;
  color: #fff;
  margin: 0;
}

/* ----------------------- 店铺列表 ----------------------- */
.container {
  max-width: 600px;
  margin: 0 auto;
  padding: 0 4vw;
}
.wrapper .business-list {
  width: 100%;
  padding: 0;
  margin: 0;
  list-style: none;
}
.wrapper .business-list li {
  padding: 3vw;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.wrapper .business-list li:hover {
  background-color: #f9f9f9;
}
.wrapper .business-list li .business-info {
  display: flex;
  align-items: flex-start;
}
.wrapper .business-list li .business-info img {
  width: 20vw;
  height: 20vw;
  object-fit: cover;
  border-radius: 4px;
}
.wrapper .business-list li .business-info .business-info-detail {
  flex: 1;
  margin-left: 3vw;
}
.wrapper .business-list li .business-info .business-info-detail h3 {
  font-size: 4vw;
  margin: 0 0 2vw 0;
  color: #333;
}
.wrapper .business-list li .business-info .business-info-delivery {
  display: flex;
  gap: 2vw;
  font-size: 3vw;
  color: #666;
  margin: 0;
}
.wrapper .business-list li .business-info-rating,
.wrapper .business-list li .business-info-promotion {
  display: none;
}
.action-buttons {
  display: flex;
  gap: 2vw;
}
.action-buttons button {
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 10px;
  padding: 1.5vw 3vw;
  cursor: pointer;
  font-size: 3vw;
  transition: background-color 0.3s, color 0.3s;
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
  bottom: 12vw;
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
  background-color: #007bff;
  color: #fff;
  padding: 4vw 0;
  border-radius: 10px;
  text-decoration: none;
  font-size: 4vw;
  font-weight: bold;
  box-shadow: 0 2px 8px rgba(0,0,0,0.2);
  border: none;
  cursor: pointer;
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
  font-size: 3vw;
  flex-grow: 1;
  text-align: center;
}
.footer-nav .nav-item i {
  font-size: 5vw;
  margin-bottom: 1vw;
}
.footer-nav .nav-item.active {
  color: #007bff;
}
</style>