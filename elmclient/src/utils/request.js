import axios from 'axios';

// 1. 创建 Axios 实例
const request = axios.create({
  baseURL: 'http://localhost:8080', // 这里填你的后端基础路径（如 http://localhost:8080），如果和前端同域可留空
  timeout: 5000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json' // 默认请求格式
  }
});

// 2. 请求拦截器：给非登录/注册请求加 Authorization 头
request.interceptors.request.use(
  (config) => {
    // 排除登录和注册接口（根据你的实际接口路径调整）
    const excludePaths = ['/api/auth', '/api/register'];
    if (!excludePaths.some(path => config.url.includes(path))) {
      // 从 localStorage/sessionStorage 获取 token
      const token = localStorage.getItem('token') || sessionStorage.getItem('token');
      if (token) {
        config.headers.Authorization = `Bearer ${token}`; // 拼接 Bearer 格式
      }
    }
    //【【------------ 为了上传图片，需要在请求头中添加 'Content-Type': 'application/x-www-form-urlencoded'，否则会报错。
    //------------------------------------------------------------
    // 处理 POST 请求（从 main.js 迁移）
    // if (config.method === 'post' && typeof config.data === 'object') {
    //     config.data = JSON.stringify(config.data);
    //   }
    if (config.url.includes('/upload')) {
      // 移除默认的 Content-Type，让浏览器自动设置
      delete config.headers['Content-Type'];
    } else if (config.method === 'post' && typeof config.data === 'object') {
      // 其他 POST 请求处理
      config.data = JSON.stringify(config.data);
    }
    //【【------------ 为了上传图片，需要在请求头中添加 'Content-Type': 'application/x-www-form-urlencoded'，否则会报错。
    //------------------------------------------------------------】】
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 3. 响应拦截器：统一处理错误（如 token 过期）
request.interceptors.response.use(
  (response) => {
    return response.data; // 直接返回响应体，简化后续处理
  },
  (error) => {
//     // 示例：token 过期时跳转登录页
//     if (error.response?.status === 401) {
//       // 清除存储的 token 和用户信息
//       localStorage.removeItem('token');
//       sessionStorage.removeItem('token');
//       localStorage.removeItem('userInfo');
//       sessionStorage.removeItem('userInfo');
//       // 跳转登录页（需确保 router 已全局引入或通过其他方式获取）
//       window.location.href = '/login';
//     }
//     return Promise.reject(error);
//   }
// 迁移 main.js 中的模拟接口逻辑
const url = error.config?.url || '';
    
// 模拟修改昵称接口
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
if (url.includes('updateUserInfo')) {
  console.log('🐛 [模拟] 更新个人信息接口响应');
  return Promise.resolve({
    data: 1,
    status: 200,
    statusText: 'OK',
    config: error.config,
    headers: {}
  });
}

// 原有错误处理
if (error.response && error.response.status === 500) {
  console.error('服务器错误:', error.response.data);
}
return Promise.reject(error);
}
);

export default request;