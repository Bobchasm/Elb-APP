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
        <label for="userName" class="form-item-title">用户名称：</label>
        <div class="form-item-content">
          <input id="userName" type="text" v-model="user.userName" placeholder="用户名称" />
        </div>
      </li>
      <!-- 手机号码 -->
      <li class="form-item">
        <label for="userId" class="form-item-title">手机号码：</label>
        <div class="form-item-content">
          <input id="userId" type="text" @blur="checkUserId" v-model="user.userId" placeholder="手机号码" />
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
        <label for="userEmail" class="form-item-title">邮箱：</label>
        <div class="form-item-content">
          <input id="userEmail" type="text" v-model="user.userEmail" placeholder="请输入邮箱" />
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
          <input type="radio" v-model="user.userSex" value="1" id="male" />
          <label for="male">男</label>
          <input type="radio" v-model="user.userSex" value="0" id="female" />
          <label for="female">女</label>
        </div>
      </li>
    </ul>

    <!-- 注册按钮 -->
    <div class="button-register">
      <button @click="register">注册</button>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

export default {
  name: 'Register',
  setup() {
    const router = useRouter();
    const user = reactive({
      userId: '',
      password: '',
      userName: '',
      userEmail: '',
      userSex: 1,
      userImg: ''
    });
    const confirmPassword = ref('');
    const avatar = ref(null); // 用于存储头像文件的 Base64 字符串

    // 校验手机号码是否已存在
    const checkUserId = () => {
      // 如果手机号码为空，不进行校验
      if (!user.userId) return;
      const reg = /^1[3456789]\d{9}$/;
      if (!reg.test(user.userId)) {
        alert('手机号码格式错误，请重新输入！');
        user.userId = ''; // 清空输入
        return;
      }

      axios.post('UserController/userIdExists', { userId: user.userId })
        .then(response => {
          if (response.data === 1) {
            user.userId = '';
            alert('此手机号码已存在！');
          }
        })
        .catch(error => {
          console.error(error);
        });
    };

    // 处理头像文件上传
    const handleFileUpload = (event) => {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onload = (e) => {
          avatar.value = e.target.result; // 保存 Base64 字符串
        };
        reader.readAsDataURL(file);
      } else {
        avatar.value = null;
      }
    };

    // 注册函数
    const register = () => {
      // 检查用户名
      if (!user.userName) {
        alert('用户名不能为空！');
        return;
      }
      if (user.userName.length > 8) {
        alert('用户名过长！');
        return;
      }

      // 检查密码
      const regex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
      if (!user.password || !regex.test(user.password)) {
        alert('密码格式错误，请确保包含至少一个大写字母、一个小写字母和一个数字，长度至少为8个字符。');
        return;
      }
      if (user.password !== confirmPassword.value) {
        alert('两次输入的密码不一致！');
        return;
      }
      
      // 检查邮箱
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!user.userEmail || !emailRegex.test(user.userEmail)) {
        alert('邮箱格式不正确！');
        return;
      }

      // 将头像 Base64 字符串赋值给 user.userImg
      if (avatar.value) {
        user.userImg = avatar.value;
      } else {
        alert('请上传头像！');
        return;
      }

      // 注册请求
      axios.post('UserController/saveUser', user)
        .then(response => {
          if (response.data > 0) {
            alert('注册成功！');
            router.push({ path: '/index' });
          } else {
            alert('注册失败！');
          }
        })
        .catch(error => {
          console.error(error);
        });
    };

    return {
      user,
      confirmPassword,
      avatar,
      checkUserId,
      handleFileUpload,
      register
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

/****************** 表单部分 ******************/
.wrapper .form-box {
  width: 100%;
  margin-top: 12vw;
}

.wrapper .form-item {
  box-sizing: border-box;
  padding: 4vw 3vw 0 3vw;
  display: flex;
  align-items: center;
}

.wrapper .form-item-title {
  flex: 0 0 18vw;
  font-size: 3vw;
  font-weight: 700;
  color: #666;
}

.wrapper .form-item-content {
  flex: 1;
}

.wrapper .form-item-content input[type='text'],
.wrapper .form-item-content input[type='password'] {
  border: none;
  outline: none;
  width: 100%;
  height: 4vw;
  font-size: 3vw;
  padding-left: 2vw;
}

.wrapper .form-item-content input[type='file'] {
  width: 100%;
  height: 4vw;
  font-size: 3vw;
  border: none;
  outline: none;
  color: #666;
}

.wrapper .form-item.gender-item .form-item-content input[type='radio'] {
  width: 6vw;
  height: 3.2vw;
  vertical-align: middle;
}

.wrapper .form-item.gender-item .form-item-content label {
  font-size: 3vw;
  color: #666;
  margin-right: 4vw;
}

.wrapper .avatar-upload-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2vw;
}

.wrapper .avatar-preview {
  width: 15vw;
  height: 15vw;
  border-radius: 50%;
  overflow: hidden;
  border: 2px solid #ddd;
}

.wrapper .avatar-preview img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

/****************** 注册按钮部分 ******************/
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
  color: #fff;
  background-color: #38ca73;
  border-radius: 4px;
  border: none;
  outline: none;
}
</style>
