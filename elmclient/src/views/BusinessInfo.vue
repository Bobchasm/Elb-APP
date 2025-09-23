<template>
    <div class="wrapper">
        <!-- header部分 -->
        <!-- 首页点进去后展示的内容 -->
        <BackButton />
        <header>
            <p>商家信息</p>
        </header>
        <!-- 商家logo部分 -->
        <div class="business-logo">
            <img :src="business.businessImg || require('@/assets/default-business.png')" />
        </div>

        <!-- 商家信息部分 -->
        <div class="business-info">
            <h1>{{ business.businessName }}</h1>
            <p>
                &#165;{{ business.startPrice }}起送 &#165;{{
                    business.deliveryPrice
                }}配送
            </p>
            <p>{{ business.businessExplain }}</p>
            <div class="reactions">
                <div class="reaction" @click.stop="toggleLike"
                    :class="{ 'active': isLiked, 'disabled': interactionLoading }" :title="isLiked ? '已点赞' : '点赞'">
                    <i class="fa fa-thumbs-up"
                        :style="isLiked ? 'color:#e74c3c' : interactionLoading ? 'color:#ddd' : 'color:#bbb'"></i>
                    <span v-if="interactionLoading" class="loading-dots">...</span>
                </div>
                <div class="reaction" @click.stop="toggleFavorite"
                    :class="{ 'active': isFavorited, 'disabled': interactionLoading }"
                    :title="isFavorited ? '已收藏' : '收藏'">
                    <i class="fa fa-star"
                        :style="isFavorited ? 'color:#e74c3c' : interactionLoading ? 'color:#ddd' : 'color:#bbb'"></i>
                    <span v-if="interactionLoading" class="loading-dots">...</span>
                </div>
            </div>
        </div>

        <!-- 食品列表部分 -->
        <ul class="food">
            <li v-for="(item, index) in foodArr" :key="item.foodId">
                <div class="food-left">
                    <img :src="item.foodImg || require('@/assets/default-business.png')" />
                    <div class="food-left-info">
                        <h3>{{ item.foodName }}</h3>
                        <p>{{ item.foodExplain }}</p>
                        <p>&#165;{{ item.foodPrice }}</p>
                    </div>
                </div>
                <div class="food-right">
                    <div>
                        <i class="fa fa-minus-circle" @click="minus(index)" v-show="item.quantity != 0"></i>
                    </div>
                    <p>
                        <span v-show="item.quantity != 0">{{ item.quantity }}</span>
                    </p>
                    <div>
                        <i class="fa fa-plus-circle" @click="add(index)"></i>
                    </div>
                </div>
            </li>
        </ul>

        <!-- 购物车部分 -->
        <div class="cart">
            <div class="cart-left">
                <div class="cart-left-icon" :style="totalQuantity == 0
                    ? 'background-color:#505051;'
                    : 'background-color:#3190E8;'
                    " @click="goToCart(businessId.value)">
                    <i class="fa fa-shopping-cart"></i>
                    <div class="cart-left-icon-quantity" v-show="totalQuantity != 0">
                        {{ totalQuantity }}
                    </div>
                </div>
                <div class="cart-left-info">
                    <p>&#165;{{ totalPrice.toFixed(2) }}</p>
                    <p>另需配送费{{ business.deliveryPrice }}元</p>
                </div>
            </div>
            <div class="cart-right">
                <!-- 不够起送费 -->
                <div class="cart-right-item" v-show="totalSettle - business.deliveryPrice < business.starPrice"
                    style="background-color: #535356; cursor: default">
                    &#165;{{ business.starPrice }}起送
                </div>
                <!-- 达到起送费 -->

                <div class="cart-right-item" @click="toOrder">
                    去结算
                </div>
            </div>
        </div>
    </div>
</template>


<script>
import { ref, onMounted, computed, watch, onErrorCaptured } from "vue";
import { useRoute, useRouter } from "vue-router";
import request from "@/utils/request";
import BackButton from "../components/BackButton.vue";

export default {
    name: "BusinessInfo",
    components: { BackButton },
    setup() {
        const route = useRoute();
        const router = useRouter();
        const userInfo = ref(null);
        console.log("BusinessInfo组件初始化，路由参数:", route.query);

        // 基础数据
        const businessId = ref(null);
        const business = ref({
            id: 0,
            businessName: "",
            businessImg: "",
            startPrice: 0,
            deliveryPrice: 0,
            businessExplain: "",
            businessAddress: "",
            orderTypeId: 0,
            remarks: ""
        });
        const foodArr = ref([]);
        const loadingBusiness = ref(false);
        const loadingFoods = ref(false);

        // 用户交互状态
        const isLiked = ref(false);
        const isFavorited = ref(false);
        const interactionLoading = ref(false);

        // 本地购物车实现
        const localCart = ref({});

        const fetchUserInfo = async () => {
            const token = localStorage.getItem('token') || sessionStorage.getItem('token');
            if (!token) return;

            try {
                const res = await request.get('/api/user');
                if (res) {
                    userInfo.value = res;
                    console.log(userInfo.value);
                    // 保存用户信息到存储
                    const storage = localStorage.getItem('token') ? localStorage : sessionStorage;
                    storage.setItem('userInfo', JSON.stringify(res));
                } else {
                    console.error('获取用户信息失败');
                    userInfo.value = null;
                }
            } catch (error) {
                console.error('获取用户信息异常:', error);
                userInfo.value = null;
            }
        };

        // 错误捕获
        onErrorCaptured((error) => {
            console.error('组件错误捕获:', error);
            return false;
        });

        // 初始化本地购物车
        const initLocalCart = () => {
            const cartData = localStorage.getItem(`cart_${businessId.value}`);
            console.log(`初始化购物车，businessId: ${businessId.value}, 本地存储数据:`, cartData);
            if (cartData) {
                try {
                    localCart.value = JSON.parse(cartData);
                    console.log("解析后的购物车数据:", localCart.value);
                } catch (e) {
                    console.error("解析购物车数据失败:", e);
                    localCart.value = {};
                }
            } else {
                console.log("未找到本地购物车数据");
            }
        };

        // 保存购物车到本地存储
        const saveCartToLocal = () => {
            console.log("保存购物车到本地:", localCart.value);
            localStorage.setItem(`cart_${businessId.value}`, JSON.stringify(localCart.value));
        };

        // 加载用户互动状态（使用新的查询接口）
        const loadReactions = async () => {
    try {
        // 确保有有效的businessId
        if (!businessId.value) {
            console.error("缺少businessId");
            return;
        }

        // 等待用户信息加载（最多等待2秒）
        let retry = 0;
        while (!userInfo.value?.id && retry < 4) {
            await new Promise(resolve => setTimeout(resolve, 500));
            retry++;
        }

        const userId = userInfo.value?.id;
        if (!userId) {
            console.log("用户未登录，不加载互动状态");
            isLiked.value = false;
            isFavorited.value = false;
            return;
        }

        console.log(`加载互动状态，userId: ${userId}, merchantId: ${businessId.value}`);
        
        const response = await request.get('/api/merchant/interaction/status', {
            params: { userId, merchantId: businessId.value },
            headers: { 'Cache-Control': 'no-cache' } // 防止缓存
        });

        console.log("互动状态API响应:", response);

        // 根据实际API响应结构调整
        if (response?.success) {
            isLiked.value = Boolean(response.data?.liked);
            isFavorited.value = Boolean(response.data?.collected);
            console.log(`设置互动状态 - 点赞: ${isLiked.value}, 收藏: ${isFavorited.value}`);
        } else {
            console.error("API返回失败:", response?.message);
            isLiked.value = false;
            isFavorited.value = false;
        }
    } catch (error) {
        console.error("加载互动状态异常:", error);
        isLiked.value = false;
        isFavorited.value = false;
    }
};
        // 更新互动状态到后端
        const updateInteraction = async (type, newValue) => {
            try {
                // 如果已经是目标状态，则不再执行
                if ((type === 'like' && isLiked.value === newValue) ||
                    (type === 'favorite' && isFavorited.value === newValue)) {
                    console.log(`已经是目标状态，无需更新: ${type}=${newValue}`);
                    return;
                }

                interactionLoading.value = true;
                const userId = userInfo.value?.id;
                if (!userId) {
                    alert('请先登录');
                    return;
                }

                const dto = {
                    userId,
                    merchantId: businessId.value,
                    liked: type === 'like' ? newValue : isLiked.value,
                    collected: type === 'favorite' ? newValue : isFavorited.value
                };

                console.log(`更新互动状态:`, dto);

                const response = await request.post('/api/merchant/interaction/update', dto);

                if (response.success) {
                    if (type === 'like') {
                        isLiked.value = newValue;
                    } else {
                        isFavorited.value = newValue;
                    }
                    console.log(`${type}状态更新成功: ${newValue}`);
                } else {
                    console.error(`${type}状态更新失败:`, response.message);
                    alert('操作失败，请重试');
                }
            } catch (error) {
                console.error(`${type}状态更新异常:`, error);
                alert('操作异常，请检查网络');
            } finally {
                interactionLoading.value = false;
            }
        };


        // 修改切换函数，增加状态检查
        const toggleLike = async () => {
            if (interactionLoading.value) return;
            if (!userInfo.value?.id) {
                alert('请先登录');
                return;
            }
            await updateInteraction('like', !isLiked.value);
        };

        const toggleFavorite = async () => {
            if (interactionLoading.value) return;
            if (!userInfo.value?.id) {
                alert('请先登录');
                return;
            }
            await updateInteraction('favorite', !isFavorited.value);
        };



        // 获取商家信息
        const fetchBusinessInfo = async () => {
            loadingBusiness.value = true;
            console.log(`开始获取商家信息，businessId: ${businessId.value}`);

            try {
                const response = await request.get(`/api/businesses/${businessId.value}`);
                console.log("商家信息API完整响应:", response);

                if (response.success === true) {
                    console.log("API请求成功，开始处理数据");
                    business.value = {
                        id: response.data.id,
                        businessName: response.data.businessName,
                        businessImg: response.data.businessImg,
                        startPrice: response.data.startPrice,
                        deliveryPrice: response.data.deliveryPrice,
                        businessExplain: response.data.businessExplain,
                        businessAddress: response.data.businessAddress,
                        orderTypeId: response.data.orderTypeId,
                        remarks: response.data.remarks
                    };
                    console.log("商家信息设置成功:", business.value);
                } else {
                    console.log("API请求失败，进入else分支");
                    const errorMsg = response.message || "获取商家信息失败";
                    console.error("商家信息API返回失败:", errorMsg);
                    throw new Error(errorMsg);
                }
            } catch (error) {
                console.error("获取商家信息失败:", error);
            } finally {
                loadingBusiness.value = false;
            }
        };

        // 获取食品列表
        const fetchFoodList = async () => {
            loadingFoods.value = true;
            console.log(`开始获取食品列表，businessId: ${businessId.value}`);

            try {
                const response = await request.get("/api/foods/list", {
                    params: { businessId: businessId.value }
                });
                console.log("食品列表API响应:", response);

                if (response.success) {
                    // 过滤掉下架商品（shelveStatus === 0）
                    const availableFoods = response.data.filter(food => food.shelveStatus === 1);
                    console.log("可用食品列表:", availableFoods);

                    foodArr.value = availableFoods.map(item => ({
                        id: item.id,
                        foodId: item.id,
                        foodName: item.foodName,
                        foodPrice: item.foodPrice,
                        foodExplain: item.foodExplain,
                        foodImg: item.foodImg,
                        remarks: item.remarks,
                        businessId: item.businessId,
                        businessName: item.businessName,
                        quantity: localCart.value[item.id] || 0
                    }));
                    console.log("食品列表设置成功:", foodArr.value);
                } else {
                    const errorMsg = response.message || "获取食品列表失败";
                    console.error("食品列表API返回失败:", errorMsg);
                    throw new Error(errorMsg);
                }
            } catch (error) {
                console.error("获取食品列表失败:", error);
                console.error("错误详情:", error.response || error.message);

                // 开发环境使用模拟数据
                if (process.env.NODE_ENV === "development") {
                    console.log("使用模拟食品数据");
                    foodArr.value = [
                        {
                            id: 1,
                            foodId: 1,
                            foodName: "模拟食品1",
                            foodPrice: 15,
                            foodExplain: "模拟食品描述",
                            foodImg: require('@/assets/default-food.png'),
                            remarks: "模拟备注",
                            businessId: businessId.value,
                            businessName: "模拟商家",
                            quantity: 0
                        }
                    ];
                }
            } finally {
                loadingFoods.value = false;
                console.log("食品列表加载完成");
            }
        };

        // 购物车操作
        const addToCart = (index) => {
            const food = foodArr.value[index];
            console.log(`添加商品到购物车: ${food.foodName}, 当前数量: ${food.quantity}`);

            if (!localCart.value[food.id]) {
                localCart.value[food.id] = 0;
            }
            localCart.value[food.id]++;
            food.quantity = localCart.value[food.id];
            saveCartToLocal();

            console.log(`添加后购物车状态:`, localCart.value);
        };

        const removeFromCart = (index) => {
            const food = foodArr.value[index];
            console.log(`从购物车移除商品: ${food.foodName}, 当前数量: ${food.quantity}`);

            if (localCart.value[food.id] <= 1) {
                delete localCart.value[food.id];
                food.quantity = 0;
            } else {
                localCart.value[food.id]--;
                food.quantity = localCart.value[food.id];
            }
            saveCartToLocal();

            console.log(`移除后购物车状态:`, localCart.value);
        };

        // 跳转到订单页面
        const toOrder = () => {
            console.log("跳转到订单页面，当前购物车:", localCart.value);
            if (totalQuantity.value === 0) {
                alert("请先添加商品到购物车");
                return;
            }
            router.push({
                path: "/cart",
                query: {
                    businessId: businessId.value,
                    //cart: JSON.stringify(localCart.value)
                }
            });
        };

        // 跳转到购物车页面
        const goToCart = () => {
            console.log("跳转到购物车页面，当前购物车:", localCart.value);
            if (totalQuantity.value === 0) {
                alert("购物车为空");
                return;
            }
            router.push({
                path: "/cart",
                query: {
                    businessId: businessId.value,
                    cart: JSON.stringify(localCart.value)
                }
            });
        };

        // 计算属性
        const totalPrice = computed(() => {
            const total = foodArr.value.reduce((total, item) => {
                return total + (item.foodPrice || 0) * (item.quantity || 0);
            }, 0);
            console.log(`计算总价: ${total}`);
            return total;
        });

        const totalQuantity = computed(() => {
            const quantity = Object.values(localCart.value).reduce((sum, qty) => sum + qty, 0);
            console.log(`计算总数量: ${quantity}`);
            return quantity;
        });

        const totalSettle = computed(() => {
            const settle = totalPrice.value + (business.value.deliveryPrice || 0);
            console.log(`计算结算总额: ${settle}`);
            return settle;
        });

        // 检查是否达到起送费
        const canOrder = computed(() => {
            const canOrder = totalPrice.value >= business.value.startPrice;
            console.log(`检查是否可下单: ${canOrder}`);
            return canOrder;
        });

        // 初始化
    
// 修改 onMounted 部分
onMounted(async () => {
    console.log("组件挂载完成");
    businessId.value = parseInt(route.query.businessId);
    
    if (!businessId.value) {
        console.error("无效的商家ID:", route.query.businessId);
        router.push("/");
        return;
    }

    // 先加载用户信息
    await fetchUserInfo();
    
    // 然后加载其他数据
    initLocalCart();
    await fetchBusinessInfo();
    await fetchFoodList();
    
    // 最后加载互动状态（确保有用户ID）
    await loadReactions();
});
        // 监听businessId变化
        watch(() => route.query.businessId, (newId) => {
            console.log("路由businessId变化:", newId);
            if (newId && parseInt(newId) !== businessId.value) {
                businessId.value = parseInt(newId);
                console.log("新的businessId:", businessId.value);
                fetchUserInfo();
                initLocalCart();
                fetchBusinessInfo();
                fetchFoodList();
                loadReactions();
            }
        });

        return {
            business,
            foodArr,
            loadingBusiness,
            loadingFoods,
            totalPrice,
            totalQuantity,
            totalSettle,
            canOrder,
            isLiked,
            isFavorited,
            add: addToCart,
            minus: removeFromCart,
            toOrder,
            goToCart,
            toggleLike,
            toggleFavorite,
            interactionLoading
        };
    }
};
</script>



<style scoped>
/****************** 总容器 ******************/
.wrapper {
	width: 100%;
	height: 100%;
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
	justify-content: center;
	align-items: center;
}

/****************** 商家logo部分 ******************/
.wrapper .business-logo {
	width: 100%;
	height: 50vw;
	/*使用上外边距避开header部分*/
	margin-top: 12vw;
	display: flex;
	justify-content: center;
	align-items: center;
}

.wrapper .business-logo img {
	width: 40vw;
	height: 40vw;
	border-radius: 5px;
}

/****************** 商家信息部分 ******************/
.wrapper .business-info {
	width: 100%;
	height: 20vw;
	display: flex;
	flex-direction: column;
	justify-content: center;
	align-items: center;
	position: relative;
}

.wrapper .business-info h1 {
	font-size: 5vw;
}

.wrapper .business-info .reactions {
	position: absolute;
	right: 3vw;
	bottom: -2vw;
	display: flex;
	gap: 4vw;
}

.wrapper .business-info .reactions .reaction {
	display: flex;
	align-items: center;
	gap: 1vw;
	cursor: pointer;
	user-select: none;
}

.wrapper .business-info .reactions .reaction i {
	font-size: 5vw;
	color: #bbb;
}

.wrapper .business-info p {
	font-size: 3vw;
	color: #666;
	margin-top: 1vw;
}

/****************** 食品列表部分 ******************/
.wrapper .food {
	width: 100%;
	/*使用下外边距避开footer部分*/
	margin-bottom: 14vw;
}

.wrapper .food li {
	width: 100%;
	box-sizing: border-box;
	padding: 2.5vw;
	user-select: none;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.wrapper .food li .food-left {
	display: flex;
	align-items: center;
}

.wrapper .food li .food-left img {
	width: 20vw;
	height: 20vw;
}

.wrapper .food li .food-left .food-left-info {
	margin-left: 3vw;
}

.wrapper .food li .food-left .food-left-info h3 {
	font-size: 3.8vw;
	color: #555;
}

.wrapper .food li .food-left .food-left-info p {
	font-size: 3vw;
	color: #888;
	margin-top: 2vw;
}

.wrapper .food li .food-right {
	width: 16vw;
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.wrapper .food li .food-right .fa-minus-circle {
	font-size: 5.5vw;
	color: #999;
	cursor: pointer;
}

.wrapper .food li .food-right p {
	font-size: 3.6vw;
	color: #333;
}

.wrapper .food li .food-right .fa-plus-circle {
	font-size: 5.5vw;
	color: #0097ef;
	cursor: pointer;
}

/****************** 购物车部分 ******************/
.wrapper .cart {
	width: 100%;
	height: 14vw;
	position: fixed;
	left: 0;
	bottom: 0;
	display: flex;
}

.wrapper .cart .cart-left {
	flex: 2;
	background-color: #505051;
	display: flex;
}

.wrapper .cart .cart-left .cart-left-icon {
	width: 16vw;
	height: 16vw;
	box-sizing: border-box;
	border: solid 1.6vw #444;
	border-radius: 8vw;
	background-color: #3190e8;
	font-size: 7vw;
	color: #fff;
	display: flex;
	justify-content: center;
	align-items: center;
	margin-top: -4vw;
	margin-left: 3vw;
	position: relative;
}

.wrapper .cart .cart-left .cart-left-icon-quantity {
	width: 5vw;
	height: 5vw;
	border-radius: 2.5vw;
	background-color: red;
	color: #fff;
	font-size: 3.6vw;
	display: flex;
	justify-content: center;
	align-items: center;
	position: absolute;
	right: -1.5vw;
	top: -1.5vw;
}

.wrapper .cart .cart-left .cart-left-info p:first-child {
	font-size: 4.5vw;
	color: #fff;
	margin-top: 1vw;
}

.wrapper .cart .cart-left .cart-left-info p:last-child {
	font-size: 2.8vw;
	color: #aaa;
}

.wrapper .cart .cart-right {
	flex: 1;
}

/*达到起送费时的样式*/
.wrapper .cart .cart-right .cart-right-item {
	width: 100%;
	height: 100%;
	background-color: #38ca73;
	color: #fff;
	font-size: 4.5vw;
	font-weight: 700;
	user-select: none;
	cursor: pointer;
	display: flex;
	justify-content: center;
	align-items: center;
}
</style>


