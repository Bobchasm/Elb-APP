<template>
  <div class="wrapper">
    <!-- header部分 -->
    <header>
      <p>用户注册</p>
    </header>

    <!-- 表单部分 -->
    <ul class="form-box">
      <!-- 用户名 -->
      <li class="form-item">
        <label for="username" class="form-item-title">用户名称：</label>
        <div class="form-item-content">
          <input
            id="username"
            type="text"
            v-model="user.username"
            placeholder="用户名称"
          />
        </div>
      </li>
      <li class="form-item">
        <label for="firstname" class="form-item-title">姓：</label>
        <div class="form-item-content">
          <input
            id="firstname"
            type="text"
            v-model="user.firstname"
            placeholder="姓"
          />
        </div>
      </li>
      <li class="form-item">
        <label for="lastname" class="form-item-title">名：</label>
        <div class="form-item-content">
          <input
            id="lastname"
            type="text"
            v-model="user.lastname"
            placeholder="名"
          />
        </div>
      </li>
      <!-- 手机号码 -->
      <li class="form-item">
        <label for="phone" class="form-item-title">手机号码：</label>
        <div class="form-item-content">
          <input id="phone" type="text" v-model="user.phone" placeholder="手机号码" />
        </div>
      </li>
      <!-- 密码 -->
      <li class="form-item">
        <label for="password" class="form-item-title">密码：</label>
        <div class="form-item-content">
          <input id="password" type="password" v-model="user.password" placeholder="密码" />
        </div>
      </li>
      <!-- 确认密码 -->
      <li class="form-item">
        <label for="confirmPassword" class="form-item-title">确认密码：</label>
        <div class="form-item-content">
          <input id="confirmPassword" type="password" v-model="confirmPassword" placeholder="确认密码" />
        </div>
      </li>
      <!-- 邮箱 -->
      <li class="form-item">
        <label for="useremail" class="form-item-title">邮箱：</label>
        <div class="form-item-content">
          <input id="useremail" type="text" v-model="user.useremail" placeholder="请输入邮箱" />
        </div>
      </li>
      <!-- 头像 -->
      <li class="form-item avatar-item">
        <label for="avatarFile" class="form-item-title">头像：</label>
        <div class="form-item-content avatar-upload-container">
          <input id="avatarFile" type="file" @change="handleFileUpload" />
          <div v-if="avatar" class="avatar-preview">
            <img :src="avatar" alt="用户头像预览" />
          </div>
        </div>
      </li>
      <!-- 性别 -->
      <li class="form-item gender-item">
        <div class="form-item-title">性别：</div>
        <div class="form-item-content">
          <input type="radio" v-model="user.usersex" value="1" id="male" />
          <label for="male">男</label>
          <input type="radio" v-model="user.usersex" value="0" id="female" />
          <label for="female">女</label>
        </div>
      </li>
    </ul>

    <!-- 注册按钮 -->
    <div class="button-register">
      <button @click="register">注册</button>
    </div>

    <!-- 自定义消息框 -->
    <div v-if="messageBoxVisible" class="message-box-overlay">
      <div class="message-box">
        <p>{{ messageBoxMessage }}</p>
        <button @click="closeMessageBox">确定</button>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import request from '../utils/request';

export default {
  name: 'Register',
  setup() {
    const router = useRouter();
    const user = reactive({
      phone: '',
      password: '',
      username: '',
      useremail: '',
      usersex: 0,
      firstname: '',
      lastname: '',
      photo: ''
    });
    const confirmPassword = ref('');
    const avatar = ref(null);

    // 消息框状态
    const messageBoxVisible = ref(false);
    const messageBoxMessage = ref('');

    // 显示消息框
    const showMessageBox = (message) => {
      messageBoxMessage.value = message;
      messageBoxVisible.value = true;
    };

    // 关闭消息框
    const closeMessageBox = () => {
      messageBoxVisible.value = false;
    };

    // 处理头像文件上传
    const handleFileUpload = async (event) => {
      const file = event.target.files[0];
      if (file) {
        // 1. 先预览头像
        const reader = new FileReader();
        reader.onload = (e) => {
          avatar.value = e.target.result; // 显示本地预览
        };
        reader.readAsDataURL(file);

        // 2. 使用request请求实例上传到后端
        try {
          // 创建FormData对象，用于文件上传
          const formData = new FormData();
          formData.append('file', file); // 键名'file'与后端MultipartFile参数名保持一致

          // 发送POST请求到后端接口（使用项目中的request实例）
          const result = await request.post('/upload', formData);

          // 处理响应（假设后端返回格式为{success: boolean, data: string}）
          if (result.success) {
            // 将后端返回的URL赋值给user.photo
            user.photo = result.data;
            console.log('头像上传成功，URL已保存:', user.photo);
          } else {
            throw new Error(result.message || '上传失败，后端返回异常');
          }
        } catch (error) {
          console.error('头像上传出错:', error.message || '网络请求失败');
          // 错误提示（可替换为项目中的提示组件）
          showMessageBox('头像上传失败，请重试');
        }
      } else {
        // 未选择文件时清空
        avatar.value = null;
        user.photo = null;
      }
    };

    // 注册函数，包含所有校验和注册请求
    const register = () => {
      // 客户端校验
      user.username = user.username.trim();
      if (!user.username) {
        showMessageBox('用户名不能为空！');
        return;
      }
      if (!user.firstname) {
        showMessageBox('姓不能为空！');
        return;
      }
      if (!user.lastname) {
        showMessageBox('名不能为空！');
        return;
      }
      if (user.username.length > 8) {
        showMessageBox('用户名过长！');
        return;
      }

      const passwordRegex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
      if (!user.password || !passwordRegex.test(user.password)) {
        showMessageBox('密码格式错误，请确保包含至少一个大写字母、一个小写字母和一个数字，长度至少为8个字符。');
        return;
      }
      if (user.password !== confirmPassword.value) {
        showMessageBox('两次输入的密码不一致！');
        return;
      }
      
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!user.useremail || !emailRegex.test(user.useremail)) {
        showMessageBox('邮箱格式不正确！');
        return;
      }

      // 检查头像是否已上传成功
      if (!user.photo) {
        showMessageBox('请先上传头像！');
        return;
      }

      // 构建符合API规范的请求体
      const registerPayload = {
        username: user.username,
        password: user.password,
        phone: user.phone,
        email: user.useremail,
        firstName: user.firstname,
        lastName: user.lastname,
        photo: user.photo,
        gender: user.usersex
      };
      
      request.post('/api/register', registerPayload)
        .then(response => {
          console.log('注册请求成功，服务器响应数据:', response);
          
          // 检查服务器响应中的 success 字段
          if (response && response.success) {
            showMessageBox('注册成功！');
            // 注册成功后跳转
            setTimeout(() => {
              router.push({ path: '/index' });
            }, 1500); // 1.5秒后跳转
          } else {
            // success 为 false 或字段不存在的情况
            let errorMessage = '注册失败！服务器返回了无效数据。';
            if (response && response.message) {
              errorMessage = `注册失败！原因：${response.message}`;
            }
            showMessageBox(errorMessage);
          }
        })
        .catch(error => {
          console.error('注册请求发生错误:', error.response || error.message);
          
          if (error.response?.status === 409) { // 假设409是用户名冲突的错误码
            showMessageBox('此用户名已存在！');
          } else {
            showMessageBox('请求失败，请检查网络或服务器！');
          }
        });
    };

    return {
      user,
      confirmPassword,
      avatar,
      handleFileUpload,
      register,
      messageBoxVisible,
      messageBoxMessage,
      closeMessageBox
    };
  }
};
</script>

<style scoped>
/* -------------------- 基础样式重置 -------------------- */
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: 'Arial', sans-serif;
  background-color: #f0f2f5;
}

.wrapper {
  width: 100%;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #f0f2f5;
  padding-bottom: 20px;
}

/* -------------------- header部分 -------------------- */
header {
  width: 100%;
  height: 15vw;
  max-height: 80px;
  background-color: #0097FF;
  color: #fff;
  font-size: clamp(20px, 5vw, 24px);
  position: fixed;
  left: 0;
  top: 0;
  z-index: 1000;
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* -------------------- 表单部分 -------------------- */
.form-box {
  width: 90%;
  max-width: 400px;
  background: #fff;
  margin-top: 25vw;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  list-style: none;
}

.form-item {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.form-item-title {
  font-size: 16px;
  font-weight: 600;
  color: #444;
  flex-basis: 90px;
  flex-shrink: 0;
}

.form-item-content {
  flex: 1;
}

.form-item-content input[type='text'],
.form-item-content input[type='password'],
.form-item-content input[type='file'] {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px; /* 8px圆角 */
  font-size: 16px;
  color: #333;
  transition: border-color 0.3s;
}

.form-item-content input[type='text']:focus,
.form-item-content input[type='password']:focus,
.form-item-content input[type='file']:focus {
  outline: none;
  border-color: #0097FF;
}

.form-item.gender-item .form-item-content {
  display: flex;
  align-items: center;
}

.form-item.gender-item .form-item-content input[type='radio'] {
  width: 16px;
  height: 16px;
  margin-right: 6px;
  vertical-align: middle;
  cursor: pointer;
}

.form-item.gender-item .form-item-content label {
  font-size: 14px;
  color: #666;
  margin-right: 15px;
  cursor: pointer;
}

.avatar-upload-container {
  display: flex;
  align-items: center;
  gap: 15px;
}

.avatar-preview {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #ddd;
  flex-shrink: 0;
}

.avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/* -------------------- 注册按钮部分 -------------------- */
.button-register {
  width: 90%;
  max-width: 400px;
  margin-top: 20px;
}

.button-register button {
  width: 100%;
  height: 50px;
  font-size: 18px;
  font-weight: 700;
  color: #fff;
  background-color: #0097FF; /* 更改为蓝色 */
  border-radius: 8px; /* 8px圆角 */
  border: none;
  outline: none;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.1s, box-shadow 0.3s;
  box-shadow: 0 4px 12px rgba(0, 151, 255, 0.3);
}

.button-register button:hover {
  background-color: #007acc;
}

.button-register button:active {
  transform: translateY(1px);
}

/* -------------------- 自定义消息框 -------------------- */
.message-box-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

.message-box {
  background: #fff;
  padding: 24px;
  border-radius: 12px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  width: 80%;
  max-width: 350px;
}

.message-box p {
  margin-bottom: 20px;
  font-size: 16px;
  color: #333;
}

.message-box button {
  background: #0097ff;
  color: #fff;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 600;
}
</style>
