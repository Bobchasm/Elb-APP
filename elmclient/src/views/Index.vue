<template>
    <!-- 登录、注册部分 -->
    <div class="wrapper">
        <!-- header部分 -->
        <header>
            <div class="icon-location-box">
                <i class="fas fa-map-marker-alt"></i>
            </div>
            <!-- <div class="location-text">天津大学北洋园校区<i class="fa fa-caret-down"></i></div> -->
            <div class="location-text" @click="showLocationPicker">
                <span class="location-display">{{ displayLocation }}</span>
                <i class="fa fa-caret-down"></i>
            </div>

            <!-- 漂亮的位置选择弹窗 -->
            <transition name="fade">
                <div v-if="showPicker" class="location-modal" @click.self="hideLocationPicker">
                    <div class="modal-container">
                        <div class="modal-header">
                            <h3>选择位置</h3>
                            <button class="close-btn" @click="hideLocationPicker">
                                <i class="fa fa-times"></i>
                            </button>
                        </div>
                        
                        <div class="modal-content">
                            <!-- 位置层级导航 -->
                            <div class="location-nav">
                                <div 
                                    v-for="(level, index) in locationLevels" 
                                    :key="index"
                                    :class="['nav-item', { active: currentLevel === index, disabled: index > currentLevel }]"
                                    @click="switchLevel(index)"
                                >
                                    {{ level }}
                                </div>
                            </div>

                            <!-- 位置列表 -->
                            <div class="location-list-container">
                                <div v-if="loading" class="loading-state">
                                    <i class="fa fa-spinner fa-spin"></i>
                                    <span>加载中...</span>
                                </div>
                                
                                <div v-else-if="locationData.length === 0" class="empty-state">
                                    <i class="fa fa-map-marker"></i>
                                    <span>暂无数据</span>
                                </div>
                                
                                <div v-else class="location-items">
                                    <div 
                                        v-for="item in locationData" 
                                        :key="item.id"
                                        :class="['location-item', { selected: isSelected(item) }]"
                                        @click="selectLocation(item)"
                                    >
                                        <span class="item-name">{{ item.name }}</span>
                                        <i v-if="isSelected(item)" class="fa fa-check selected-icon"></i>
                                    </div>
                                </div>
                            </div>

                            <!-- 当前选择显示 -->
                            <div v-if="selectedLocation.province" class="current-selection">
                                  <span>已选择：</span>
                                  <span class="selection-text">
                                     {{ getDisplayText(selectedLocation) }}
                                  </span>
                            </div>
                        </div>

                        <div class="modal-footer">
                            <button class="btn-cancel" @click="hideLocationPicker">取消</button>
                            <button class="btn-confirm" @click="confirmLocation">确认</button>
                        </div>
                    </div>
                </div>
            </transition>

            <div class="login-register">
                <template v-if="!userInfo">
                    <button @click="goToLChoose">登录</button>
                    <button @click="goToRChoose">注册</button>
                </template>
                <template v-else>
                    <div class="user-info">
                        <p>{{ userInfo.username }} ，您好！</p>
                    </div>
                </template>
            </div>
        </header>
        <!-- search部分 -->
        <div class="search">
            <div class="search-fixed-top" ref="fixedBox">
                <div class="search-box">
                    <i class="fa fa-search"></i>
                    <input v-model="searchKeyword" type="text" placeholder="搜索饿了么商家、商品名称" @keyup.enter="performSearch" />
                    <button @click="performSearch" class="search-btn">搜索</button>
                </div>
            </div>
        </div>

        
        <!-- 点餐分类部分 -->
        <ul class="foodtype">
            <li @click="toBusinessList(1)">
                <img src="@/assets/dcfl01.png" alt="美食">
                <p>美食</p>
            </li>
            <li @click="toBusinessList(2)">
                <img src="@/assets/dcfl02.png" alt="早餐">
                <p>早餐</p>
            </li>
            <li @click="toBusinessList(3)">
                <img src="@/assets/dcfl03.png" alt="跑腿代购">
                <p>跑腿代购</p>
            </li>
            <li @click="toBusinessList(4)">
                <img src="@/assets/dcfl04.png" alt="汉堡披萨">
                <p>汉堡披萨</p>
            </li>
            <li @click="toBusinessList(5)">
                <img src="@/assets/dcfl05.png" alt="甜品饮品">
                <p>甜品饮品</p>
            </li>
            <li @click="toBusinessList(6)">
                <img src="@/assets/dcfl06.png" alt="速食简餐">
                <p>速食简餐</p>
            </li>
            <li @click="toBusinessList(7)">
                <img src="@/assets/dcfl07.png" alt="地方小吃">
                <p>地方小吃</p>
            </li>
            <li @click="toBusinessList(8)">
                <img src="@/assets/dcfl08.png" alt="米粉面馆">
                <p>米粉面馆</p>
            </li>
            <li @click="toBusinessList(9)">
                <img src="@/assets/dcfl09.png" alt="包子粥铺">
                <p>包子粥铺</p>
            </li>
            <li @click="toBusinessList(10)">
                <img src="@/assets/dcfl10.png" alt="炸鸡炸串">
                <p>炸鸡炸串</p>
            </li>
        </ul>

        <!-- 横幅广告部分（注意：此处有背景图片） -->
        <div class="banner">
            <h3>品质套餐</h3>
            <p>搭配齐全吃得好</p>
            <a>立即抢购 &gt;</a>
        </div>

        <!-- 超级会员部分 -->
        <div class="supermember">
            <div class="left">
                <img src="@/assets/super_member.png" alt="超级会员">
                <h3>超级会员</h3>
                <p>&#8226; 每月享超值权益</p>
            </div>
            <div class="right">
                立即开通 &gt;
            </div>
        </div>

        <!-- 推荐商家部分 -->
        <div class="recommend">
            <div class="recommend-line"></div>
            <p>推荐商家</p>
            <div class="recommend-line"></div>
        </div>

        <!-- 推荐方式部分 -->
        <ul class="recommendtype">
            <li 
                :class="{ active: sortBy === 'default' }" 
                @click="setSortBy('default')"
            >
                综合排序<i class="fa fa-caret-down"></i>
            </li>
            <!-- <li 
                :class="{ active: sortBy === 'distance' }" 
                @click="setSortBy('distance')"
            >
                距离最近
            </li> -->
            <li 
                :class="{ active: sortBy === 'sales' }" 
                @click="setSortBy('sales')"
            >
                销量最高
            </li>
            <li 
                :class="{ active: showFilter }" 
                @click="toggleFilter"
            >
                筛选<i class="fa fa-filter"></i>
            </li>
        </ul>

        <!-- 筛选弹窗 -->
        <transition name="fade">
            <div v-if="showFilter" class="filter-modal" @click.self="hideFilter">
                <div class="filter-container">
                    <div class="filter-header">
                        <h3>筛选条件</h3>
                        <button class="close-btn" @click="hideFilter">
                            <i class="fa fa-times"></i>
                        </button>
                    </div>
                    
                    <div class="filter-content">
                        <!-- 免配送费筛选 -->
                        <div class="filter-section">
                            <h4>配送费</h4>
                            <label class="filter-option">
                                <input 
                                    type="checkbox" 
                                    v-model="filters.freeDelivery"
                                    @change="applyFilters"
                                >
                                <span>免配送费</span>
                            </label>
                        </div>

                        <!-- 起送价筛选 -->
                        <div class="filter-section">
                            <h4>起送价</h4>
                            <div class="price-range">
                                <label class="filter-option">
                                    <input 
                                        type="radio" 
                                        name="startPrice" 
                                        value="0"
                                        v-model="filters.startPrice"
                                        @change="applyFilters"
                                    >
                                    <span>不限</span>
                                </label>
                                <label class="filter-option">
                                    <input 
                                        type="radio" 
                                        name="startPrice" 
                                        value="20"
                                        v-model="filters.startPrice"
                                        @change="applyFilters"
                                    >
                                    <span>20元以下</span>
                                </label>
                                <label class="filter-option">
                                    <input 
                                        type="radio" 
                                        name="startPrice" 
                                        value="30"
                                        v-model="filters.startPrice"
                                        @change="applyFilters"
                                    >
                                    <span>30元以下</span>
                                </label>
                                <label class="filter-option">
                                    <input 
                                        type="radio" 
                                        name="startPrice" 
                                        value="50"
                                        v-model="filters.startPrice"
                                        @change="applyFilters"
                                    >
                                    <span>50元以下</span>
                                </label>
                            </div>
                        </div>
                    </div>

                    <div class="filter-footer">
                        <button class="btn-reset" @click="resetFilters">重置</button>
                        <button class="btn-confirm" @click="confirmFilters">确定</button>
                    </div>
                </div>
            </div>
        </transition>

        <!-- 推荐商家列表部分 -->
        <ul class="business-list">
            <li v-for="business in businessList" :key="business.id || business.businessId" @click="toBusinessInfo(business.id || business.businessId)">
                <div class="business-info">
                    <img :src="business.businessImg === 'string' ? require('@/assets/default-business.png') : business.businessImg" @error="handleImageError" :alt="business.businessName">
                    <div class="business-info-detail">
                        <h3>{{ business.businessName === 'string' ? '商家名称待更新' : business.businessName }}</h3>
                        <div class="business-info-rating">
                            <span class="rating-number">评分：{{ business.score || getBusinessRating(business.id || business.businessId) }}</span>
                            <span class="sales-number">销量：{{ business.salesCount || 0 }}</span>
                        </div>
                        <div class="business-info-delivery">
                            <span>起送 ¥{{ business.startPrice || business.starPrice }}</span>
                            <span>配送 ¥{{ business.deliveryPrice }}</span>
                        </div>
                        <div class="business-info-promotion">
                            <div class="business-info-promotion-left">
                            </div>
                        </div>
                    </div>
                </div>
            </li>
        </ul>

        <!-- 底部菜单部分 -->

    </div>
</template>

<script>
import { ref, onMounted, onBeforeUnmount,computed } from 'vue';
import Footer from '../components/Footer.vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import request from '../utils/request';
import AMapLoader from '@amap/amap-jsapi-loader';
// 高德地图API key（请替换为你的实际key）
const AMAP_KEY = '24cce1eb31aec79422f44af47428fc8a';

export default {
    name: 'Index',
    setup() {
        const fixedBox = ref(null);
        const router = useRouter();
        const userInfo = ref(null);
        const businessList = ref([]);
        const ratingMap = ref({});
        const currentLocation = ref('定位中...');
        const searchKeyword = ref('');
        const sortBy = ref('default');
        const showPicker = ref(false);
        const showFilter = ref(false);
        const filters = ref({
            freeDelivery: false,
            startPrice: '0'
        });
        const loading = ref(false);
        const locationData = ref([]);
        const currentLevel = ref(0);
        const selectedLocation = ref({
            province: '',
            city: '',
            district: ''
        });
        const locationLevels = ref(['请选择省份', '请选择城市', '请选择区域']);
        // 新增：临时存储选择过程中的地址，不直接影响显示
        const tempSelectedLocation = ref({
            province: '',
            city: '',
            district: ''
        });
        
        // 计算显示的位置文本
        const displayLocation = computed(() => {
    const { province, city, district } = selectedLocation.value;
    
    // 如果有区级信息，显示完整省市区
    if (district && province && city) {
        // 如果是直辖市，省和市名相同，只显示一次省/市名
        if (province === city) {
            return `${province} ${district}`;
        }
        return `${province} ${city} ${district}`;
    }
    
    // 只有省市信息
    if (province && city) {
        return `${province} ${city}`;
    }
    
    // 只有省信息
    if (province) {
        return province;
    }
    
    // 默认情况
    return currentLocation.value;
        });

        // 获取当前位置
        const getCurrentLocation = async () => {
            try {
                // 使用高德地图IP定位API
                const response = await request.get(`https://restapi.amap.com/v3/ip?key=${AMAP_KEY}`);
                if (response.data.status === '1' && response.data.city) {
                    currentLocation.value = response.data.city;
                    // 初始化选择位置
                    selectedLocation.value = {
                        province: response.data.province,
                        city: response.data.city,
                        district: ''
                    };
                } else {
                    currentLocation.value = '天津大学北洋园校区';
                }
            } catch (error) {
                console.error('获取位置失败:', error);
                currentLocation.value = '天津大学北洋园校区';
            }
        };

        // 显示位置选择器
        const showLocationPicker = () => {
            showPicker.value = true;
            loadProvinces();
        };

        // 隐藏位置选择器
        const hideLocationPicker = () => {
            showPicker.value = false;
    // 重置临时变量：恢复为当前已确认的最终地址
    tempSelectedLocation.value = { ...selectedLocation.value };
        };

        // 加载省份数据
        const loadProvinces = async () => {
            loading.value = true;
            try {
                const response = await request.get(`https://restapi.amap.com/v3/config/district?key=${AMAP_KEY}&keywords=中国&subdistrict=1`);
                if (response.data.status === '1') {
                    locationData.value = response.data.districts[0].districts;
                    currentLevel.value = 0;
                }
            } catch (error) {
                console.error('加载省份数据失败:', error);
            } finally {
                loading.value = false;
            }
        };

        // 加载城市数据
        const loadCities = async (provinceCode, provinceName) => {
            loading.value = true;
            try {
                const response = await request.get(`https://restapi.amap.com/v3/config/district?key=${AMAP_KEY}&keywords=${provinceCode}&subdistrict=1`);
                if (response.data.status === '1' && response.data.districts[0].districts) {
                    locationData.value = response.data.districts[0].districts;
                    currentLevel.value = 1;
                    tempSelectedLocation.value.province = provinceName;
                    // 重置临时变量的城市/区域（避免之前的残留值）
                    tempSelectedLocation.value.city = '';
                    tempSelectedLocation.value.district = '';
                }
            } catch (error) {
                console.error('加载城市数据失败:', error);
            } finally {
                loading.value = false;
            }
        };

        // 加载区域数据
        const loadDistricts = async (cityCode, cityName) => {
            loading.value = true;
            try {
                const response = await request.get(`https://restapi.amap.com/v3/config/district?key=${AMAP_KEY}&keywords=${cityCode}&subdistrict=1`);
                if (response.data.status === '1' && response.data.districts[0].districts) {
                    locationData.value = response.data.districts[0].districts;
                    currentLevel.value = 2;
                    tempSelectedLocation.value.city = cityName;
                    // 重置临时变量的区域
                    tempSelectedLocation.value.district = '';
                }
            } catch (error) {
                console.error('加载区域数据失败:', error);
            } finally {
                loading.value = false;
            }
        };

        // 切换级别
        const switchLevel = (level) => {
            if (level < currentLevel.value) {
                currentLevel.value = level;
                if (level === 0) {
                    // 切换回省份级：重置临时变量的城市/区域
                tempSelectedLocation.value.city = '';
                tempSelectedLocation.value.district = '';
                    loadProvinces();
                } else if (level === 1) {
                    loadCities(tempSelectedLocation.value.province, tempSelectedLocation.value.province);
                    // 切换回城市级：重置临时变量的区域
                    tempSelectedLocation.value.district = '';
                }
            }
        };

        // 选择位置
        const selectLocation = (item) => {
            if (currentLevel.value === 0) {
                loadCities(item.adcode, item.name);
            } else if (currentLevel.value === 1) {
                loadDistricts(item.adcode, item.name);
            } else if (currentLevel.value === 2) {
                tempSelectedLocation.value.district = item.name;
            }
        };

        // 确认选择
        const confirmLocation = () => {
            const { province, city, district } = tempSelectedLocation.value; // 校验临时变量
            // 1. 严格校验：必须完整选择省、市、区
            if (!province) {
                alert('请先选择省份');
                return;
            }
            if (!city) {
                alert('请先选择城市');
                return;
            }
            if (!district) {
                alert('请先选择区域');
                return;
            }

            // 2. 校验通过：同步临时变量到最终变量
            selectedLocation.value = { ...tempSelectedLocation.value };
            // 3. 保存到本地存储
            localStorage.setItem('userLocation', JSON.stringify(selectedLocation.value));
            const displayText = getDisplayText(selectedLocation.value);
            localStorage.setItem('userLocationDisplay', displayText);
            
            // 4. 关闭弹窗
            hideLocationPicker();
        };
        // 新增一个方法来生成显示文本
const getDisplayText = (location) => {
    const { province, city, district } = location;
    if (district && province && city) {
        if (province === city) {
            return `${province} ${district}`;
        }
        return `${province} ${city} ${district}`;
    }
    if (province && city) {
        return `${province} ${city}`;
    }
    if (province) {
        return province;
    }
    return '未知位置';
};

        // 检查是否选中
        const isSelected = (item) => {
            const { province, city, district } = tempSelectedLocation.value; // 关键：用临时变量
            if (currentLevel.value === 0) {
                return province === item.name;
            } else if (currentLevel.value === 1) {
                return city === item.name;
            } else if (currentLevel.value === 2) {
                return district === item.name;
            }
            return false;
        };


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

        const loadReactions = () => {
            try {
                return JSON.parse(localStorage.getItem('reactions')) || { likes: {}, favorites: {} };
            } catch (e) {
                return { likes: {}, favorites: {} };
            }
        };
        
        const getReactionCount = (businessId) => {
            const reactions = loadReactions();
            const likesMap = reactions.likes[String(businessId)] || {};
            const favsMap = reactions.favorites[String(businessId)] || {};
            const likes = Object.keys(likesMap).length;
            const favs = Object.keys(favsMap).length;
            return likes + favs;
        };

        const guessCommentCount = (biz) => {
            // 兼容不同后端字段命名，若不存在则为0
            return (
                biz.commentCount ??
                biz.comments ??
                biz.remarkNum ??
                biz.reviewCount ??
                0
            ) || 0;
        };

        const computeRatings = () => {
            const entries = businessList.value || [];
            if (!entries.length) { ratingMap.value = {}; return; }
            const reactionCounts = entries.map(b => getReactionCount(b.businessId));
            const commentCounts = entries.map(b => guessCommentCount(b));
            const rMin = Math.min(...reactionCounts);
            const rMax = Math.max(...reactionCounts);
            const cMin = Math.min(...commentCounts);
            const cMax = Math.max(...commentCounts);
            const weights = { comments: 0.6, reactions: 0.4 };
            const map = {};
            entries.forEach((b, idx) => {
                const r = reactionCounts[idx];
                const c = commentCounts[idx];
                const rNorm = rMax > rMin ? (r - rMin) / (rMax - rMin) : (r > 0 ? 1 : 0);
                const cNorm = cMax > cMin ? (c - cMin) / (cMax - cMin) : (c > 0 ? 1 : 0);
                const combined = weights.comments * cNorm + weights.reactions * rNorm;
                let rating = 1 + combined * 4; // map to [1,5]
                if (rating < 1) rating = 1;
                if (rating > 5) rating = 5;
                map[b.businessId] = rating.toFixed(1);
            });
            ratingMap.value = map;
        };

        const getBusinessRating = (businessId) => {
            return ratingMap.value[businessId] || '1.0';
        };

        const navigateToOrders = () => {
            router.push({ path: '/orders' });
        };
        const handleScroll = () => {
            let scroll = window.scrollY || document.documentElement.scrollTop;
            let width = document.documentElement.clientWidth;
            let search = fixedBox.value;

            if (scroll > width * 0.12) {
                search.style.position = 'fixed';
                search.style.left = '0';
                search.style.top = '0';
            } else {
                search.style.position = 'static';
            }
        };
        onMounted(() => {
            // 先从localStorage获取保存的位置
    const savedLocation = localStorage.getItem('userLocation');
    if (savedLocation) {
        try {
            const location = JSON.parse(savedLocation);
            selectedLocation.value = location;
            tempSelectedLocation.value = { ...location };
            
            // 如果有保存的显示文本，直接使用
            const savedDisplay = localStorage.getItem('userLocationDisplay');
            if (savedDisplay) {
                currentLocation.value = savedDisplay;
            }
        } catch (e) {
            getCurrentLocation();
        }
    } else {
        getCurrentLocation();
    }
            // 加载用户信息
            fetchUserInfo();
    
            window.addEventListener('scroll', handleScroll);

            getBusinessList();
        });

        onBeforeUnmount(() => {
            window.removeEventListener('scroll', handleScroll);
        });

        const toBusinessList = (orderTypeId) => {
            router.push({ path: '/BusinessList', query: { orderTypeId } });
        };
        const goToLChoose = () => {
            // 跳转到登录页面
            router.push({path: '/login'});
        };
        const goToRChoose = () => {
            // 跳转到注册页面
            console.log('111111');
            router.push({name:'RChoose'});
        }
        const navigateToSearch = () => {
            router.push({ path: '/search' });
        };

        // 执行搜索
        const performSearch = async () => {
            if (searchKeyword.value.trim() !== '') {
                try {
                    // 构建查询参数
                    const params = {
                        keyword: searchKeyword.value.trim()
                    };

                    // 根据排序方式添加参数
                    if (sortBy.value === 'score') {
                        params.isScore = 1;
                        params.isSales = 0;
                    } else if (sortBy.value === 'sales') {
                        params.isScore = 0;
                        params.isSales = 1;
                    } else if (sortBy.value === 'distance') {
                        params.isScore = 0;
                        params.isSales = 0;
                        // 距离排序可能需要后端支持，这里先按评分排序
                        params.isScore = 1;
                    } else {
                        params.isScore = 0;
                        params.isSales = 0;
                    }

                    console.log('搜索参数:', params);
                    console.log('请求URL:', 'http://localhost:8080/api/businesses/search');

                    // 调用搜索接口
                    const response = await request.get('/api/businesses/search', { params });

                    console.log('111搜索响应:', response);
                    console.log('222响应状态:', response?.status);
                    console.log('333响应数据:', response?.data);

                    // 更新商家列表 - 根据API文档的响应格式处理
                    if (response && response.success && response.data && Array.isArray(response.data)) {
                        businessList.value = response.data;
                        computeRatings();
                    } else {
                        console.warn('搜索响应格式不正确:', response);
                        businessList.value = [];
                    }

                } catch (error) {
                    console.error('搜索失败:', error);
                    console.error('错误详情:', error.response?.data);
                    console.error('错误状态:', error.response?.status);
                    console.error('错误信息:', error.message);
                    // 如果搜索失败，显示所有商家
                    getBusinessList();
                }
            } else {
                // 如果搜索关键词为空，显示所有商家
                getBusinessList();
            }
        };

        // 设置排序方式
        const setSortBy = (type) => {
            sortBy.value = type;
            console.log('设置排序方式:', type);
            
            if (searchKeyword.value.trim() !== '') {
                performSearch(); // 重新搜索以应用新的排序
            } else {
                getBusinessList(); // 如果没有搜索关键词，重新加载所有商家
            }
        };

        // 获取商家列表
        const getBusinessList = () => {
            console.log('开始获取商家列表...');
            // 尝试使用新的API路径
            request.get('/api/businesses', { params: { orderTypeId: 1 } })
                .then(response => {
                    console.log('商家列表响应:', response);
                    // 检查响应数据结构
                    if (response && response.success && response.data && Array.isArray(response.data)) {
                        businessList.value = response.data;
                        console.log('商家列表数据:', businessList.value);
                        computeRatings();
                    } else if (response && Array.isArray(response)) {
                        // 如果直接返回数组
                        businessList.value = response;
                        console.log('商家列表数据:', businessList.value);
                        computeRatings();
                    } else {
                        console.warn('响应数据为空或格式不正确:', response);
                        businessList.value = [];
                    }
                })
                .catch(error => {
                    console.error('获取商家列表失败:', error);
                    // 如果新接口失败，尝试使用搜索接口获取所有商家
                    console.log('尝试使用搜索接口获取商家列表...');
                    request.get('/api/businesses/search', { params: { keyword: '', isScore: 0, isSales: 0 } })
                        .then(searchResponse => {
                            console.log('搜索接口响应:', searchResponse);
                            if (searchResponse && searchResponse.success && searchResponse.data && Array.isArray(searchResponse.data)) {
                                businessList.value = searchResponse.data;
                                console.log('通过搜索接口获取的商家列表:', businessList.value);
                                computeRatings();
                            } else {
                                console.warn('搜索接口响应格式不正确:', searchResponse);
                                businessList.value = [];
                            }
                        })
                        .catch(searchError => {
                            console.error('搜索接口也失败了:', searchError);
                            businessList.value = [];
                        });
                });
        };

        // 处理图片加载失败
        const handleImageError = (e) => {
            e.target.src = require('@/assets/default-business.png');
        };
        
        // 跳转到商家详情页
        const toBusinessInfo = (businessId) => {
            router.push({
                path: '/businessInfo',
                query: { businessId }
            });
        };

        // 筛选功能
        const toggleFilter = () => {
            showFilter.value = !showFilter.value;
        };

        const hideFilter = () => {
            showFilter.value = false;
        };

        const applyFilters = () => {
            console.log('应用筛选条件:', filters.value);
            // 这里可以添加筛选逻辑
            filterBusinessList();
        };

        const resetFilters = () => {
            filters.value = {
                freeDelivery: false,
                startPrice: '0'
            };
            applyFilters();
        };

        const confirmFilters = () => {
            applyFilters();
            hideFilter();
        };

        // 筛选商家列表
        const filterBusinessList = () => {
            let filteredList = [...businessList.value];

            // 免配送费筛选
            if (filters.value.freeDelivery) {
                filteredList = filteredList.filter(business => 
                    business.deliveryPrice === 0 || business.deliveryPrice === null
                );
            }

            // 起送价筛选
            if (filters.value.startPrice !== '0') {
                const maxPrice = parseInt(filters.value.startPrice);
                filteredList = filteredList.filter(business => 
                    business.startPrice <= maxPrice
                );
            }

            businessList.value = filteredList;
            console.log('筛选后的商家列表:', filteredList);
        };

        return {
            fixedBox,
            toBusinessList,
            navigateToOrders,
            goToLChoose,
            goToRChoose,
            userInfo,
            isuser: computed(() => !!userInfo.value),
            navigateToSearch,
            businessList,
            toBusinessInfo,
            handleImageError,
            getBusinessRating,
            displayLocation,
            showPicker,
            loading,
            locationData,
            currentLevel,
            locationLevels,
            selectedLocation,
            showLocationPicker,
            hideLocationPicker,
            switchLevel,
            selectLocation,
            isSelected,
            confirmLocation,
            getDisplayText,
            searchKeyword,
            sortBy,
            performSearch,
            setSortBy,
            showFilter,
            filters,
            toggleFilter,
            hideFilter,
            applyFilters,
            resetFilters,
            confirmFilters
        };
    },
    components: {
        Footer
    }
}
</script>

<style scoped>
/****************** 总容器 ******************/
.wrapper {
    width: 100%;
    height: 100%;
}

/****************** header ******************/
.wrapper header {
    width: 100%;
    height: 12vw;
    background-color: #0097ff;

    display: flex;
    align-items: center;
 
    /* 让location和login-register两端对齐 */
    padding: 0 3vw;
    /* 添加两边的内边距，使内容不要紧贴屏幕边缘 */
}

.wrapper header .icon-location-box {
    width: 3.5vw;
    height: 3.5vw;
    margin-right: 1vw;
}

.wrapper header .location-text {
    font-size: 4.5vw;
    font-weight: 700;
    color: #fff;
}

.wrapper header .icon-location-box i {
    font-size: 5vw;
    color: #fff;
}

.wrapper header .location-text .fa-caret-down {
    margin-left: 1vw;
}

/****************** 登录、注册部分 ******************/
.wrapper .login-register {
    display: flex;
    gap: 2vw;
    align-items: center;
    margin-left: 5vw;
}

.wrapper .login-register .user-info {
    font-size: 4vw;
    /* 增加字体大小 */
    font-weight: 500;
    color: #fff;
}

.wrapper .login-register button {
    padding: 1.5vw 3vw;
    /* 增加按钮的内边距，变大 */
    border: none;
    background-color: white;
    color: #0097ff;
    cursor: pointer;
    border-radius: 1vw;
    /* 加大圆角 */
    transition: background-color 0.3s;
    font-size: 3.5vw;
    /* 增加按钮文字的大小 */
}

.wrapper .login-register button:hover {
    background-color: #f0f0f0;
}

/****************** search ******************/
.wrapper .search {
    width: 100%;
    height: 13vw;
}

.wrapper .search .search-fixed-top {
    width: 100%;
    height: 13vw;
    background-color: #0097FF;
    display: flex;
    justify-content: center;
    align-items: center;
}

.wrapper .search .search-fixed-top .search-box {
    width: 90%;
    height: 9vw;
    background-color: #fff;
    border-radius: 2px;

    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 0 2vw;

    font-size: 3.5vw;
    color: #AEAEAE;
    font-family: "宋体";
    /*此样式是让文本选中状态无效*/
    user-select: none;
}

.wrapper .search .search-fixed-top .search-box input {
    flex: 1;
    border: none;
    outline: none;
    background: transparent;
    font-size: 3.5vw;
    color: #333;
    margin: 0 1vw;
}

.wrapper .search .search-fixed-top .search-box input::placeholder {
    color: #AEAEAE;
}

.wrapper .search .search-fixed-top .search-box .search-btn {
    background: #0097ff;
    color: white;
    border: none;
    padding: 1.5vw 3vw;
    border-radius: 1vw;
    font-size: 3vw;
    cursor: pointer;
    transition: background-color 0.3s;
}

.wrapper .search .search-fixed-top .search-box .search-btn:hover {
    background: #0080e0;
}

.wrapper .search .search-fixed-top .search-box .fa-search {
    margin-right: 1vw;
}

/* 排序选项样式 */
.sort-options {
    width: 100%;
    padding: 3vw;
    background-color: #f8f9fa;
    border-bottom: 1px solid #e0e0e0;
}

.sort-buttons {
    display: flex;
    gap: 2vw;
    justify-content: center;
    flex-wrap: wrap;
}

.sort-buttons button {
    padding: 2vw 4vw;
    border: 1px solid #ddd;
    background-color: white;
    color: #666;
    border-radius: 2vw;
    cursor: pointer;
    transition: all 0.3s;
    font-size: 3.2vw;
    min-width: 20vw;
}

.sort-buttons button:hover {
    border-color: #0097ff;
    color: #0097ff;
}

.sort-buttons button.active {
    background-color: #0097ff;
    color: white;
    border-color: #0097ff;
}

/****************** 点餐分类部分 ******************/
.wrapper .foodtype {
    width: 100%;
    height: 48vw;

    display: flex;
    flex-wrap: wrap;
    justify-content: space-around;
    /*要使用align-content。10个子元素将自动换行为两行，而且两行作为一个整体垂直居中*/
    align-content: center;
}

.wrapper .foodtype li {
    /*一共10个子元素，通过计算，子元素宽度在16.7 ~ 20 之间，才能保证换两行*/
    width: 18vw;
    height: 20vw;

    display: flex;
    /*弹性盒子主轴方向设为column，然后仍然是垂直水平方向居中*/
    flex-direction: column;
    justify-content: center;
    align-items: center;

    user-select: none;
    cursor: pointer;
}

.wrapper .foodtype li img {
    width: 12vw;
    /*视频讲解时高度设置为12vw，实际上设置为10.3vw更佳*/
    height: 10.3vw;
}

.wrapper .foodtype li p {
    font-size: 3.2vw;
    color: #666;
}

/****************** 横幅广告部分 ******************/
.wrapper .banner {
    /**
             * 设置容器宽度95%，然后水平居中，这样两边留白; 
             * 这里不能用padding，因为背景图片也会覆盖padding
             */
    width: 95%;
    margin: 0 auto;
    height: 29vw;

    /*此三个样式组合，可以保证背景图片充满整个容器*/
    background-image: url(@/assets/index_banner.png);
    background-repeat: no-repeat;
    background-size: cover;

    box-sizing: border-box;
    padding: 2vw 6vw;
}

.wrapper .banner h3 {
    font-size: 4.2vw;
    margin-bottom: 1.2vw;
}

.wrapper .banner p {
    font-size: 3.4vw;
    color: #666;
    margin-bottom: 2.4vw;
}

.wrapper .banner a {
    font-size: 3vw;
    color: #C79060;
    font-weight: 700;
}

/****************** 超级会员部分 ******************/
.wrapper .supermember {
    /*这里也设置容器宽度95%，不能用padding，因为背景色也会充满padding*/
    width: 95%;
    margin: 0 auto;
    height: 11.5vw;
    background-color: #FEEDC1;
    margin-top: 1.3vw;
    border-radius: 2px;
    color: #644F1B;

    display: flex;
    justify-content: space-between;
    align-items: center;
}

.wrapper .supermember .left {
    display: flex;
    align-items: center;
    margin-left: 4vw;
    user-select: none;
}

.wrapper .supermember .left img {
    width: 6vw;
    height: 6vw;
    margin-right: 2vw;
}

.wrapper .supermember .left h3 {
    font-size: 4vw;
    margin-right: 2vw;
}

.wrapper .supermember .left p {
    font-size: 3vw;
}

.wrapper .supermember .right {
    font-size: 3vw;
    margin-right: 4vw;
    cursor: pointer;
}

/****************** 推荐商家部分 ******************/
.wrapper .recommend {
    width: 100%;
    height: 14vw;
    display: flex;
    justify-content: center;
    align-items: center;
}

.wrapper .recommend .recommend-line {
    width: 6vw;
    height: 0.2vw;
    background-color: #888;
}

.wrapper .recommend p {
    font-size: 4vw;
    margin: 0 4vw;
}

/****************** 推荐方式部分 ******************/
.wrapper .recommendtype {
    width: 100%;
    height: 5vw;
    margin-bottom: 5vw;

    display: flex;
    justify-content: space-around;
    align-items: center;
}

.wrapper .recommendtype li {
    font-size: 3.5vw;
    color: #555;
}

/****************** 推荐商家列表部分 ******************/
.wrapper .business-list {
    width: 100%;
    padding: 0;
    margin: 0 0 15vh 0; /* 添加底部边距，避免被 Footer 遮挡 */
    list-style: none;
}

.wrapper .business-list li {
    padding: 3vw;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
    transition: background-color 0.3s;
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

.wrapper .business-list li .business-info .business-info-rating {
    display: flex;
    align-items: center;
    margin-bottom: 2vw;
    justify-content: flex-end;
}

.wrapper .business-list li .business-info .business-info-rating .rating {
    display: flex;
}

.wrapper .business-list li .business-info .business-info-rating .rating .fa-star {
    color: #999;
    font-size: 3vw;
}

.wrapper .business-list li .business-info .business-info-rating .rating .fa-star.active {
    color: #ffd700;
}

.wrapper .business-list li .business-info .business-info-rating .rating-number {
    font-size: 2.8vw;
    color: #999;
}

.wrapper .business-list li .business-info .business-info-rating .sales-number {
    font-size: 2.8vw;
    color: #999;
    margin-left: 2vw;
}

.wrapper .business-list li .business-info .business-info-rating .sales {
    margin-left: 2vw;
    font-size: 3vw;
    color: #666;
}

.wrapper .business-list li .business-info .business-info-delivery {
    display: flex;
    gap: 2vw;
    font-size: 3vw;
    color: #666;
    margin-bottom: 2vw;
}

.wrapper .business-list li .business-info .business-info-promotion {
    display: flex;
    align-items: center;
}

.wrapper .business-list li .business-info .business-info-promotion .business-info-promotion-left {
    display: flex;
    align-items: center;
    gap: 1vw;
}

.wrapper .business-list li .business-info .business-info-promotion .business-info-promotion-left .business-info-promotion-left-incon {
    background-color: #ff4444;
    color: white;
    padding: 0.5vw 1vw;
    border-radius: 2px;
    font-size: 2.5vw;
}

.wrapper .business-list li .business-info .business-info-promotion .business-info-promotion-left p {
    color: #666;
    font-size: 3vw;
    margin: 0;
}
/* 位置显示样式 */
.location-text {
    cursor: pointer;
    transition: color 0.3s;
    display: flex;
    align-items: center;
    gap: 4px;
}

.location-text:hover {
    color: #e0e0e0;
}

.location-display {
    max-width: 180px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

/* 模态框样式 */
.location-modal {
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
    padding: 20px;
}

.modal-container {
    background: white;
    border-radius: 12px;
    width: 100%;
    max-width: 400px;
    max-height: 80vh;
    display: flex;
    flex-direction: column;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
    overflow: hidden;
}

.modal-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    border-bottom: 1px solid #f0f0f0;
    background: linear-gradient(135deg, #0097ff, #0066cc);
    color: white;
}

.modal-header h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
}

.close-btn {
    background: none;
    border: none;
    color: white;
    font-size: 20px;
    cursor: pointer;
    padding: 5px;
    border-radius: 50%;
    transition: background-color 0.3s;
}

.close-btn:hover {
    background-color: rgba(255, 255, 255, 0.2);
}

.modal-content {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
}

/* 位置导航样式 */
.location-nav {
    display: flex;
    margin-bottom: 20px;
    border-bottom: 2px solid #f0f0f0;
}

.nav-item {
    padding: 12px 20px;
    cursor: pointer;
    border-bottom: 3px solid transparent;
    transition: all 0.3s;
    font-weight: 500;
    color: #666;
}

.nav-item.active {
    color: #0097ff;
    border-bottom-color: #0097ff;
}

.nav-item.disabled {
    color: #ccc;
    cursor: not-allowed;
}

.nav-item:not(.disabled):hover {
    color: #0097ff;
}

/* 位置列表样式 */
.location-list-container {
    min-height: 200px;
    max-height: 300px;
    overflow-y: auto;
    margin-bottom: 20px;
}

.loading-state, .empty-state {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    height: 150px;
    color: #999;
}

.loading-state i, .empty-state i {
    font-size: 24px;
    margin-bottom: 10px;
}

.location-items {
    display: grid;
    gap: 8px;
}

.location-item {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 12px 16px;
    border: 1px solid #e0e0e0;
    border-radius: 8px;
    cursor: pointer;
    transition: all 0.3s;
}

.location-item:hover {
    border-color: #0097ff;
    background-color: #f8f9ff;
}

.location-item.selected {
    border-color: #0097ff;
    background-color: #e6f3ff;
}

.item-name {
    font-weight: 500;
}

.selected-icon {
    color: #0097ff;
    font-size: 14px;
}

/* 当前选择显示 */
.current-selection {
    padding: 15px;
    background-color: #f8f9fa;
    border-radius: 8px;
    margin-top: 15px;
}

.selection-text {
    font-weight: 600;
    color: #0097ff;
    display: inline-block;
    max-width: 250px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

/* 模态框底部 */
.modal-footer {
    display: flex;
    gap: 12px;
    padding: 20px;
    border-top: 1px solid #f0f0f0;
    background-color: #fafafa;
}

.btn-cancel, .btn-confirm {
    flex: 1;
    padding: 12px;
    border: none;
    border-radius: 6px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s;
}

.btn-cancel {
    background-color: #f8f9fa;
    color: #666;
}

.btn-cancel:hover {
    background-color: #e9ecef;
}

.btn-confirm {
    background: linear-gradient(135deg, #0097ff, #0066cc);
    color: white;
}

.btn-confirm:hover {
    background: linear-gradient(135deg, #0080e0, #0055aa);
    transform: translateY(-1px);
}

/* 动画效果 */
.fade-enter-active, .fade-leave-active {
    transition: opacity 0.3s;
}

.fade-enter-from, .fade-leave-to {
    opacity: 0;
}

/* 推荐方式样式 */
.wrapper .recommendtype {
    width: 100%;
    height: 5vw;
    margin-bottom: 5vw;
    display: flex;
    justify-content: space-around;
    align-items: center;
}

.wrapper .recommendtype li {
    font-size: 3.5vw;
    color: #555;
    cursor: pointer;
    transition: color 0.3s;
    padding: 1vw 2vw;
    border-radius: 1vw;
}

.wrapper .recommendtype li:hover {
    color: #0097ff;
}

.wrapper .recommendtype li.active {
    color: #0097ff;
    background-color: #f0f8ff;
}

/* 筛选弹窗样式 */
.filter-modal {
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
    padding: 20px;
}

.filter-container {
    background: white;
    border-radius: 12px;
    width: 100%;
    max-width: 400px;
    max-height: 80vh;
    display: flex;
    flex-direction: column;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
    overflow: hidden;
}

.filter-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 20px;
    border-bottom: 1px solid #f0f0f0;
    background: linear-gradient(135deg, #0097ff, #0066cc);
    color: white;
}

.filter-header h3 {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
}

.close-btn {
    background: none;
    border: none;
    color: white;
    font-size: 20px;
    cursor: pointer;
    padding: 5px;
    border-radius: 50%;
    transition: background-color 0.3s;
}

.close-btn:hover {
    background-color: rgba(255, 255, 255, 0.2);
}

.filter-content {
    flex: 1;
    padding: 20px;
    overflow-y: auto;
}

.filter-section {
    margin-bottom: 25px;
}

.filter-section h4 {
    font-size: 16px;
    font-weight: 600;
    margin: 0 0 15px 0;
    color: #333;
}

.filter-option {
    display: flex;
    align-items: center;
    margin-bottom: 12px;
    cursor: pointer;
    font-size: 14px;
    color: #666;
}

.filter-option input[type="checkbox"],
.filter-option input[type="radio"] {
    margin-right: 10px;
    transform: scale(1.2);
}

.filter-option:hover {
    color: #0097ff;
}

.price-range {
    display: flex;
    flex-direction: column;
    gap: 8px;
}

.filter-footer {
    display: flex;
    gap: 12px;
    padding: 20px;
    border-top: 1px solid #f0f0f0;
    background-color: #fafafa;
}

.btn-reset, .btn-confirm {
    flex: 1;
    padding: 12px;
    border: none;
    border-radius: 6px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s;
}

.btn-reset {
    background-color: #f8f9fa;
    color: #666;
}

.btn-reset:hover {
    background-color: #e9ecef;
}

.btn-confirm {
    background: linear-gradient(135deg, #0097ff, #0066cc);
    color: white;
}

.btn-confirm:hover {
    background: linear-gradient(135deg, #0080e0, #0055aa);
    transform: translateY(-1px);
}
</style>