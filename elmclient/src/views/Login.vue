<template>
	<div class="wrapper">
	  <!-- header部分 -->
	  <header>
		<p>用户登陆</p>
	  </header>
  
	  <!-- 表单部分 -->
      <ul class="form-box">
      <li>
        <div class="title">
          用户名：
        </div>
        <div class="content">
          <input type="text" v-model="userName" placeholder="用户名">
        </div>
      </li>
      <li>
        <div class="title">
          密码：
        </div>
        <div class="content">
          <input type="password" v-model="password" placeholder="密码">
        </div>
      </li>
	  <li style="justify-content: flex-end; padding-top: 2vw;">
        <label style="display: flex; align-items: center; font-size: 3vw; color: #666;">
          <input
            type="checkbox"
            v-model="rememberMe"
            style="width: 3vw; height: 3vw; margin-right: 1vw;"
          >
          记住我
        </label>
      </li>
    </ul>

    <div class="button-login">
      <button @click="login">用户登录</button>
    </div>
  
	  <!-- 底部菜单部分 -->

	</div>
  </template>
  
  <script>
  import { ref ,computed} from 'vue';
  import { useRouter } from 'vue-router';
  import Footer from '../components/Footer.vue';
  import request from '../utils/request';

  export default {
	name: 'Login',
	setup() {
		const userName = ref('');
	  const password = ref('');
	  const router = useRouter();
	  const rememberMe=ref(false);
	 // 回显记住的用户名（从localStorage获取） 
	  const savedUserName = computed(() => {
      return localStorage.getItem('savedUserName') || '';
    });

	  const setSessionStorage = (key, value) => {
      window.sessionStorage.setItem(key, JSON.stringify(value)); // 自定义会话存储函数
    };
    const login = async () => {
		console.log('执行了');
      // 1. 表单校验
      if (!userName.value.trim()) {
        alert('用户名不能为空！');
        return;
      }
      if (!password.value.trim()) {
        alert('密码不能为空！');
        return;
      }

      try {
        // 2. 调用登录接口：传 userName/password/rememberMe
        const res = await request.post('/api/auth', {
          username: userName.value.trim(),
          password: password.value.trim(),
          rememberMe: rememberMe.value
        });

        // 3. 解析后端返回
        if (!res) {
          alert('登录失败');
          return;
        }

        // 4. 获取 id_token
        const idToken = res?.id_token;
		console.log(idToken);
        if (!idToken) {
          alert('登录失败，未获取到token！');
          return;
        }


        // 5. 根据“记住我”状态存储 token
        const storage = rememberMe.value ? localStorage : sessionStorage;
        storage.setItem('token', idToken); // 存储 token（key 为 token）
		console.log(storage.getItem('token'));
		let userRes;

		// 获取用户信息
        try {
          userRes = await request.get('/api/user');
          if (userRes) {
            storage.setItem('userInfo', JSON.stringify(userRes));
          }
        } catch (error) {
          console.error('获取用户信息失败:', error);
        }
		console.log(storage.getItem('userInfo'));


        // 6. 记住用户名（仅勾选时存localStorage）
        if (rememberMe.value) {
          localStorage.setItem('savedUserName', userName.value.trim());
        } else {
          localStorage.removeItem('savedUserName'); // 未勾选则清除
        }
		let targetPath = '/index'; // 默认跳转首页
		if (userRes?.authorities && Array.isArray(userRes.authorities)) {
			console.log('x');
			console.log(userRes.authorities);
		// 检查权限数组中是否包含ADMIN权限
		const isAdmin = userRes.authorities.some(auth => auth.name === 'ADMIN');
		if (isAdmin) {
			targetPath = '/admin/home'; // 管理员跳转管理员首页
		}
		}
		router.push({ path: targetPath });
		
      } catch (error) {
        // 捕获网络错误或后端500等异常
        const errorMsg = error.response?.data?.message || '网络异常，登录失败！';
        alert(errorMsg);
        console.error('登录错误:', error);
      }
    };

  
	  return {
        userName,
		password,
		login,
		rememberMe,
		savedUserName
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
	
		 /****************** header部分 ******************/
		 .wrapper header {
		 	width: 100%;
		 	height: 12vw;
		 	background-color: #0097FF;
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
	
		 /****************** 表单部分 ******************/
		 .wrapper .form-box {
		 	width: 100%;
		 	margin-top: 12vw;
		 }
	
		 .wrapper .form-box li {
		 	box-sizing: border-box;
		 	padding: 4vw 3vw 0 3vw;
		 	display: flex;
		 	align-items: center;
		 }
	
		 .wrapper .form-box li .title {
		 	flex: 0 0 18vw;
		 	font-size: 3vw;
		 	font-weight: 700;
		 	color: #666;
		 }
	
		 .wrapper .form-box li .content {
		 	flex: 1;
		 }
	
		 .wrapper .form-box li .content input {
		 	border: none;
		 	outline: none;
		 	width: 100%;
		 	height: 4vw;
		 	font-size: 3vw;
		 }
	
		 .wrapper .button-login {
		 	width: 100%;
		 	box-sizing: border-box;
		 	padding: 4vw 3vw 0 3vw;
		 }
	
		 .wrapper .button-login button {
		 	width: 100%;
		 	height: 10vw;
		 	font-size: 3.8vw;
		 	font-weight: 700;
		 	color: #fff;
		 	background-color: #38CA73;
		 	border-radius: 4px;
		 	border: none;
		 	outline: none;
		 }
	
		 .wrapper .button-register {
		 	width: 100%;
		 	box-sizing: border-box;
		 	padding: 4vw 3vw 0 3vw;
		 }
	
		 .wrapper .button-register button {
		 	width: 100%;
		 	height: 10vw;
		 	font-size: 3.8vw;
		 	font-weight: 700;
		 	/*与上面登陆按钮不同的只有颜色、背景色、边框不同*/
		 	color: #666;
		 	background-color: #EEE;
		 	border: solid 1px #DDD;
		 	border-radius: 4px;
		 	border: none;
		 	outline: none;
		 }
	
		 /****************** 底部菜单部分 ******************/
		 .wrapper .footer {
		 	width: 100%;
		 	height: 14vw;
		 	border-top: solid 1px #DDD;
		 	background-color: #fff;
		 	position: fixed;
		 	left: 0;
		 	bottom: 0;
		 	display: flex;
		 	justify-content: space-around;
		 	align-items: center;
		 }
	
		 .wrapper .footer li {
		 	display: flex;
		 	flex-direction: column;
		 	justify-content: center;
		 	align-items: center;
		 	color: #999;
		 	user-select: none;
		 	cursor: pointer;
		 }
	
		 .wrapper .footer li p {
		 	font-size: 2.8vw;
		 }
	
		 .wrapper .footer li i {
		 	font-size: 5vw;
		 }
  
</style>