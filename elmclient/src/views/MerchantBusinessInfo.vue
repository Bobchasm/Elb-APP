<template>
  <div class="wrapper">
    <header>
      <div class="header-icon">
        <i class="fa fa-angle-left" @click="$router.back()"></i>
      </div>
      <p>商家信息</p>
      <div></div>
    </header>

    <div class="business-info-card">
      <div class="business-logo">
        <img :src="business.businessImg" />
      </div>

      <div class="info-details">
        <h1>{{ business.businessName }}</h1>
        <p class="price-info">
          <span class="info-item">起送价 &#165;{{ business.starPrice }}</span>
          <span class="info-item">配送费 &#165;{{ business.deliveryPrice }}</span>
        </p>
        <p class="explain-text">{{ business.businessExplain }}</p>
      </div>
    </div>

    <div class="likes-collections">
      <div class="icon-item">
        <i class="fa fa-thumbs-up"></i>
        <span>点赞: {{ business.likes }}</span>
      </div>
      <div class="icon-item">
        <i class="fa fa-bookmark"></i>
        <span>收藏: {{ business.collections }}</span>
      </div>
    </div>

    <div class="edit-button-container">
      <button class="edit-button" @click="showEditBusinessModal">编辑商家信息</button>
    </div>

    <ul class="food">
      <li v-for="(item, index) in foodArr" :key="item.foodId">
        <div class="food-left">
          <img :src="item.foodImg" />
          <div class="food-left-info">
            <h3>{{ item.foodName }}</h3>
            <p>{{ item.foodExplain }}</p>
            <p class="food-price">&#165;{{ item.foodPrice }}</p>
          </div>
        </div>
        <div class="food-right">
          <button class="action-button" @click="showEditFoodModal(index)">编辑</button>
          <button class="action-button delete-button" @click="deleteFood(index)">删除</button>
        </div>
      </li>
    </ul>

    <div class="footer-button-container">
      <button class="add-food-button" @click="showAddNewFoodModal">添加商品</button>
    </div>

  </div>
</template>

<script>
import { ref, reactive, onMounted } from "vue";
import axios from "axios";
import { useRoute } from "vue-router";
import Swal from 'sweetalert2';

export default {
  name: "BusinessInfo",
  setup() {
    const businessId = ref(null);
    const business = ref({});
    const foodArr = ref([]);
    const route = useRoute();

    // 模拟数据
    const mockBusinessData = {
      businessId: 1,
      businessName: '美味汉堡店',
      businessImg: 'https://i.ibb.co/L5Qy0fD/burger.jpg',
      starPrice: 15,
      deliveryPrice: 3,
      businessExplain: '我们的汉堡采用新鲜牛肉和时令蔬菜，手工制作，美味多汁！',
      likes: Math.floor(Math.random() * 1000),
      collections: Math.floor(Math.random() * 500)
    };
    const mockFoodData = [
      { foodId: 1, foodName: '经典牛肉汉堡', foodImg: 'https://i.ibb.co/L5Qy0fD/burger.jpg', foodExplain: '招牌主打，经典美味', foodPrice: 25, quantity: 0 },
      { foodId: 2, foodName: '薯条', foodImg: 'https://i.ibb.co/CByP6zP/fries.jpg', foodExplain: '酥脆可口，黄金薯条', foodPrice: 8, quantity: 0 },
      { foodId: 3, foodName: '可乐', foodImg: 'https://i.ibb.co/3W6qWbH/coke.jpg', foodExplain: '冰爽解渴，畅饮无限', foodPrice: 6, quantity: 0 }
    ];

    onMounted(() => {
      businessId.value = parseInt(route.query.businessId);
      // 在实际项目中，这里会发送 API 请求
      fetchBusinessAndFoodData();
    });

    const fetchBusinessAndFoodData = () => {
      // 模拟API请求，使用假数据
      business.value = mockBusinessData;
      foodArr.value = mockFoodData;
    };

    const showEditBusinessModal = async () => {
      const { value: formValues } = await Swal.fire({
        title: '编辑商铺信息',
        html:
          `<input id="swal-input-name" class="swal2-input" placeholder="商铺名称" value="${business.value.businessName}">` +
          `<input id="swal-input-img" class="swal2-input" placeholder="商铺图片URL" value="${business.value.businessImg}">` +
          `<input id="swal-input-star" type="number" class="swal2-input" placeholder="起送费" value="${business.value.starPrice}">` +
          `<input id="swal-input-delivery" type="number" class="swal2-input" placeholder="配送费" value="${business.value.deliveryPrice}">` +
          `<textarea id="swal-input-explain" class="swal2-textarea" placeholder="简介">${business.value.businessExplain}</textarea>`,
        focusConfirm: false,
        showCancelButton: true,
        confirmButtonText: '确认修改',
        cancelButtonText: '取消',
        confirmButtonColor: '#0097ef',
        cancelButtonColor: '#d33',
        preConfirm: () => {
          const name = document.getElementById('swal-input-name').value;
          const img = document.getElementById('swal-input-img').value;
          const star = parseFloat(document.getElementById('swal-input-star').value);
          const delivery = parseFloat(document.getElementById('swal-input-delivery').value);
          const explain = document.getElementById('swal-input-explain').value;

          if (!name || !img || isNaN(star) || isNaN(delivery)) {
            Swal.showValidationMessage('请填写完整且正确的信息');
            return false;
          }
          return { name, img, star, delivery, explain };
        }
      });

      if (formValues) {
        // 在实际项目中，这里会发送 API 请求更新数据
        console.log('更新商铺信息:', formValues);
        business.value = {
          ...business.value,
          businessName: formValues.name,
          businessImg: formValues.img,
          starPrice: formValues.star,
          deliveryPrice: formValues.delivery,
          businessExplain: formValues.explain
        };
        Swal.fire('修改成功', '商铺信息已更新。', 'success');
      }
    };

    const showAddNewFoodModal = async () => {
      const { value: formValues } = await Swal.fire({
        title: '添加新商品',
        html:
          `<input id="swal-food-name" class="swal2-input" placeholder="商品名称">` +
          `<input id="swal-food-img" class="swal2-input" placeholder="商品图片URL">` +
          `<input id="swal-food-explain" class="swal2-input" placeholder="商品简介">` +
          `<input id="swal-food-price" type="number" class="swal2-input" placeholder="商品价格">`,
        focusConfirm: false,
        showCancelButton: true,
        confirmButtonText: '确认添加',
        cancelButtonText: '取消',
        confirmButtonColor: '#0097ef', // 修改：将确认按钮颜色改为蓝色
        preConfirm: () => {
          const name = document.getElementById('swal-food-name').value;
          const img = document.getElementById('swal-food-img').value;
          const explain = document.getElementById('swal-food-explain').value;
          const price = parseFloat(document.getElementById('swal-food-price').value);

          if (!name || !img || !explain || isNaN(price)) {
            Swal.showValidationMessage('请填写完整且正确的信息');
            return false;
          }
          return { name, img, explain, price };
        }
      });

      if (formValues) {
        const newFood = {
          foodId: foodArr.value.length + 1, // 模拟生成一个ID
          foodName: formValues.name,
          foodImg: formValues.img,
          foodExplain: formValues.explain,
          foodPrice: formValues.price,
          quantity: 0
        };
        foodArr.value.push(newFood);
        Swal.fire('添加成功', '新商品已添加！', 'success');
      }
    };

    const showEditFoodModal = async (index) => {
      const foodItem = foodArr.value[index];
      const { value: formValues } = await Swal.fire({
        title: '编辑商品信息',
        html:
          `<input id="swal-food-name" class="swal2-input" placeholder="商品名称" value="${foodItem.foodName}">` +
          `<input id="swal-food-img" class="swal2-input" placeholder="商品图片URL" value="${foodItem.foodImg}">` +
          `<input id="swal-food-explain" class="swal2-input" placeholder="商品简介" value="${foodItem.foodExplain}">` +
          `<input id="swal-food-price" type="number" class="swal2-input" placeholder="商品价格" value="${foodItem.foodPrice}">`,
        focusConfirm: false,
        showCancelButton: true,
        confirmButtonText: '确认修改',
        cancelButtonText: '取消',
        confirmButtonColor: '#0097ef',
        preConfirm: () => {
          const name = document.getElementById('swal-food-name').value;
          const img = document.getElementById('swal-food-img').value;
          const explain = document.getElementById('swal-food-explain').value;
          const price = parseFloat(document.getElementById('swal-food-price').value);

          if (!name || !img || !explain || isNaN(price)) {
            Swal.showValidationMessage('请填写完整且正确的信息');
            return false;
          }
          return { name, img, explain, price };
        }
      });

      if (formValues) {
        // 更新商品数据
        foodArr.value[index] = {
          ...foodItem,
          foodName: formValues.name,
          foodImg: formValues.img,
          foodExplain: formValues.explain,
          foodPrice: formValues.price
        };
        Swal.fire('修改成功', '商品信息已更新。', 'success');
      }
    };

    const deleteFood = (index) => {
      Swal.fire({
        title: '确定要删除吗？',
        text: "删除后将无法恢复！",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#0097ef', // 修改：将确认按钮颜色改为蓝色
        cancelButtonColor: '#d33',
        confirmButtonText: '确定删除',
        cancelButtonText: '取消'
      }).then((result) => {
        if (result.isConfirmed) {
          foodArr.value.splice(index, 1);
          Swal.fire('删除成功', '商品已从列表中移除。', 'success');
        }
      });
    };

    return {
      business,
      foodArr,
      showEditBusinessModal,
      showAddNewFoodModal,
      showEditFoodModal,
      deleteFood
    };
  },
};
</script>

<style scoped>
/****************** 总容器 ******************/
.wrapper {
  width: 100%;
  min-height: 100vh;
  padding-bottom: 14vw;
  box-sizing: border-box;
  background-color: #f5f5f5;
}

/****************** header部分 ******************/
.wrapper header {
  width: 100%;
  height: 12vw;
  background-color: #0097ff;
  color: #fff;
  font-size: 4.8vw;
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 4vw;
  box-sizing: border-box;
}

.wrapper header .fa-angle-left {
  font-size: 7vw;
  cursor: pointer;
}

/****************** 商家信息卡片 ******************/
.business-info-card {
  margin-top: 15vw;
  padding: 4vw;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  display: flex;
  align-items: center;
  margin: 15vw 4vw 0;
}

.business-info-card .business-logo {
  width: 25vw;
  height: 25vw;
  border-radius: 5px;
  overflow: hidden;
  margin-right: 4vw;
}

.business-info-card .business-logo img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.business-info-card .info-details h1 {
  font-size: 5vw;
  margin-bottom: 1vw;
  color: #333;
}

.business-info-card .info-details .price-info {
  font-size: 3vw;
  color: #666;
  margin-top: 1vw;
}

.business-info-card .info-details .info-item {
  margin-right: 2vw;
}

.business-info-card .info-details .explain-text {
  font-size: 3.2vw;
  color: #888;
  margin-top: 2vw;
  line-height: 1.5;
}

/****************** 点赞和收藏部分 ******************/
.likes-collections {
  display: flex;
  justify-content: space-around;
  background-color: #fff;
  margin: 3vw 4vw;
  padding: 3vw 0;
  border-radius: 10px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  font-size: 3.5vw;
  color: #666;
}

.likes-collections .icon-item {
  display: flex;
  align-items: center;
}

.likes-collections .icon-item .fa {
  margin-right: 1.5vw;
  font-size: 4.5vw;
  color: #0097ef;
}

/* 编辑商家按钮 */
.edit-button-container {
  display: flex;
  justify-content: center;
  margin-top: 4vw;
}

.edit-button {
  background-color: #007bff;
  color: #fff;
  border: none;
  padding: 2.5vw 5vw;
  border-radius: 5px;
  font-size: 3.5vw;
  cursor: pointer;
  transition: background-color 0.3s;
}

.edit-button:hover {
  background-color: #0056b3;
}

/****************** 食品列表部分 ******************/
.wrapper .food {
  width: 100%;
  margin-bottom: 14vw;
  margin-top: 4vw;
}

.wrapper .food li {
  width: 100%;
  box-sizing: border-box;
  padding: 4vw;
  user-select: none;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background-color: #fff;
  border-bottom: 1px solid #eee;
}

.wrapper .food li .food-left {
  display: flex;
  align-items: center;
}

.wrapper .food li .food-left img {
  width: 18vw;
  height: 18vw;
  border-radius: 5px;
  object-fit: cover;
}

.wrapper .food li .food-left .food-left-info {
  margin-left: 3vw;
}

.wrapper .food li .food-left .food-left-info h3 {
  font-size: 4vw;
  color: #555;
  margin-bottom: 1vw;
}

.wrapper .food li .food-left .food-left-info p {
  font-size: 3vw;
  color: #888;
  margin-top: 1vw;
}

.wrapper .food li .food-left .food-left-info .food-price {
  font-size: 3.8vw;
  color: #ff5722;
  font-weight: bold;
  margin-top: 2vw;
}

.wrapper .food li .food-right {
  display: flex;
  align-items: center;
}

.wrapper .food li .food-right .action-button {
  background-color: #0097ef;
  color: #fff;
  border: none;
  padding: 2vw 3.5vw;
  border-radius: 5px;
  font-size: 3vw;
  cursor: pointer;
  margin-left: 2vw;
  transition: background-color 0.3s;
}

.wrapper .food li .food-right .action-button:hover {
  background-color: #007bb6;
}

.wrapper .food li .food-right .delete-button {
  background-color: #e74c3c;
}

.wrapper .food li .food-right .delete-button:hover {
  background-color: #c0392b;
}

/* 底部添加商品按钮 */
.footer-button-container {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 14vw;
  background-color: #fff;
  border-top: 1px solid #f0f0f0;
  z-index: 100;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

.add-food-button {
  width: 90%;
  background-color: #0097ef;
  color: #fff;
  padding: 3.5vw 0;
  border-radius: 8vw;
  font-size: 4.5vw;
  font-weight: bold;
  border: none;
  cursor: pointer;
  transition: background-color 0.3s;
}

.add-food-button:hover {
  background-color: #007bb6;
}
</style>