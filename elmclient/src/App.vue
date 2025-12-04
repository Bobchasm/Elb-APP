<template>
  <div class="app-container">
    <BackButton v-if="showBackButton" />
    <div class="content">
      <keep-alive include="Discover">
        <router-view />
      </keep-alive>
    </div>
    <Footer v-if="showFooter" />
    <BusinessFooter v-if="showBusinessFooter" />
    <AdminFooter v-if="showAdminFooter" />
  </div>
</template>

<script>
import BackButton from './components/BackButton.vue';
import Footer from './components/Footer.vue';
import BusinessFooter from './components/BusinessFooter.vue';
import AdminFooter from './components/AdminFooter.vue';
import { computed } from 'vue';
import { useRoute } from 'vue-router';

export default {
  components: {
    BackButton,
    Footer,
    BusinessFooter,
    AdminFooter,
  },
  setup() {
    const route = useRoute();

    const showBackButton = computed(() => {
      if (route.path.startsWith('/merchant') || route.path.startsWith('/admin')) {
        return false;
      }
         });


    const showFooter = computed(() => {
      if (route.path.startsWith('/merchant') || route.path.startsWith('/admin')) {
        return false;
      }
      return !['BusinessInfo', 'Payment', 'SuccessfulPayment', 'Orders', 'Cart','Favorites','Notifications','UserAddress','ListDetail','Register','Login','EditUserAddress','Points','PointsLottery','PointsDetails','PointsExpiring'].includes(route.name);
    });

    const showBusinessFooter = computed(() => {
     if(route.path=== '/merchant/businessinfo'){
        return false;
      }
      return route.path.startsWith('/merchant');
      
    });

    const showAdminFooter = computed(() => {
      return route.path.startsWith('/admin');
    });

    return { showFooter, showBusinessFooter, showAdminFooter, showBackButton};
  },
};
</script>

<style>
/* 保持所有原有样式不变 */
html,
body,
div,
span,
h1,
h2,
h3,
h4,
h5,
h6,
ul,
ol,
li,
p {
  margin: 0;
  padding: 0;
}

html,
body,
#app {
  width: 100%;
  height: 100%;
  font-family: "微软雅黑";
}

html,
body {
  margin: 0;
  padding: 0;
  height: 100%;
}

ul,
ol {
  list-style: none;
}

a {
  text-decoration: none;
}
.app-container {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
}

.content {
  flex: 1;
  overflow-y: auto;
}

/*试图统一header */
.app-header-fixed {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    /* 确保在宽屏设备上不会过宽，这里设置 max-width */
    max-width: 600px; 
    /* 在 fixed 模式下，margin: 0 auto 需要额外的 left/right 配合 */
    left: 50%;
    transform: translateX(-50%); 
    
    height: 12vw; /* 统一高度，使用 vw */
    background-color: #0097FF;
    color: #fff;
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.app-header-fixed h3,
.app-header-fixed h1 { /* 统一 h1 和 h3 的样式 */
    font-size: 4.8vw; /* 统一字体大小 */
    margin: 0;
    font-weight: 500;
    color: inherit; /* 继承父级的 #fff 颜色 */
}

/* ⚠️ 重要：添加 padding-top 以防止内容被固定头部遮挡 */
.details-container {
    padding-top: 13vw; /* 顶部容器增加内边距，等于头部高度 */
}

</style>