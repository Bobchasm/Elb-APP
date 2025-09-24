<template>
	<div class="wrapper">
		<!-- header部分 -->
		<BackButton />
    <div class="header">
    
      <h1 class="title">订单配送地址</h1>
    </div>
		<!-- 地址列表部分 -->
		<ul class="addresslist">
			<li v-for="item in deliveryAddressArr" :key="item.id">
				<div class="addresslist-left" @click="setDeliveryAddress(item)">
					<h3>{{ item.contactName }}{{ sexFilter(item.contactSex) }} {{ item.contactTel }}</h3>
					<p>{{ item.address }}</p>
				</div>
				<div class="addresslist-right">
					<i class="fa fa-edit" @click="editUserAddress(item.id)"></i>
					<i class="fa fa-remove" @click="removeUserAddress(item.id)"></i>
					<button class="select-btn" :class="{ 'selected': addressSelectedId === item.id }"
						@click="selectUserAddress(item.id)" :disabled="addressSelectedId === item.id">
						{{ addressSelectedId === item.id ? '已选' : '使用' }}
					</button>
				</div>
			</li>
		</ul>


		<!-- 新增地址部分 -->
		<div class="addbtn" @click="toAddUserAddress">
			<i class="fa fa-plus-circle"></i>
			<p>新增收货地址</p>
		</div>

		<!-- 底部结算栏 -->
		<div class="order-bar">
			<button class="checkout-order-btn" @click="submitOrder">确认下单</button>
		</div>

	</div>
</template>

<script>
import { ref, reactive, onMounted } from 'vue';
import Footer from '../components/Footer.vue';
import { useRoute, useRouter } from 'vue-router';
import request from '../utils/request';
import BackButton from '@/components/BackButton.vue';
export default {
	name: 'UserAddress',
	components: {
		BackButton
		},
	setup() {

		const user = reactive({});
		const deliveryAddressArr = ref([]);
		const route = useRoute();
		const router = useRouter();
		const businessId = ref(route.query.businessId);
		const orderId = ref();
		const addressSelectedId = ref(0);

		
		const goBack = () => {
      router.back();
    };
		onMounted(() => {
			const userFromLocal = localStorage.getItem('userInfo') ? JSON.parse(localStorage.getItem('userInfo')) : null;
			const userFromSession = sessionStorage.getItem('userInfo') ? JSON.parse(sessionStorage.getItem('userInfo')) : null;
			user.value = userFromLocal || userFromSession;
			businessId.value = route.query.businessId;
			listDeliveryAddressByUserId();
		});

		const listDeliveryAddressByUserId = () => {
			// 查询送货地址
			request.get('/api/addresses/listDeliveryAddressByUserId', {
				params: { userId: user.value.id }
			}).then(response => {
				deliveryAddressArr.value = response.data;
			}).catch(error => {
				console.error('获取送货地址列表失败:', error);
				// 可以添加错误提示，例如：
				// toast.error('获取送货地址失败');
			});
		};

		const setDeliveryAddress = (deliveryAddress) => {
			// 把用户选择的默认送货地址存储到localStorage中
			localStorage.setItem(user.value.id, JSON.stringify(deliveryAddress));
			router.push({ path: '/orders', query: { businessId: businessId.value } });
		};

		const toAddUserAddress = () => {
			router.push({ path: '/addUserAddress', query: { businessId: businessId.value } });
		};


		const selectUserAddress = (id) => {
			addressSelectedId.value = id;
		};

		const submitOrder = () => {
			if (addressSelectedId.value === 0) {
				alert('请选择配送地址');
				// uni.showToast({
				// 	title: '请选择配送地址',//提示内容
				// 	icon: 'none',//图标
				// 	duration: 2000//持续时间
				// });
				return;
			}
			else {
				request.get("/api/orders/submit?businessId=" + businessId.value + "&addressId=" + addressSelectedId.value)
					.then(response => {
						if (response.success) {
							orderId.value = response.data;
							router.push({ path: '/payment', query: { businessId: businessId.value, orderId: response.data } });
						} else {
							alert('下单失败！');
							router.push({ path: '/orderList' })
						}
					}).catch(error => {
						console.error('下单失败:', error);
					});
			}
		};

		const editUserAddress = (id) => {
			router.push({ path: '/editUserAddress', query: { businessId: businessId.value, id } });
		};

		const removeUserAddress = (id) => {
			if (!confirm('确认要删除此送货地址吗？')) {
				return;
			}

			request.put('/api/addresses/removeDeliveryAddress', {
				id: id
			}).then(response => {
				console.log(response.data);
				if (response.success) {
					let deliveryAddress = JSON.parse(localStorage.getItem(user.value.id.toString()));
					if (deliveryAddress && deliveryAddress.id === id) {
						localStorage.removeItem(user.value.id.toString());
					}
					listDeliveryAddressByUserId();
				} else {
					alert('删除地址失败！');
				}
			}).catch(error => {
				console.error(error);
			});
		};

		const sexFilter = (value) => value === 1 ? '先生' : '女士';

		return {
			businessId,
			user,
			deliveryAddressArr,
			listDeliveryAddressByUserId,
			setDeliveryAddress,
			toAddUserAddress,
			editUserAddress,
			removeUserAddress,
			selectUserAddress,
			sexFilter,
			orderId,
			addressSelectedId,
			submitOrder,
			goBack
		};
	},
	components: {
		Footer
	}
}
</script>

<style scoped>
/*************** 总容器 ***************/
.wrapper {
	width: 100%;
	height: 100%;
}

/*************** header ***************/
.wrapper header {
	width: 100%;
	height: 12vw;
	background-color: #0097FF;
	display: flex;
	justify-content: space-around;
	align-items: center;
	color: #fff;
	font-size: 4.8vw;
	position: fixed;
	left: 0;
	top: 0;
	/*保证在最上层*/
	z-index: 1000;
}
.wapper title {
 font-size: 1.1rem;
  color: #ffffff;
  font-weight: 600;
  margin: 0;
}
.back-icon {
  position: absolute;
  left: 15px; /* 调整左边距以更好地对齐 */
  font-size: 1.2rem;
  color: #ffffff;
  cursor: pointer;
  padding: 5px;
}

/*************** addresslist ***************/
.wrapper .addresslist {
	width: 100%;
	margin-top: 12vw;
	background-color: #fff;
}

.wrapper .addresslist li {
	width: 100%;
	box-sizing: border-box;
	border-bottom: solid 1px #DDD;
	padding: 3vw;
	display: flex;
}

.wrapper .addresslist li .addresslist-left {
	flex: 2.5;
	/*左边这块区域是可以点击的*/
	user-select: none;
	cursor: pointer;
}

.wrapper .addresslist li .addresslist-left h3 {
	font-size: 4.6vw;
	font-weight: 300;
	color: #666;
}

.wrapper .addresslist li .addresslist-left p {
	font-size: 4vw;
	color: #666;
}

.wrapper .addresslist li .addresslist-right {
	flex: 1;
	font-size: 5.6vw;
	color: #999;
	cursor: pointer;
	display: flex;
	justify-content: space-around;
	align-items: center;
}



/*************** 新增地址部分 ***************/
.wrapper .addbtn {
	width: 100%;
	height: 14vw;
	border-top: solid 1px #DDD;
	border-bottom: solid 1px #DDD;
	background-color: #fff;
	margin-top: 4vw;
	display: flex;
	justify-content: center;
	align-items: center;
	font-size: 4.5vw;
	color: #0097FF;
	user-select: none;
	cursor: pointer;
}

.wrapper .addbtn p {
	margin-left: 2vw;
}

/* .wrapper .addresslist .addresslist-right .fa-select {
	background-color: #0097ef;
	color: #fff;
	border: none;
	padding: 2vw 3.5vw;
	border-radius: 5px;
	font-size: 3vw;
	cursor: pointer;
	margin-left: 2vw;
	transition: background-color 0.3s;
} */

.wrapper .addresslist .addresslist-right .select-btn {
	background-color: #0097ef;
	color: #fff;
	border: none;
	padding: 2vw 3.5vw;
	border-radius: 5px;
	font-size: 3vw;
	cursor: pointer;
	margin-left: 2vw;
	transition: all 0.3s;
}

.wrapper .addresslist .addresslist-right .select-btn.selected {
	background-color: #f25858;
	color: #fcfafa;
	/* cursor: not-allowed; */
	border: none;
	padding: 2vw 3.5vw;
	border-radius: 5px;
	font-size: 3vw;
	cursor: pointer;
	margin-left: 2vw;
}

.wrapper .addresslist .addresslist-right .select-btn:not(.selected):hover {
	background-color: #0081e6;
}

.wrapper .addresslist .addresslist-right .select-btn:disabled {
	cursor: not-allowed;
	opacity: 0.8;
}


/*************** 底部结算栏 ***************/
.wrapper .order-bar {
	position: fixed;
	right: 10vw;
	bottom: 25vw;
	z-index: 1000;
}

.wrapper .order-bar .checkout-order-btn {
	background-color: #d91212;
	color: #fff;
	border: none;
	padding: 3vw 6vw;
	border-radius: 8vw;
	font-size: 4.5vw;
	font-weight: bold;
	cursor: pointer;
	box-shadow: 0 2vw 4vw rgba(0, 151, 255, 0.3);
	transition: all 0.3s ease;
	min-width: 25vw;
	text-align: center;
}

.wrapper .order-bar .checkout-order-btn:hover {
	background-color: #0081e6;
	transform: translateY(-0.5vw);
	box-shadow: 0 3vw 6vw rgba(0, 151, 255, 0.4);
}

.wrapper .order-bar .checkout-order-btn:active {
	transform: translateY(0);
	box-shadow: 0 1vw 2vw rgba(0, 151, 255, 0.3);
}
</style>