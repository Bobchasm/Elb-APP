<template>
  <div class="app-container">
    <BackButton />
    <div class="content">
      <keep-alive include="Discover">
        <router-view />
      </keep-alive>
    </div>
    <Footer v-if="showFooter" />
  </div>
</template>

<script>
import BackButton from './components/BackButton.vue';
import Footer from './components/Footer.vue';
import { computed } from 'vue';
import { useRoute } from 'vue-router';

export default {
  components: {
    BackButton,
    Footer,
  },
  setup() {
    const route = useRoute();

    // 核心逻辑：根据路由路径判断是否为商家页面
    const showFooter = computed(() => {
      // 如果当前路由路径以 /merchant 开头，则隐藏普通导航栏
      if (route.path.startsWith('/merchant')) {
        return false;
      }
	  if (route.path.startsWith('/admin')) {
        return false;
      }
      
      // 对于普通用户，在特定页面不显示 footer
      return !['BusinessInfo', 'Payment', 'SuccessfulPayment', 'Orders', 'Cart'].includes(route.name);
    });
    
    return { showFooter };
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
</style>