// import { createApp } from 'vue'
// import App from './App.vue'
// import router from './router'

// createApp(App).use(router).mount('#app')
// import Vue from 'vue'
//  import App from './App.vue'
//  import router from './router'
//  import 'font-awesome/css/font-awesome.min.css'
//  import axios from 'axios'
//  import qs from 'qs'
//  import {
//  getCurDate,
//  setSessionStorage,
//  getSessionStorage,
//  removeSessionStorage,
//  setLocalStorage,
//  getLocalStorage,
//  removeLocalStorage
//  } from './common.js'
//  Vue.config.productionTip = false
//  //设置axios的基础url部分
// axios.defaults.baseURL = 'http://localhost:8080/elm/';
//  //将axios挂载到vue实例上，使用时就可以 this.$axios 这样使用了
// Vue.prototype.$axios = axios;
//  Vue.prototype.$qs = qs;
//  Vue.prototype.$getCurDate = getCurDate;
//  Vue.prototype.$setSessionStorage = setSessionStorage;
//  Vue.prototype.$getSessionStorage = getSessionStorage;
//  Vue.prototype.$removeSessionStorage = removeSessionStorage;
//  Vue.prototype.$setLocalStorage = setLocalStorage;
//  Vue.prototype.$getLocalStorage = getLocalStorage;
//  Vue.prototype.$removeLocalStorage = removeLocalStorage;
//  router.beforeEach(function(to,from,next){
//  let user = sessionStorage.getItem('user');
//  //除了登录、注册、首页、商家列表、商家信息之外，都需要判断是否登录
//  if(!(to.path=='/'||to.path=='/index'||to.path=='/businessList'||to.path=='/businessInfo'||to.path=='/login'||to.path=='/register')){
//     if(user==null){
//     router.push('/login');
//     location.reload();
//     }
// }
//     next();
//     });
//  new Vue({
//     router,
//     render: h => h(App)
// }).$mount('#app')
import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import qs from 'qs'
import './common.js'  // 需要调整为Vue3的全局属性挂载方式

const app = createApp(App)

// 配置axios
axios.defaults.baseURL = 'http://localhost:8080/elm/'

// 挂载全局属性
app.config.globalProperties.$axios = axios
app.config.globalProperties.$qs = qs

// 路由守卫
router.beforeEach((to, from, next) => {
  const user = sessionStorage.getItem('user')
  if (!['/', '/index', '/businessList', '/businessInfo', '/login', '/register'].includes(to.path)) {
    if (!user) {
      next('/login')
    }
  }
  next()
})

app.use(router)
app.mount('#app')