<template>
  <div class="wrapper">
    <div class="my-information">
      <div class="header">
        <h1>个人信息</h1>
      </div>
      <div class="content">
        <div class="user-info-container">
          <div class="details">
            <!-- <p class="nickname" @click="editNickname">昵称: {{ user?.userName }}</p> -->
            <p class="nickname">昵称: {{ user?.userName }}</p>
            <p class="phone">电话: {{ user?.userId }}</p>
            <p class="gender">性别: {{ user?.userSex === 1 ? '男' : '女' }}</p>
          </div>
          <div class="avatar" >
            <img :src="user2?.userImg || require('@/assets/default-avatar.png')" alt="用户头像" />
          </div>
        </div>
        <div class="actions">
          <div class="edit-nickname" v-if="showEditNickname">
            <input v-model="newNickname" placeholder="输入新昵称" />
            <button @click="submitNickname">提交</button>
          </div>
          <div class="ep" v-if="showEditPassword">
            <div class="edit-password">
              <input type="password" v-model="oldPassword" placeholder="输入旧密码" />
              <input type="password" v-model="newPassword" placeholder="输入新密码" />
            </div>
            <button @click="submitPassword">提交</button>
          </div>
          <input type="file" ref="fileInput" @change="uploadAvatar" accept="image/*" style="display:none;" />
          <div class="main-buttons">
            <!-- <button class="btn-red" @click="editpasswd">修改密码</button>
            <button class="btn-orange" @click="myfavorite">收藏列表</button>
            <button class="btn-yellow" @click="goToLikesList">点赞列表</button>
            <button class="btn-blue" @click="goToCommentsList">评论列表</button> -->
            <button class="btn-purple" @click="logout">退出登录</button>
          </div>
        </div>
      </div>
    </div>
    <Footer />
  </div>
</template>

<script>
import { ref, onMounted, onBeforeMount } from 'vue';
import Footer from '../components/Footer.vue';
import axios from 'axios';
import { useRouter } from 'vue-router';
import { toast } from '../utils/toast';

export default {
  name: 'MyInformation',
  setup() {
    const router = useRouter();
    const user = ref();
    const user2 = ref();
    const showEditNickname = ref(false);
    const newNickname = ref('');
    const showEditPassword = ref(false);
    const newPassword = ref('');
    const oldPassword = ref('');
    // const fileInput = ref(null);

    // const showUpload = () => {
    //   fileInput.value.click();
    // };

    // const uploadAvatar = async (event) => {
    //   const file = event.target.files[0];
    //   if (file) {
    //     console.log('选择的文件:', file);
    //     console.log('文件类型:', file.type);
    //     console.log('文件大小:', file.size);

    //     if (!file.type.includes('image')) {
    //       toast.warning('请选择图片文件！');
    //       event.target.value = '';
    //       return;
    //     }
    //     if (file.size > 10 * 1024 * 1024) {
    //       toast.warning('图片大小不能超过10MB！');
    //       event.target.value = '';
    //       return;
    //     }

    //     try {
    //       const reader = new FileReader();
    //       reader.onload = async (e) => {
    //         const base64String = e.target.result;
    //         console.log('图片转base64长度:', base64String.length);

    //         try {
    //           console.log('准备发送的数据:', {
    //             userId: user.value.userId,
    //             userImg: base64String.substring(0, 100) + '...'
    //           });

    //           const response = await axios.post('UserController/changeUserAvatar', {
    //             userId: user.value.userId,
    //             userImg: base64String
    //           });

    //           console.log('服务器响应:', response);

    //           if (response.data.code === 1) {
    //             user.value.userImg = base64String;
    //             if (user2.value) {
    //               user2.value.userImg = base64String;
    //             }
    //             sessionStorage.setItem('user', JSON.stringify(user.value));
    //             toast.success('头像修改成功！');
    //           } else {
    //             console.error('服务器返回错误:', response.data);
    //             toast.error(response.data.msg || '头像修改失败，请重试！');
    //           }
    //         } catch (error) {
    //           console.error('修改头像请求失败:', error);
    //           console.error('错误详情:', error.response?.data || error.message);
    //           toast.error('头像上传失败，请重试！');
    //         }
    //       };

    //       reader.onerror = (error) => {
    //         console.error('文件读取错误:', error);
    //         toast.error('读取文件失败，请重试！');
    //       };

    //       reader.readAsDataURL(file);
    //     } catch (error) {
    //       console.error('文件处理错误:', error);
    //       toast.error('处理文件失败，请重试！');
    //     }
    //   }
    //   event.target.value = '';
    // };

    onBeforeMount(async () => {
      user.value = sessionStorage.getItem('user') ? JSON.parse(sessionStorage.getItem('user')) : null;
      if (!user.value) {
        toast.warning('用户未登录，请先登录！');
        router.push({ path: '/login' });
        return;
      }

      try {
        const response = await axios.post('UserController/getUserByIdByPass', {
            userId: user.value.userId,
            password: user.value.password
        });
        
        if (response.data) {
          user2.value = response.data;
          if (response.data.userImg) {
            user.value.userImg = response.data.userImg;
            sessionStorage.setItem('user', JSON.stringify(user.value));
          }
        }
      } catch (error) {
        console.error('获取用户信息失败:', error);
        toast.error('获取用户信息失败，请重试！');
      }
    });

    const logout = () => {
      sessionStorage.removeItem('user');
      router.push({ path: '/index' });
    };

    const editNickname = () => {
      showEditNickname.value = true;
    };

    const submitNickname = () => {
      if (newNickname.value.trim() === '') {
        toast.warning('昵称不能为空！');
        return;
      }
      if (newNickname.value.length > 8) {
        toast.warning('昵称不能超过8个字符！');
        return;
      }
      axios
        .post('UserController/changeUserName', {
          userId: user.value.userId,
          userName: newNickname.value,
        })
        .then((response) => {
          if (response.data === 1) {
            user.value.userName = newNickname.value;
            sessionStorage.setItem('user', JSON.stringify(user.value));
            toast.success('昵称修改成功！');
            showEditNickname.value = false;
            newNickname.value = '';
          } else {
            toast.error('昵称修改失败！');
          }
        })
        .catch((error) => {
          console.error(error);
          toast.error('昵称修改失败！');
        });
    };

    const editpasswd = () => {
      showEditPassword.value = true;
    };

    const submitPassword = () => {
      if (oldPassword.value.trim() === '') {
        toast.warning('旧密码不能为空！');
        return;
      }
      if (newPassword.value.trim() === '') {
        toast.warning('新密码不能为空！');
        return;
      }

      axios
        .post('UserController/changeUserPassword', {
          userId: user.value.userId,
          oldPassword: oldPassword.value,
          newPassword: newPassword.value,
        })
        .then((response) => {
          if (response.data === 1) {
            toast.success('密码修改成功！');
            showEditPassword.value = false;
            oldPassword.value = '';
            newPassword.value = '';
          } else {
            toast.error('密码修改失败！');
          }
        })
        .catch((error) => {
          console.error(error);
          toast.error('密码修改失败！');
        });
    };

    const myfavorite = () => {
      router.push({ path: '/favorites' });
    };

    const goToLikesList = () => {
      router.push({ path: '/likes' });
    };

    const goToCommentsList = () => {
      router.push({ path: '/comments' });
    };

    return {
      user,
      user2,
      logout,
      editNickname,
      submitNickname,
      showEditNickname,
      newNickname,
      editpasswd,
      submitPassword,
      showEditPassword,
      newPassword,
      oldPassword,
      // showUpload,
      // uploadAvatar,
      // fileInput,
      myfavorite,
      goToLikesList,
      goToCommentsList
    };
  },
  components: {
    Footer,
  },
};
</script>

<style scoped>
.wrapper {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  padding-bottom: 8vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e8eb 100%);
}

.my-information {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
}

.header {
  width: 100%;
  height: 12vw;
  max-height: 80px;
  background: linear-gradient(to right, #3a7bd5, #00d2ff);
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  position: relative;
  overflow: hidden;
}

.header::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.2) 0%, rgba(255,255,255,0) 70%);
  transform: rotate(30deg);
  animation: shine 6s infinite linear;
}

@keyframes shine {
  0% { transform: rotate(30deg) translate(-10%, -10%); }
  100% { transform: rotate(30deg) translate(10%, 10%); }
}

.header h1 {
  color: white;
  font-size: 5vw;
  /* max-font-size: 24px; */
  margin: 0;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
  letter-spacing: 1px;
}

.content {
  width: 92%;
  max-width: 600px;
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  background-color: #ffffff;
  border-radius: 16px;
  margin-top: 20px;
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  padding: 20px 0;
  position: relative;
  overflow: hidden;
}

.content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 6px;
  background: linear-gradient(to right, #3a7bd5, #00d2ff);
}

.user-info-container {
  width: 90%;
  max-width: 500px;
  display: flex;
  align-items: center;
  gap: 20px;
  margin: 20px auto;
}

.avatar {
  /* position: relative;
  cursor: pointer; */
  width: 25vw;
  height: 25vw;
  max-width: 120px;
  max-height: 120px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid white;
  box-shadow: 0 6px 20px rgba(0, 151, 255, 0.3);
  flex-shrink: 0;
  /* transition: all 0.3s ease;
  z-index: 1; */
}

/* .avatar:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 25px rgba(0, 151, 255, 0.4);
} */

.avatar img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.avatar-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: rgba(0, 0, 0, 0.5);
  color: white;
  opacity: 0;
  transition: opacity 0.3s ease;
  border-radius: 50%;
}

.avatar:hover .avatar-overlay {
  opacity: 1;
}

/* .avatar-overlay span {
  font-size: 3vw;
  max-font-size: 14px;
  text-align: center;
  padding: 2vw;
  font-weight: 500;
} */

.details {
  flex: 1;
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #e9ecef;
}

.nickname {
  /* cursor: pointer; */
  position: relative;
  display: flex;
  align-items: center;
  padding: 12px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  margin-bottom: 10px;
  font-size: 4vw;
  /* max-font-size: 18px; */
  font-weight: 500;
  color: #333;
  transition: all 0.2s ease;
}

/* .nickname:hover {
  background-color: #f1f8ff;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
} */

/* .nickname::after {
  content: '✏️ 点击修改';
  margin-left: auto;
  color: #666;
  font-size: 3vw;
  max-font-size: 14px;
  padding-left: 10px;
  opacity: 0.8;
} */

.phone,
.gender {
  font-size: 3.8vw;
  /* max-font-size: 16px; */
  margin: 8px 0;
  color: #495057;
  display: flex;
  align-items: center;
  padding: 12px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  font-weight: 500;
}

.actions {
  width: 90%;
  max-width: 500px;
  margin: 20px auto;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.main-buttons {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 12px;
  width: 100%;
}

.main-buttons button {
  padding: 12px;
  border: none;
  border-radius: 10px;
  cursor: pointer;
  color: white;
  font-size: 3.8vw;
  /* max-font-size: 16px; */
  text-align: center;
  font-weight: 500;
  transition: all 0.3s ease;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}

.main-buttons button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.15);
}

.main-buttons button:active {
  transform: translateY(0);
}

.btn-red {
  background: linear-gradient(135deg, #ff5e62, #ff2400);
}

.btn-orange {
  background: linear-gradient(135deg, #ff9966, #ff5e62);
}

.btn-yellow {
  background: linear-gradient(135deg, #00b09b, #96c93d);
}

.btn-blue {
  background: linear-gradient(135deg, #4b6cb7, #182848);
}

.btn-purple {
  background: linear-gradient(135deg, #8e2de2, #4a00e0);
}

.edit-nickname,
.edit-password,
.ep {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 12px;
  margin: 10px 0;
  background-color: #f8f9fa;
  padding: 15px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  border: 1px solid #e9ecef;
}

.edit-nickname input,
.edit-password input {
  height: 45px;
  font-size: 16px;
  padding: 0 15px;
  border: 1px solid #ced4da;
  border-radius: 8px;
  background-color: white;
  transition: all 0.2s ease;
}

.edit-nickname input:focus,
.edit-password input:focus {
  border-color: #80bdff;
  box-shadow: 0 0 0 0.2rem rgba(0, 123, 255, 0.25);
  outline: none;
}

.edit-nickname button,
.ep button {
  margin-top: 10px;
  height: 45px;
  background: linear-gradient(135deg, #4b6cb7, #182848);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
}

.edit-nickname button:hover,
.ep button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

@media (max-width: 480px) {
  .main-buttons {
    grid-template-columns: 1fr;
  }
  
  .user-info-container {
    flex-direction: column;
    align-items: center;
    text-align: center;
  }
  
  .nickname::after {
    display: none;
  }
  
  .nickname,
  .phone,
  .gender {
    justify-content: center;
  }
}
</style>