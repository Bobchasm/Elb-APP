<template>
  <div class="menu-section address-section">
    <div class="section-title">我的收货地址</div>
    <div class="menu-list">
      <div v-if="addresses.length === 0 && !isLoading" class="empty-state">
        暂无收货地址，请添加。
      </div>
      <div v-if="isLoading" class="loading-state">
        <i class="fas fa-spinner fa-spin"></i> 正在加载...
      </div>
      <div v-for="address in addresses" :key="address.id" class="address-item">
        <div class="address-details">
          <span class="contact-info">{{ address.contactName }} ({{ address.contactTel }})</span>
          <span class="full-address">{{ address.address }}</span>
        </div>
        <div class="address-actions">
          <i class="fas fa-edit edit-icon" @click="openAddressModal(address)"></i>
          <i class="fas fa-trash-alt delete-icon" @click="deleteAddress(address.id)"></i>
        </div>
      </div>
      <div class="add-new-item" @click="openAddressModal()">
        <i class="fas fa-plus-circle add-icon"></i>
        <span class="add-text">新增收货地址</span>
      </div>
    </div>

    <Teleport to="body">
      <div v-if="showAddressModal" class="modal-overlay" @click.self="closeAddressModal">
        <div class="modal-content">
          <h3>{{ isEditing ? '编辑地址' : '新增地址' }}</h3>
          <div class="modal-item">
            <label>收货人</label>
            <input v-model="addressForm.contactName" placeholder="输入收货人姓名" />
          </div>
          <div class="modal-item">
            <label>手机号</label>
            <input v-model="addressForm.contactTel" placeholder="输入手机号" />
          </div>
          <div class="modal-item">
            <label>性别</label>
            <input v-model="addressForm.contactSex" placeholder="1男/2女" />
          </div>
          <div class="modal-item">
            <label>详细地址</label>
            <textarea v-model="addressForm.address" placeholder="输入详细地址"></textarea>
          </div>

          <div class="modal-buttons">
            <button @click="submitAddress">提交</button>
            <button @click="closeAddressModal">取消</button>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { toast } from '../utils/toast';
// 导入你指定的 request 工具
import request from '../utils/request';

const props = defineProps({
  userId: String
});

const addresses = ref([]);
const isLoading = ref(false);
const showAddressModal = ref(false);
const isEditing = ref(false);
const addressForm = ref({
  id: null,
  contactName: '',
  contactTel: '',
  contactSex: null,
  address: '',
  userId: props.userId
});

onMounted(() => {
  loadAddresses();
});

// 加载地址列表的方法
const loadAddresses = async () => {
  if (!props.userId) {
    toast.warning('用户未登录，无法获取地址列表');
    return;
  }

  isLoading.value = true;
  try {
    // 使用 request.get 方法来发送 GET 请求
    // 假设你的 request 工具支持 params 选项
    const response = await request.get('/api/addresses', { params: { userId: props.userId } });
    addresses.value = response.data;
    // 如果接口返回的数据中包含了省市区和详细地址的组合，这里不需要再进行处理
    // addresses.value = addresses.value.map(addr => ({
    //   ...addr,
    //   address: `${addr.address}`
    // }));
  } catch (error) {
    console.error('获取地址列表失败:', error);
    toast.error('获取地址列表失败，请重试！');
  } finally {
    isLoading.value = false;
  }
};

const openAddressModal = (address = null) => {
  document.body.style.overflow = 'hidden';

  if (address) {
    isEditing.value = true;
    addressForm.value = { ...address };
  } else {
    isEditing.value = false;
    addressForm.value = {
      id: null,
      contactName: '',
      contactTel: '',
      contactSex: null,
      address: '',
      userId: props.userId
    };
  }
  showAddressModal.value = true;
};

const closeAddressModal = () => {
  document.body.style.overflow = '';
  showAddressModal.value = false;
};

const submitAddress = async () => {
  const form = addressForm.value;
  if (!form.contactName || !form.contactTel || !form.address) {
    toast.warning('请填写完整的地址信息！');
    return;
  }

  try {
    if (isEditing.value) {
      // 使用 request.post 方法发送 POST 请求
      await request.post('/api/addresses/updateDeliveryAddress', { ...form });
      toast.success('地址修改成功！');
    } else {
      await request.post('/api/address', {
        contactName: form.contactName,
        contactTel: form.contactTel,
        contactSex: form.contactSex,
        address: form.address,
        customer: { id: form.userId }
      });
      toast.success('地址添加成功！');
    }
    closeAddressModal();
    loadAddresses();
  } catch (error) {
    console.error('操作失败:', error);
    toast.error('操作失败，请重试！');
  }
};

const deleteAddress = async (id) => {
  if (confirm('确定要删除此地址吗？')) {
    try {
      // 使用 request.post 方法发送 POST 请求
      await request.post('/api/addresses/removeDeliveryAddress', null, { params: { id: id } });
      toast.success('地址删除成功！');
      loadAddresses();
    } catch (error) {
      console.error('删除失败:', error);
      toast.error('删除失败，请重试！');
    }
  }
};
</script>

<style scoped>
.menu-section {
  width: 92%;
  max-width: 500px;
  margin: 20px auto;
  transform: translateY(-50px);
}
.section-title {
  font-size: 1.1rem;
  color: #2c3e50;
  margin-bottom: 15px;
  padding-left: 10px;
  font-weight: 600;
  border-left: 4px solid #3498db;
}
.menu-list {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}
.address-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
}
.address-item:hover {
  background-color: #f1f8ff;
  transform: translateY(-1px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.08);
}
.address-item:last-child {
  border-bottom: none;
}
.address-details {
  display: flex;
  flex-direction: column;
  flex: 1;
}
.contact-info {
  font-size: 1rem;
  font-weight: 600;
  color: #34495e;
}
.full-address {
  font-size: 0.9rem;
  color: #7f8c8d;
  margin-top: 5px;
}
.address-actions {
  display: flex;
  gap: 15px;
  margin-left: 20px;
}
.address-actions .edit-icon, .address-actions .delete-icon {
  color: #3498db;
  font-size: 1.1rem;
}
.address-actions .delete-icon {
  color: #e74c3c;
}
.add-new-item {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px 20px;
  cursor: pointer;
  background: #f8f9fa;
  border-bottom-left-radius: 16px;
  border-bottom-right-radius: 16px;
  transition: background-color 0.3s ease;
}
.add-new-item:hover {
  background-color: #e9ecef;
}
.add-icon {
  color: #3498db;
  margin-right: 10px;
  font-size: 1.2rem;
}
.add-text {
  font-size: 1rem;
  color: #3498db;
}
.empty-state, .loading-state {
  text-align: center;
  padding: 20px;
  color: #7f8c8d;
  font-size: 0.9rem;
}
.loading-state .fa-spinner {
  margin-right: 5px;
}
</style>

<style>
/* 全局样式，不scoped */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10000;
  backdrop-filter: blur(3px);
}

.modal-content {
  background: white;
  padding: 25px;
  border-radius: 16px;
  max-width: 420px;
  width: 85%;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  animation: modalSlideIn 0.3s ease-out;
}

.modal-content h3 {
  margin-top: 0;
  color: #2c3e50;
  margin-bottom: 20px;
  font-size: 1.3rem;
}
.modal-item {
  margin-bottom: 15px;
  text-align: left;
}
.modal-item label {
  display: block;
  font-weight: 500;
  color: #555;
  margin-bottom: 5px;
  font-size: 0.95rem;
}
.modal-content input, .modal-content textarea {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  font-size: 16px;
  box-sizing: border-box;
  transition: border-color 0.3s;
}
.modal-content input:focus, .modal-content textarea:focus {
  outline: none;
  border-color: #3498db;
  box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2);
}
.modal-buttons {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}
.modal-buttons button {
  padding: 10px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.3s;
}
.modal-buttons button:first-child {
  background: #3498db;
  color: white;
}
.modal-buttons button:first-child:hover {
  background: #2980b9;
  transform: translateY(-1px);
}
.modal-buttons button:last-child {
  background: #e0e0e0;
  color: #333;
}
.modal-buttons button:last-child:hover {
  background: #c7c7c7;
  transform: translateY(-1px);
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: scale(0.9) translateY(-20px);
  }
  to {
    opacity: 1;
    transform: scale(1) translateY(0);
  }
}
</style>