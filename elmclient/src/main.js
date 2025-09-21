import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import { toast } from './utils/toast';
//import { getToken,removeToken} from './utils/auth';

import 'font-awesome/css/font-awesome.min.css';
import axios from 'axios';
import qs from 'qs';
import {
  getCurDate,
  setSessionStorage,
  getSessionStorage,
  removeSessionStorage,
  setLocalStorage,  
  getLocalStorage,
  removeLocalStorage
} from './common.js';

// 设置 axios 的基础配置
// axios.defaults.baseURL = process.env.VITE_API_BASE_URL;
axios.defaults.baseURL = 'http://localhost:8080';
axios.defaults.timeout = 10000; // 设置超时时间为 10 秒
axios.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8';
axios.defaults.headers.common['Accept'] = 'application/json';

// 创建 axios 实例 用以模拟接口
// const api = axios.create({
//   baseURL: import.meta.env.VITE_API_BASE_URL || '/api', // 使用环境变量配置基础URL
//   timeout: 10000, // 超时时间
// })

// 请求拦截器
axios.interceptors.request.use(
  config => {
    //api
    // const token = getToken()
    // if (token) {
    //   config.headers.Authorization = `Bearer ${token}`
    // }
    // 在发送请求之前做些什么
    if (config.method === 'post') {
      // 如果是POST请求，确保数据是JSON格式
      if (typeof config.data === 'object') {
        config.data = JSON.stringify(config.data);
      }
    }
    return config;
  },
  error => { 
    // 对请求错误做些什么
    console.error('请求错误:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
axios.interceptors.response.use(
  response => {
    // 对响应数据做点什么
    return response;
  },
  async error => {
    // 对响应错误做点什么
    console.error('响应错误:', error);

    // ✅ 添加模拟接口的代码 - 就在这里添加
    // const url = error.config?.url || '';

    // const { status, data } = error.response || {}

    // 如果是 401 错误且尚未重试过
    // if (status === 401) {
    //   // Token 过期或无效
    //   removeToken()
    //   // 跳转到登录页
    //   window.location.href = '/login'
    // }
    
    // 模拟修改昵称接口
    //Tips:当响应错误发生时，才会进入这个拦截器的错误处理部分
    if (url.includes('changeUserName')) {
      console.log('🐛 [模拟] 修改昵称接口响应');
      return Promise.resolve({
        data: 1,
        status: 200,
        statusText: 'OK',
        config: error.config,
        headers: {}
      });
    }

    // 模拟更新个人信息接口
    // 因为前端请求的是 updateUserInfo 接口，而后端没有这个接口，所以会报 404 错误
    // 拦截 404 错误并返回一个成功的模拟响应
    if (url.includes('updateUserInfo')) {
        console.log('🐛 [模拟] 更新个人信息接口响应');
        return Promise.resolve({
            data: 1, // 假设 1 表示更新成功
            status: 200,
            statusText: 'OK',
            config: error.config,
            headers: {}
        });
    }

    //原有的错误处理保持不变
    if (error.response && error.response.status === 500) {
      console.error('服务器错误:', error.response.data);
    }
    return Promise.reject(error);
  }
);


// 创建 Vue 应用实例
const app = createApp(App);

// 将 axios 挂载到 Vue 实例上
app.config.globalProperties.$axios = axios;
app.config.globalProperties.$qs = qs;
//app.config.globalProperties.$api = api

app.config.globalProperties.$getCurDate = getCurDate;
app.config.globalProperties.$setSessionStorage = setSessionStorage;
app.config.globalProperties.$getSessionStorage = getSessionStorage;
app.config.globalProperties.$removeSessionStorage = removeSessionStorage;
app.config.globalProperties.$setLocalStorage = setLocalStorage;
app.config.globalProperties.$getLocalStorage = getLocalStorage;
app.config.globalProperties.$removeLocalStorage = removeLocalStorage;

// 注册全局 toast 服务
app.config.globalProperties.$toast = toast;

// 路由守卫
router.beforeEach((to, from, next) => {
  const businessUser = sessionStorage.getItem('businessUser') ? JSON.parse(sessionStorage.getItem('businessUser')) : null;
  const user = sessionStorage.getItem('user');
  
  // 商家专属页面的路径
  const businessPaths = ['/businessView', '/businessInformation', '/submitItems'];
  
  // 如果是访问商家专属页面
  if (businessPaths.includes(to.path)) {
    if (!businessUser || !businessUser.isBusiness) {
      // 如果没有商家登录，重定向到首页
      return next('/index');
    }
  }
  
  // 普通用户页面的验证逻辑
  if (!(to.path === '/' || to.path === '/index' || to.path === '/businessList' || 
      to.path === '/businessInfo' || to.path === '/login' || to.path === '/register' || 
      to.path === '/lChoose' || to.path === '/rChoose' || to.path === '/businessLogin' || 
      to.path === '/businessRegister')) {
    if (user === null && !businessUser) {
      return next('/login');
    }
  }
  
  next();
});

// 使用 Vue Router
app.use(router);

// 挂载 Vue 应用
app.mount('#app');