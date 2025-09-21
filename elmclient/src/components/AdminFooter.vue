<template>
  <div class="admin-footer">
    <router-link to="/admin/home" class="nav-item">
      <i class="fas fa-home"></i>
      <span>首页</span>
    </router-link>

    <router-link to="/admin/users" class="nav-item">
      <i class="fas fa-users-cog"></i>
      <span>用户管理</span>
    </router-link>

    <router-link to="/admin/business" class="nav-item">
      <i class="fas fa-store-alt"></i>
      <span>商铺管理</span>
    </router-link>
  </div>
</template>

<script>
import { defineComponent } from 'vue';
import { useRouter } from 'vue-router';

export default defineComponent({
  name: 'AdminFooter',
  setup() {
    const router = useRouter();

    // 如果你未来需要用编程方式导航，可以使用以下方法
    // const goToUsers = () => {
    //   router.push('/admin/users');
    // };
    
    // 返回一个空对象或你需要的任何响应式数据
    return {};
  },
});
</script>

<style scoped>
.admin-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  width: 100%;
  max-width: 600px; /* 保持与你其他组件的样式一致 */
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  justify-content: space-around;
  align-items: center;
  height: 8vh;
  background-color: #fff;
  box-shadow: 0 -4px 12px rgba(0, 0, 0, 0.05);
  z-index: 1000;
  border-radius: 16px 16px 0 0;
}

.nav-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  flex: 1;
  color: #999;
  text-decoration: none;
  font-size: 12px;
  transition: color 0.3s ease;
}

.nav-item.router-link-exact-active {
  color: #3498db; /* 高亮选中项，颜色可以自定义 */
}

.nav-item i {
  font-size: 20px;
  margin-bottom: 4px;
}

.nav-item span {
  font-size: 12px;
}

@media (max-width: 480px) {
  .admin-footer {
    border-radius: 0;
  }
}
</style>