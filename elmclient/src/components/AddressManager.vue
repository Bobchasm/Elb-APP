<template>
  <div class="menu-section address-section">
    <div class="section-title">我的收货地址</div>
    <div class="menu-list">
      <div v-if="addresses.length === 0" class="empty-state">
        暂无收货地址，请添加。
      </div>
      <div v-for="address in addresses" :key="address.daId" class="address-item">
        <div class="address-details">
          <span class="contact-info">{{ address.contactName }} ({{ address.contactTel }})</span>
          <span class="full-address">{{ address.region }} {{ address.fullAddress }}</span>
        </div>
        <div class="address-actions">
          <i class="fas fa-edit edit-icon" @click="openAddressModal(address)"></i>
          <i class="fas fa-trash-alt delete-icon" @click="deleteAddress(address.daId)"></i>
        </div>
      </div>
      <div class="add-new-item" @click="openAddressModal()">
        <i class="fas fa-plus-circle add-icon"></i>
        <span class="add-text">新增收货地址</span>
      </div>
    </div>

    <!-- 使用Teleport将模态框渲染到body根部 -->
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
            <label>省 / 市（州） / 区（县）</label>
            <input v-model="addressForm.region" placeholder="点击选择省市区" @click="openAddressPicker" readonly />
          </div>
          <div class="modal-item">
            <label>详细地址</label>
            <textarea v-model="addressForm.fullAddress" placeholder="输入详细地址"></textarea>
          </div>
          
          <div class="modal-buttons">
            <button @click="submitAddress">提交</button>
            <button @click="closeAddressModal">取消</button>
          </div>
        </div>
      </div>
    </Teleport>

    <Teleport to="body">
      <div v-if="showAddressPickerModal" class="modal-overlay modal-picker" @click.self="closeAddressPicker">
        <div class="address-modal">
          <div class="modal-header">
            <h4>选择省市区</h4>
            <span class="close-btn" @click="closeAddressPicker">×</span>
          </div>
          <div class="picker-container">
            <div class="picker-column">
              <div v-for="p in provinces" :key="p" class="picker-item" :class="{ selected: p === selectedProvince }" @click="selectProvince(p)">
                {{ p }}
              </div>
            </div>
            <div class="picker-column">
              <div v-for="c in cities" :key="c" class="picker-item" :class="{ selected: c === selectedCity }" @click="selectCity(c)">
                {{ c }}
              </div>
            </div>
            <div class="picker-column">
              <div v-for="d in districts" :key="d" class="picker-item" :class="{ selected: d === selectedDistrict }" @click="selectDistrict(d)">
                {{ d }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </Teleport>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { toast } from '../utils/toast';
import { useAddressPicker } from '../composables/useAddressPicker';

// 定义 props 和 emits
const props = defineProps({
  userId: String
});

const addresses = ref([]);
const showAddressModal = ref(false);
const isEditing = ref(false);
const addressForm = ref({
  daId: null,
  contactName: '',
  contactTel: '',
  contactSex: null,
  region: '',
  fullAddress: '',
  userId: props.userId
});

// 使用可组合函数 useAddressPicker
const {
  showAddressPickerModal,
  provinces,
  cities,
  districts,
  selectedProvince,
  selectedCity,
  selectedDistrict,
  openAddressPicker,
  closeAddressPicker,
  selectProvince,
  selectCity,
  selectDistrict
} = useAddressPicker(addressForm);


onMounted(() => {
  loadAddresses();
});

// 加载地址列表的方法
const loadAddresses = async () => {
  try {
    if (!props.userId) {
      toast.warning('用户未登录，无法获取地址列表');
      return;
    }
    
    // 模拟数据加载
    addresses.value = [
      { daId: 1, contactName: '张三', contactTel: '13812345678', contactSex: 1, region: '广东省 深圳市 南山区', fullAddress: '高新园科技南路1号' },
      { daId: 2, contactName: '李四', contactTel: '13987654321', contactSex: 2, region: '四川省 成都市 锦江区', fullAddress: '春熙路步行街88号' }
    ];

  } catch (error) {
    console.error('获取地址列表失败:', error);
    toast.error('获取地址列表失败，请重试！');
  }
};

const openAddressModal = (address = null) => {
  // 阻止背景滚动
  document.body.style.overflow = 'hidden';
  
  if (address) {
    isEditing.value = true;
    addressForm.value = { ...address };
    const parts = address.region.split(' ');
    // 在打开编辑窗口时，同步地址选择器的数据
    selectedProvince.value = parts[0] || '';
    selectedCity.value = parts[1] || '';
    selectedDistrict.value = parts[2] || '';
  } else {
    isEditing.value = false;
    addressForm.value = {
      daId: null,
      contactName: '',
      contactTel: '',
      contactSex: null,
      region: '',
      fullAddress: '',
      userId: props.userId
    };
    // 新增时清空选择器数据
    selectedProvince.value = '';
    selectedCity.value = '';
    selectedDistrict.value = '';
  }
  showAddressModal.value = true;
};

const closeAddressModal = () => {
  // 恢复背景滚动
  document.body.style.overflow = '';
  showAddressModal.value = false;
};

const submitAddress = async () => {
  if (!addressForm.value.contactName || !addressForm.value.contactTel || !addressForm.value.region || !addressForm.value.fullAddress) {
    toast.warning('请填写完整的地址信息！');
    return;
  }
  
  try {
    if (isEditing.value) {
      // 模拟API调用
      toast.success('地址修改成功！');
    } else {
      // 模拟API调用
      toast.success('地址添加成功！');
    }
    closeAddressModal();
    loadAddresses();
  } catch (error) {
    toast.error('操作失败，请重试！');
  }
};

const deleteAddress = async (id) => {
  if (confirm('确定要删除此地址吗？')) {
    try {
      // 模拟API调用
      toast.success('地址删除成功！');
      loadAddresses();
    } catch (error) {
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
.empty-state {
  text-align: center;
  padding: 20px;
  color: #7f8c8d;
  font-size: 0.9rem;
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

.modal-overlay.modal-picker {
  background-color: rgba(0, 0, 0, 0.6);
  z-index: 10001;
  align-items: flex-end;
}
.address-modal {
  background-color: #fff;
  width: 100%;
  max-width: 600px;
  border-top-left-radius: 16px;
  border-top-right-radius: 16px;
  padding: 20px;
  animation: slide-up 0.3s ease-out forwards;
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

@keyframes slide-up {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.address-modal .modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}
.address-modal .modal-header h4 {
  margin: 0;
  font-weight: 500;
  font-size: 1.2rem;
  color: #2c3e50;
}
.address-modal .modal-header .close-btn {
  font-size: 28px;
  font-weight: bold;
  color: #999;
  cursor: pointer;
  transition: color 0.3s;
}
.address-modal .modal-header .close-btn:hover {
  color: #333;
}
.picker-container {
  display: flex;
  justify-content: space-around;
  text-align: center;
  height: 250px;
  overflow: hidden;
}
.picker-column {
  flex: 1;
  height: 100%;
  overflow-y: scroll;
  -webkit-overflow-scrolling: touch;
  scrollbar-width: none;
}
.picker-column::-webkit-scrollbar {
  display: none;
}
.picker-item {
  padding: 12px 0;
  font-size: 16px;
  color: #666;
  transition: all 0.3s;
  cursor: pointer;
}
.picker-item:hover {
  background-color: #f5f5f5;
}
.picker-item.selected {
  color: #1e88e5;
  font-size: 18px;
  font-weight: bold;
  background-color: #e3f2fd;
}
</style>