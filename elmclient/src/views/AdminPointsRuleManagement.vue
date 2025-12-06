<template>
  <div class="sub-page-container">
    <div class="top-background">
      <h1>积分使用规则</h1>
    </div>
    <div class="page-content">

    <div class="content-panel">
      <div class="toolbar">
        
        <div class="top-row">
          <div class="search-box">
            <i class="icon-search"></i>
            <input
              v-model="searchKeyword"
              type="text"
              placeholder="搜索规则名称..."
              @input="handleSearch"
              class="search-input"
            />
            <button v-if="searchKeyword" @click="clearSearch" class="clear-search">
              <i class="icon-clear"></i>
            </button>
          </div>
          
          <button class="primary-btn add-btn" @click="handleAddRule">
            <i class="icon-add"></i>新增规则
          </button>
        </div>
        
        <div class="filter-sort-row">
          
          <select v-model="ruleType" @change="handleFilterChange" class="filter-select">
            <option :value="undefined">全部类型</option>
            <option v-for="(typeName, typeValue) in ruleTypeMap" :key="typeValue" :value="Number(typeValue)">
              {{ typeName }}
            </option>
          </select>
          
          <select v-model="ruleStatusFilter" @change="handleStatusFilterChange" class="filter-select">
            <option :value="undefined">全部状态</option>
            <option v-for="(statusName, statusValue) in ruleStatusMap" :key="statusValue" :value="Number(statusValue)">
              {{ statusName }}
            </option>
          </select>
          
          <button class="sort-btn" @click="toggleSort">
            <i class="icon-sort"></i>
            {{ sortBy === 'createTime' ? '最新' : '优先级' }}
          </button>
        </div>
      </div>

      <div class="rule-list-wrapper">
        <div v-if="loading" class="loading-container">
          <div class="loading-spinner"></div>
          <p>加载中...</p>
        </div>
        
        <div v-else-if="filteredRuleList.length === 0" class="empty-state">
          <i class="icon-empty"></i>
          <p>{{ searchKeyword || ruleType !== undefined || ruleStatusFilter !== undefined ? '未找到相关规则' : '暂无积分规则' }}</p>
          <button v-if="!searchKeyword && ruleType === undefined && ruleStatusFilter === undefined" class="primary-btn" @click="handleAddRule">
            创建第一条规则
          </button>
        </div>
        
        <div v-else class="rule-list">
          <div
            v-for="rule in filteredRuleList"
            :key="rule.id"
            class="rule-item"
            @click="showRuleDetail(rule)"
          >
            <div class="rule-item-main">
              <div class="rule-info">
                <h3 class="rule-name">{{ rule.ruleName }}</h3>
                <div class="rule-meta">
                  <span class="rule-type" :class="`type-${rule.ruleType}`">
                    {{ rule.ruleTypeName }}
                  </span>
                  <span class="rule-priority">
                    <i class="icon-priority"></i>优先级: {{ rule.priority }}
                  </span>
                  <span v-if="rule.expireDays !== undefined && rule.expireDays > 0" class="rule-expire">
                    <i class="icon-expire"></i>有效期: {{ rule.expireDays }}天
                  </span>
                </div>
                <div class="rule-time">
                  <i class="icon-time"></i>
                  创建: {{ formatDate(rule.createTime) }}
                </div>
              </div>
              
              <div class="rule-status-area">
                <div :class="[
                  'rule-status',
                  rule.ruleStatus === 1 ? 'status-enabled' : 'status-disabled'
                ]">
                  {{ rule.ruleStatus === 1 ? '启用中' : '已禁用' }}
                </div>
                <div class="rule-points">
                  {{ formatPointsValue(rule) }}
                </div>
              </div>
            </div>
            
            <div class="rule-item-actions" @click.stop>
              <button @click="handleEdit(rule)" class="action-btn edit-btn">
                <i class="icon-edit"></i>
                <span>编辑</span>
              </button>
              <button @click="handleDelete(rule.id)" class="action-btn delete-btn">
                <i class="icon-delete"></i>
                <span>删除</span>
              </button>
            </div>
          </div>
        </div>
      </div>
      
      <div v-if="hasMore && !loading" class="load-more">
        <button @click="loadMore" class="load-more-btn">
          加载更多
        </button>
      </div>
    </div>

    <div v-if="showDetailModal" class="modal-overlay" @click.self="closeDetailModal">
      <div class="modal-content detail-modal">
        <div class="modal-header">
          <div class="modal-header-content">
            <h3 class="modal-title">规则详情</h3>
            <div :class="[
              'detail-status',
              selectedRule.ruleStatus === 1 ? 'status-enabled' : 'status-disabled'
            ]">
              {{ selectedRule.ruleStatus === 1 ? '启用中' : '已禁用' }}
            </div>
          </div>
          <button class="modal-close" @click="closeDetailModal">
            <i class="icon-close"></i>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="detail-content">
            <div class="detail-section">
              <h4 class="section-title">基本信息</h4>
              <div class="detail-grid">
                <div class="detail-item">
                  <span class="detail-label">规则名称</span>
                  <span class="detail-value">{{ selectedRule.ruleName }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">规则类型</span>
                  <span class="detail-value rule-type-badge" :class="`type-${selectedRule.ruleType}`">
                    {{ selectedRule.ruleTypeName }}
                  </span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">优先级</span>
                  <span class="detail-value">{{ selectedRule.priority }}</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">创建时间</span>
                  <span class="detail-value">{{ formatDateTime(selectedRule.createTime) }}</span>
                </div>
              </div>
            </div>
            
            <div class="detail-section">
              <h4 class="section-title">积分设置</h4>
              <div class="detail-grid">
                <div v-if="selectedRule.pointsRatio > 0" class="detail-item">
                  <span class="detail-label">积分比例</span>
                  <span class="detail-value">{{ selectedRule.pointsRatio }}倍</span>
                </div>
                <div v-if="selectedRule.pointsMultiplier > 0" class="detail-item">
                  <span class="detail-label">积分倍数</span>
                  <span class="detail-value">{{ selectedRule.pointsMultiplier }}倍</span>
                </div>
                <div v-if="selectedRule.pointsAmount > 0" class="detail-item">
                  <span class="detail-label">固定积分</span>
                  <span class="detail-value">{{ selectedRule.pointsAmount }}积分</span>
                </div>
                <div class="detail-item">
                  <span class="detail-label">有效期</span>
                  <span class="detail-value">
                    {{ selectedRule.expireDays > 0 ? selectedRule.expireDays + '天' : '永久有效' }}
                  </span>
                </div>
              </div>
            </div>
            
            <div v-if="selectedRule.startTime || selectedRule.endTime" class="detail-section">
              <h4 class="section-title">生效时间</h4>
              <div class="detail-grid full-width">
                <div class="detail-item">
                  <span class="detail-label">生效时间范围</span>
                  <span class="detail-value">
                    {{ formatRuleTimeRange(selectedRule.startTime, selectedRule.endTime) }}
                  </span>
                </div>
              </div>
            </div>
            
            <div class="detail-section">
              <h4 class="section-title">其他信息</h4>
              <div class="detail-grid">
                <div v-if="selectedRule.minOrderAmount" class="detail-item">
                  <span class="detail-label">最低订单金额</span>
                  <span class="detail-value">¥{{ selectedRule.minOrderAmount }}</span>
                </div>
                <div v-if="selectedRule.memberLevel" class="detail-item">
                  <span class="detail-label">适用会员等级</span>
                  <span class="detail-value">{{ selectedRule.memberLevel }}级</span>
                </div>
                <div v-if="selectedRule.behaviorType" class="detail-item">
                  <span class="detail-label">行为类型</span>
                  <span class="detail-value">{{ selectedRule.behaviorType }}</span>
                </div>
                <div v-if="selectedRule.updateTime" class="detail-item">
                  <span class="detail-label">最后更新</span>
                  <span class="detail-value">{{ formatDateTime(selectedRule.updateTime) }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="closeDetailModal">关闭</button>
          <div class="action-buttons">
            <button @click="handleEdit(selectedRule)" class="btn btn-edit">
              <i class="icon-edit"></i>编辑
            </button>
            <button @click="handleDelete(selectedRule.id)" class="btn btn-delete">
              <i class="icon-delete"></i>删除
            </button>
          </div>
        </div>
      </div>
    </div>

    <div v-if="isModalVisible" class="modal-overlay" @click.self="closeModal">
      <div class="modal-content rule-form-modal">
        <div class="modal-header">
          <h3>{{ modalTitle }}</h3>
          <button class="modal-close" @click="closeModal">
            <i class="icon-close"></i>
          </button>
        </div>
        
        <div class="modal-body">
          <form @submit.prevent="handleSaveRule" class="rule-form">
            <div class="form-group">
              <label for="ruleName">规则名称 <span class="required">*</span></label>
              <input
                id="ruleName"
                v-model="ruleForm.ruleName"
                type="text"
                required
                placeholder="请输入规则名称"
              />
            </div>

            <div class="form-row">
              <div class="form-group">
                <label for="ruleType">规则类型 <span class="required">*</span></label>
                <select id="ruleType" v-model="ruleForm.ruleType" required @change="handleRuleTypeChange">
                  <option v-for="(name, value) in ruleTypeMap" :key="value" :value="Number(value)">
                    {{ name }}
                  </option>
                </select>
              </div>

              <div class="form-group">
                <label for="ruleStatus">状态 <span class="required">*</span></label>
                <select id="ruleStatus" v-model="ruleForm.ruleStatus" required>
                  <option v-for="(name, value) in ruleStatusMap" :key="value" :value="Number(value)">
                    {{ name }}
                  </option>
                </select>
              </div>
            </div>

            <!-- 动态显示不同规则类型的特定字段 -->
            <template v-if="ruleForm.ruleType === 0">
              <div class="form-group">
                <label for="pointsRatio">积分比例</label>
                <input
                  id="pointsRatio"
                  v-model.number="ruleForm.pointsRatio"
                  type="number"
                  step="0.01"
                  min="0"
                  placeholder="例如：0.1 表示消费1元获得0.1积分"
                />
              </div>
            </template>

            <template v-else-if="ruleForm.ruleType === 1">
              <div class="form-group">
                <label for="pointsMultiplier">积分倍数</label>
                <input
                  id="pointsMultiplier"
                  v-model.number="ruleForm.pointsMultiplier"
                  type="number"
                  step="0.1"
                  min="1"
                  placeholder="例如：2.0 表示双倍积分"
                />
              </div>
            </template>

            <template v-else-if="ruleForm.ruleType === 3">
              <div class="form-group">
                <label for="behaviorType">行为类型</label>
                <select id="behaviorType" v-model="ruleForm.behaviorType">
                  <option value="like">点赞</option>
                  <option value="collect">收藏</option>
                  <option value="repay_loan">还贷款</option>
                </select>
              </div>
              <div class="form-group">
                <label for="pointsAmount">积分数量</label>
                <input
                  id="pointsAmount"
                  v-model.number="ruleForm.pointsAmount"
                  type="number"
                  min="1"
                  placeholder="请输入积分数量"
                />
              </div>
            </template>

            <div class="form-row">
              <div class="form-group">
                <label for="priority">优先级</label>
                <input
                  id="priority"
                  v-model.number="ruleForm.priority"
                  type="number"
                  min="1"
                  placeholder="数字越大优先级越高"
                />
              </div>

              <div class="form-group">
                <label for="expireDays">有效期(天)</label>
                <input
                  id="expireDays"
                  v-model.number="ruleForm.expireDays"
                  type="number"
                  min="0"
                  placeholder="0表示永久有效"
                />
              </div>
            </div>

            <div class="form-group">
              <label>生效时间范围</label>
              <div class="date-range-picker">
                <input
                  v-model="ruleForm.startTime"
                  type="datetime-local"
                  :max="ruleForm.endTime"
                />
                <span class="date-separator">至</span>
                <input
                  v-model="ruleForm.endTime"
                  type="datetime-local"
                  :min="ruleForm.startTime"
                />
              </div>
            </div>

            <!-- 高级选项，默认收起 -->
            <div class="form-advanced-options">
              <div class="advanced-toggle" @click="showAdvanced = !showAdvanced">
                <span>高级选项</span>
                <i :class="['icon-arrow', { 'expanded': showAdvanced }]"></i>
              </div>
              
              <div v-if="showAdvanced" class="advanced-options">
                <div class="form-row">
                  <div class="form-group">
                    <label for="memberLevel">会员等级</label>
                    <input
                      id="memberLevel"
                      v-model.number="ruleForm.memberLevel"
                      type="number"
                      min="1"
                      placeholder="留空表示所有等级"
                    />
                  </div>

                  <div class="form-group">
                    <label for="foodId">商品ID</label>
                    <input
                      id="foodId"
                      v-model.number="ruleForm.foodId"
                      type="number"
                      min="1"
                      placeholder="指定商品ID"
                    />
                  </div>
                </div>

                <div class="form-row">
                  <div class="form-group">
                    <label for="minOrderAmount">最低订单金额</label>
                    <input
                      id="minOrderAmount"
                      v-model.number="ruleForm.minOrderAmount"
                      type="number"
                      min="0"
                      step="0.01"
                      placeholder="最低订单金额"
                    />
                  </div>

                  <div class="form-group">
                    <label for="maxOrderAmount">最高订单金额</label>
                    <input
                      id="maxOrderAmount"
                      v-model.number="ruleForm.maxOrderAmount"
                      type="number"
                      min="0"
                      step="0.01"
                      placeholder="最高订单金额"
                    />
                  </div>
                </div>
              </div>
            </div>

            <div class="form-actions">
              <button type="button" class="btn btn-secondary" @click="closeModal" :disabled="isSaving">
                取消
              </button>
              <button type="submit" class="btn btn-primary" :disabled="isSaving">
                <span v-if="isSaving" class="spinner"></span>
                {{ isSaving ? '保存中...' : '保存规则' }}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import request from '../utils/request';
import { toast } from '../utils/toast';

// --- 状态数据定义 ---
const loading = ref(false);
const isSaving = ref(false);
const ruleList = ref([]);
const searchKeyword = ref('');
const ruleType = ref(undefined);
// ✅ 关键修复：ruleStatusFilter 保持为 ref(undefined)
//    因为 template 中 <option :value="undefined">全部状态</option> 会将 v-model 设置为 undefined
const ruleStatusFilter = ref(undefined); 
const sortBy = ref('createTime'); // 'createTime' 或 'priority'
const sortDesc = ref(true);
const pageNum = ref(1);
const pageSize = ref(20);
const total = ref(0);

// 弹窗状态
const showDetailModal = ref(false);
const isModalVisible = ref(false);
const isEditMode = ref(false);
const currentRuleId = ref(null);
const selectedRule = ref({});

// 规则类型映射
const ruleTypeMap = {
  0: '消费积分',
  1: '促销积分',
  2: '等级积分',
  3: '行为积分'
};

// 规则状态映射（用于筛选）
const ruleStatusMap = {
  1: '启用中',
  0: '已禁用'
};

// 表单数据
// 高级选项显示状态
const showAdvanced = ref(false);

// 规则表单数据
const ruleForm = ref({
  ruleName: '',
  ruleType: 0,
  ruleStatus: 1,
  pointsRatio: null,
  pointsMultiplier: null,
  pointsAmount: null,
  priority: 100,
  expireDays: 0,
  startTime: null,
  endTime: null,
  memberLevel: null,
  minOrderAmount: null,
  maxOrderAmount: null,
  foodId: null,
  behaviorType: 'like',
  holidayStart: null,
  holidayEnd: null
});

// --- 计算属性 ---
const modalTitle = computed(() => 
  isEditMode.value ? '编辑积分规则' : '新增积分规则'
);

const filteredRuleList = computed(() => {
  let list = [...ruleList.value];
  
  // 搜索和状态的本地筛选 (用于补充后端可能缺失的功能)
  if (searchKeyword.value || ruleStatusFilter.value !== undefined) {
    if (searchKeyword.value) {
      const keyword = searchKeyword.value.toLowerCase();
      list = list.filter(rule => 
        rule.ruleName.toLowerCase().includes(keyword)
      );
    }
    
    // ✅ 关键修复：本地状态筛选逻辑，只有当 ruleStatusFilter 不是 undefined 时才进行过滤
    if (ruleStatusFilter.value !== undefined) {
      list = list.filter(rule => rule.ruleStatus === ruleStatusFilter.value);
    }
  }

  // 排序
  list.sort((a, b) => {
    if (sortBy.value === 'priority') {
      return sortDesc.value ? b.priority - a.priority : a.priority - b.priority;
    } else {
      const timeA = new Date(a.createTime).getTime();
      const timeB = new Date(b.createTime).getTime();
      return sortDesc.value ? timeB - timeA : timeA - timeB;
    }
  });
  
  return list;
});

const hasMore = computed(() => {
  return ruleList.value.length < total.value;
});

// --- API 函数 ---
const fetchRuleList = async (isLoadMore = false) => {
  if (!isLoadMore) {
    loading.value = true;
    pageNum.value = 1;
  }
  
  // ⚠️ 保持简单：直接使用 ruleStatusFilter.value，因为它在选择“全部状态”时就是 undefined，
  // 传递 undefined 给后端参数通常意味着不筛选该字段。
  const requestParams = {
    pageNum: pageNum.value,
    pageSize: pageSize.value,
    ruleType: ruleType.value,
    ruleStatus: ruleStatusFilter.value // 如果是 undefined，则不进行状态筛选
  };
  
  // 1. 调试信息：打印请求参数
  console.log('API请求参数 (查询积分规则列表):', requestParams);
  
  try {
    const response = await request.get('/api/marketing/points/rules', {
      params: requestParams
    });
    
    // 2. 调试信息：打印后端的原始返回值
    console.log('API返回原始响应 (查询积分规则列表):', response);
    
    if (response.success) {
      const data = response.data || response;
      let newRules = [];
      
      if (Array.isArray(data)) {
        newRules = data;
        total.value = data.length; 
      } else if (data && Array.isArray(data.data)) {
        newRules = data.data;
        total.value = data.total || 0;
      }
      
      // 3. 调试信息：打印处理后的规则数量和总数
      console.log('处理后的规则数量:', newRules.length, '总数:', total.value);
      
      // 添加规则类型名称
      newRules = newRules.map(rule => ({
        ...rule,
        ruleTypeName: ruleTypeMap[rule.ruleType] || '未知'
      }));
      
      if (isLoadMore) {
        ruleList.value = [...ruleList.value, ...newRules];
      } else {
        ruleList.value = newRules;
      }
    } else {
      // 4. 调试信息：打印接口返回的错误信息
      console.error('API业务失败 (查询积分规则列表):', response.message || '未知错误');
      toast.error(response.message || '查询规则列表失败');
      ruleList.value = [];
      total.value = 0;
    }
  } catch (error) {
    // 5. 调试信息：打印网络请求或解析错误
    console.error('API请求错误 (查询积分规则列表):', error);
    toast.error('网络请求失败，无法获取规则列表');
    ruleList.value = [];
    total.value = 0;
  } finally {
    loading.value = false;
    // 6. 调试信息：请求结束
    console.log('API请求结束 (查询积分规则列表)');
  }
};

// ... (其他函数保持不变)

const loadMore = () => {
  if (hasMore.value && !loading.value) {
    pageNum.value++;
    fetchRuleList(true);
  }
};

// --- 工具函数（保持不变） ---
const formatDate = (dateString) => {
  if (!dateString) return '-';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN');
};

const formatDateTime = (dateString) => {
  if (!dateString) return '-';
  const date = new Date(dateString);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  });
};

const formatRuleTimeRange = (startTime, endTime) => {
  if (!startTime && !endTime) return '永久有效';
  
  const format = (date) => new Date(date).toLocaleDateString('zh-CN');
  if (startTime && endTime) return `${format(startTime)} 至 ${format(endTime)}`;
  if (startTime) return `从 ${format(startTime)} 开始`;
  if (endTime) return `截止至 ${format(endTime)}`;
};

const formatPointsValue = (rule) => {
  if (rule.ruleType === 0) return `${rule.pointsRatio || 0}倍`;
  if (rule.ruleType === 1) return `${rule.pointsMultiplier || 0}倍`;
  if (rule.ruleType === 3) return `${rule.pointsAmount || 0}积分`;
  return '-';
};

// --- 交互操作函数（保持不变） ---
const showRuleDetail = (rule) => {
  selectedRule.value = { ...rule };
  showDetailModal.value = true;
};

const closeDetailModal = () => {
  showDetailModal.value = false;
  selectedRule.value = {};
};

const handleAddRule = () => {
  currentRuleId.value = null;
  isEditMode.value = false;
  ruleForm.value = {
    ruleName: '',
    ruleType: 0,
    ruleStatus: 1,
    pointsRatio: null,
    pointsMultiplier: null,
    pointsAmount: null,
    priority: 100,
    expireDays: 0,
    startTime: null,
    endTime: null,
    memberLevel: null,
    minOrderAmount: null,
    maxOrderAmount: null,
    foodId: null,
    behaviorType: 'like',
    holidayStart: null,
    holidayEnd: null
  };
  showAdvanced.value = false;
  isModalVisible.value = true;
};

const handleRuleTypeChange = () => {
  // 清空特定类型的值，避免数据混淆
  ruleForm.value.pointsRatio = null;
  ruleForm.value.pointsMultiplier = null;
  ruleForm.value.pointsAmount = null;
  ruleForm.value.behaviorType = 'like';
};

const handleEdit = (rule) => {
  closeDetailModal();
  
  // 转换日期时间格式为本地日期时间选择器需要的格式
  const formatForDateTimeInput = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    // 处理时区问题，确保日期时间正确显示
    const tzOffset = date.getTimezoneOffset() * 60000;
    const localISOTime = new Date(date - tzOffset).toISOString().slice(0, 16);
    return localISOTime;
  };

  ruleForm.value = {
    ...rule,
    startTime: formatForDateTimeInput(rule.startTime),
    endTime: formatForDateTimeInput(rule.endTime),
    holidayStart: rule.holidayStart ? rule.holidayStart.split('T')[0] : '',
    holidayEnd: rule.holidayEnd ? rule.holidayEnd.split('T')[0] : ''
  };
  
  currentRuleId.value = rule.id;
  isEditMode.value = true;
  showAdvanced.value = false;
  isModalVisible.value = true;
};

const handleDelete = async (id) => {
  if (!confirm('确定要删除这条积分规则吗？此操作不可恢复！')) {
    return;
  }

  try {
    // ⚠️ 实际删除接口调用，需要根据您的 API 调整
    // const response = await request.delete(`/api/marketing/points/rules/${id}`);
    
    // 模拟成功
    const response = { success: true, message: '删除成功' }; 
    
    if (response.success) {
      toast.success('规则删除成功！');
      // 从列表中移除
      ruleList.value = ruleList.value.filter(rule => rule.id !== id);
      if (selectedRule.value.id === id) {
        closeDetailModal();
      }
    } else {
      toast.error(response.message || '规则删除失败');
    }
  } catch (error) {
    console.error('删除规则API请求错误:', error);
    toast.error('网络请求失败，无法删除规则');
  }
};

const closeModal = () => {
  isModalVisible.value = false;
  isEditMode.value = false;
  currentRuleId.value = null;
};

// 表单验证
const validateForm = () => {
  if (!ruleForm.value.ruleName.trim()) {
    toast.error('请输入规则名称');
    return false;
  }
  
  // 根据规则类型验证必填字段
  if (ruleForm.value.ruleType === 0 && ruleForm.value.pointsRatio === null) {
    toast.error('请填写积分比例');
    return false;
  }
  
  if (ruleForm.value.ruleType === 1 && ruleForm.value.pointsMultiplier === null) {
    toast.error('请填写积分倍数');
    return false;
  }
  
  if (ruleForm.value.ruleType === 3) {
    if (!ruleForm.value.behaviorType) {
      toast.error('请选择行为类型');
      return false;
    }
    if (!ruleForm.value.pointsAmount) {
      toast.error('请填写积分数量');
      return false;
    }
  }
  
  // 验证时间范围
  if (ruleForm.value.startTime && ruleForm.value.endTime) {
    const start = new Date(ruleForm.value.startTime);
    const end = new Date(ruleForm.value.endTime);
    if (start >= end) {
      toast.error('结束时间必须晚于开始时间');
      return false;
    }
  }
  
  // 验证订单金额范围
  if (ruleForm.value.minOrderAmount !== null && ruleForm.value.maxOrderAmount !== null) {
    if (Number(ruleForm.value.minOrderAmount) > Number(ruleForm.value.maxOrderAmount)) {
      toast.error('最高订单金额必须大于或等于最低订单金额');
      return false;
    }
  }
  
  return true;
};

// 保存规则
const handleSaveRule = async () => {
  if (!validateForm()) return;
  
  isSaving.value = true;
  
  try {
    // 准备请求数据
    const formData = { ...ruleForm.value };
    
    // 确保必填字段存在
    const requiredFields = ['ruleName', 'ruleType', 'ruleStatus'];
    requiredFields.forEach(field => {
      if (formData[field] === undefined || formData[field] === '') {
        throw new Error(`必填字段 ${field} 不能为空`);
      }
    });
    
    // 转换数字字段
    const numberFields = ['pointsRatio', 'pointsMultiplier', 'pointsAmount', 'priority', 'expireDays', 'memberLevel', 'foodId'];
    numberFields.forEach(field => {
      if (formData[field] !== undefined && formData[field] !== null && formData[field] !== '') {
        formData[field] = Number(formData[field]);
      }
    });
    
    // 转换日期时间格式
    const dateFields = ['startTime', 'endTime'];
    dateFields.forEach(field => {
      if (formData[field]) {
        formData[field] = new Date(formData[field]).toISOString();
      }
    });
    
    // 转换日期格式（只有日期部分）
    const dateOnlyFields = ['holidayStart', 'holidayEnd'];
    dateOnlyFields.forEach(field => {
      if (formData[field]) {
        formData[field] = formData[field].split('T')[0];
      }
    });
    
    // 根据规则类型设置特定字段
    if (formData.ruleType === 0) { // 消费积分
      formData.pointsMultiplier = null;
      formData.pointsAmount = null;
      formData.behaviorType = null;
    } else if (formData.ruleType === 1) { // 促销积分
      formData.pointsRatio = null;
      formData.pointsAmount = null;
      formData.behaviorType = null;
    } else if (formData.ruleType === 3) { // 行为积分
      formData.pointsRatio = null;
      formData.pointsMultiplier = null;
    }
    
    // 清理数据，保留0和false等有效值
    Object.keys(formData).forEach(key => {
      if (formData[key] === '' || formData[key] === null || formData[key] === undefined) {
        delete formData[key];
      }
    });
    
    console.log('提交的表单数据:', JSON.stringify(formData, null, 2));
    
    let response;
    if (isEditMode.value && currentRuleId.value) {
      // 更新现有规则
      response = await request.put(`/api/marketing/points/rules/${currentRuleId.value}`, formData);
    } else {
      // 创建新规则
      response = await request.post('/api/marketing/points/rules', formData);
    }

    if (response.success) {
      toast.success(isEditMode.value ? '规则更新成功！' : '规则创建成功！');
      closeModal();
      refreshList();
    } else {
      throw new Error(response.message || '操作失败');
    }
  } catch (error) {
    console.error('保存规则失败:', error);
    toast.error(error.message || '保存规则失败，请稍后重试');
  } finally {
    isSaving.value = false;
  }
};

const handleSearch = () => {
  // 触发搜索和刷新
  fetchRuleList();
};

const clearSearch = () => {
  searchKeyword.value = '';
  fetchRuleList();
};

const handleFilterChange = () => {
  fetchRuleList();
};

const handleStatusFilterChange = () => {
  fetchRuleList();
};

const toggleSort = () => {
  // 仅切换排序字段，computed 会自动重新排序
  if (sortBy.value === 'createTime') {
    sortBy.value = 'priority';
  } else {
    sortBy.value = 'createTime';
  }
};

const refreshList = () => {
  // 重置筛选条件，显示所有状态的规则
  ruleStatusFilter.value = undefined;
  // 重置分页到第一页
  pageNum.value = 1;
  // 重新获取规则列表
  fetchRuleList();
};

// --- 生命周期 ---
onMounted(() => {
  fetchRuleList();
});

// 监听搜索关键词变化，如果清空则刷新列表
watch(searchKeyword, (newVal) => {
  if (newVal === '') {
    fetchRuleList();
  }
});
</script>

<style scoped>
/* 顶部背景样式 */
.top-background {
  width: 100%;
  height: 100px;
  background: linear-gradient(to right, #3a7bd5, #00d2ff);
  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border-radius: 16px 16px 0 0;
  position: fixed;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  z-index: 1000;
  overflow: hidden;
  margin-bottom: 50px;
  max-width: 600px;
}

.top-background::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 255, 255, 0.2) 0%, rgba(255, 255, 255, 0) 70%);
  transform: rotate(30deg);
  animation: shine 6s infinite linear;
}

@keyframes shine {
  0% {
    transform: rotate(30deg) translate(-10%, -10%);
  }
  100% {
    transform: rotate(30deg) translate(10%, 10%);
  }
}

.top-background h1 {
  color: white;
  font-size: 20px;
  font-weight: 600;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  letter-spacing: 1px;
  margin: 0;
  z-index: 1;
}

/* 页面内容区域 */
.page-content {
  margin-top: 120px; /* 为固定定位的顶部栏留出空间 */
  padding: 0 16px 16px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
  position: relative;
}

/* 基础样式 */
.sub-page-container {
  max-width: 100%;
  margin: 0;
  padding: 16px;
  min-height: 100vh;
  background: #f5f7fa;
}

.sub-page-header {
  padding: 20px 0 16px;
  border-bottom: none;
  margin-bottom: 16px;
}

.sub-page-title {
  font-size: 24px;
  font-weight: 700;
  color: #1a1a1a;
  margin: 0 0 4px;
}

.sub-page-description {
  font-size: 14px;
  color: #666;
  margin: 0;
}

.content-panel {
  background: transparent;
  padding: 0;
}

/* 工具栏 - 布局调整 */
.toolbar {
  display: flex;
  flex-direction: column; /* 确保子元素纵向排列 */
  gap: 12px;
  margin-bottom: 16px;
  background: white;
  padding: 16px;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
}

/* 顶部行：搜索框和新增按钮 */
.top-row {
  display: flex;
  gap: 12px;
  width: 100%;
  align-items: center; /* 垂直居中 */
}

/* 筛选和排序行 */
.filter-sort-row {
  display: flex;
  gap: 8px;
  width: 100%;
}

.search-box {
  flex: 1; /* 占据更多空间 */
  position: relative;
  display: flex;
  align-items: center;
  background: #f8f9fa;
  border-radius: 10px;
  padding: 0 12px;
}

.search-box .icon-search {
  color: #999;
  font-size: 16px;
  margin-right: 8px;
}

.search-input {
  flex: 1;
  height: 44px;
  border: none;
  background: transparent;
  font-size: 15px;
  color: #333;
  outline: none;
  padding: 0;
}

.search-input::placeholder {
  color: #aaa;
}

.clear-search {
  background: none;
  border: none;
  padding: 6px;
  color: #999;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.filter-select,
.sort-btn {
  height: 44px;
  padding: 0 12px;
  border: 1px solid #e8e8e8;
  border-radius: 10px;
  background: white;
  font-size: 14px;
  color: #333;
  cursor: pointer;
  white-space: nowrap;
}

.filter-select {
  min-width: 100px;
}

.sort-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  background: #f8f9fa;
  border: none;
}

.add-btn {
  width: auto; /* 宽度自适应 */
  min-width: 120px; /* 最小宽度 */
  height: 44px; /* 与搜索框高度一致 */
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: opacity 0.2s, transform 0.2s;
}

.add-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}


/* 规则列表 */
.rule-list-wrapper {
  min-height: 300px;
}

.rule-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.rule-item {
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);
  transition: all 0.2s ease;
  border: 1px solid transparent;
}

.rule-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
  border-color: #e8e8e8;
}

.rule-item-main {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
  cursor: pointer;
}

.rule-info {
  flex: 1;
  min-width: 0; /* 防止内容溢出 */
}

.rule-name {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 8px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.rule-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 8px;
}

.rule-type {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 500;
}

.type-0 { background: rgba(56, 161, 105, 0.1); color: #38a169; }
.type-1 { background: rgba(49, 130, 206, 0.1); color: #3182ce; }
.type-2 { background: rgba(158, 91, 224, 0.1); color: #9e5be0; }
.type-3 { background: rgba(229, 62, 62, 0.1); color: #e53e3e; }

.rule-priority,
.rule-expire {
  font-size: 12px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 2px;
}

.rule-time {
  font-size: 12px;
  color: #999;
  display: flex;
  align-items: center;
  gap: 4px;
}

.rule-status-area {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
  margin-left: 12px;
}

.rule-status {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 12px;
  font-weight: 500;
  min-width: 60px;
  text-align: center;
}

.status-enabled {
  background: rgba(56, 161, 105, 0.1);
  color: #38a169;
}

.status-disabled {
  background: rgba(160, 174, 192, 0.1);
  color: #a0aec0;
}

.rule-points {
  font-size: 14px;
  font-weight: 600;
  color: #667eea;
  text-align: right;
}

.rule-item-actions {
  display: flex;
  gap: 8px;
  padding-top: 12px;
  border-top: 1px solid #f0f0f0;
  justify-content: flex-end;
}

.action-btn {
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
  border: none;
}

.edit-btn {
  background: #ebf8ff;
  color: #3182ce;
}

.delete-btn {
  background: #fff5f5;
  color: #e53e3e;
}

.action-btn:hover {
  opacity: 0.8;
  transform: translateY(-1px);
}

/* 加载更多 */
.load-more {
  text-align: center;
  padding: 20px 0;
}

.load-more-btn {
  padding: 12px 32px;
  background: white;
  border: 1px solid #e8e8e8;
  border-radius: 12px;
  color: #666;
  font-size: 15px;
  cursor: pointer;
  transition: all 0.2s;
}

.load-more-btn:hover {
  background: #f8f9fa;
  border-color: #d0d0d0;
}

/* 详情弹窗 (保持不变) */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  max-height: 90vh;
  display: flex;
  flex-direction: column;
}

.detail-modal {
  width: 90%;
  max-width: 500px;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e8e8e8;
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
  color: #1a1a1a;
}

.modal-close {
  background: none;
  border: none;
  font-size: 20px;
  color: #999;
  cursor: pointer;
  padding: 4px;
}

.modal-header-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.detail-status {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 12px;
  font-weight: 500;
}

.modal-body {
  padding: 20px;
  overflow-y: auto;
  flex: 1;
}

.detail-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.detail-section {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 16px;
}

.section-title {
  font-size: 15px;
  font-weight: 600;
  color: #333;
  margin: 0 0 12px 0;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.detail-grid.full-width {
  grid-template-columns: 1fr;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-label {
  font-size: 13px;
  color: #666;
}

.detail-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
  word-break: break-word;
}

.rule-type-badge {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
  margin-top: 2px;
}

/* 弹窗底部按钮 (保持不变) */
.modal-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-top: 1px solid #e8e8e8;
}

.action-buttons {
  display: flex;
  gap: 8px;
}

.btn {
  padding: 10px 20px;
  border-radius: 10px;
  font-size: 15px;
  font-weight: 500;
  cursor: pointer;
  border: none;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.2s;
}

.btn-secondary {
  background: #f8f9fa;
  color: #666;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn-edit {
  background: #ebf8ff;
  color: #3182ce;
}

.btn-delete {
  background: #fff5f5;
  color: #e53e3e;
}

.btn:hover:not(:disabled) {
  opacity: 0.9;
  transform: translateY(-1px);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 加载状态 (保持不变) */
.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  gap: 16px;
}

.loading-spinner, .spinner {
  width: 20px;
  height: 20px;
  border: 3px solid #f3f3f3;
  border-top: 3px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.spinner {
  border-top-color: white;
  margin-right: 8px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 空状态 (保持不变) */
.empty-state {
  text-align: center;
  padding: 60px 20px;
  color: #999;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
  opacity: 0.3;
}

.empty-state p {
  margin: 8px 0 20px;
  font-size: 16px;
  color: #666;
}

/* 规则表单样式 */
.rule-form-modal {
  max-width: 800px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
  padding: 24px;
}

.rule-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 500;
  color: #333;
  font-size: 14px;
}

.form-group input[type="text"],
.form-group input[type="number"],
.form-group input[type="date"],
.form-group input[type="datetime-local"],
.form-group select {
  padding: 10px 12px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  transition: border-color 0.2s;
}

.form-group input:focus,
.form-group select:focus {
  border-color: #1890ff;
  outline: none;
  box-shadow: 0 0 0 2px rgba(24, 144, 255, 0.2);
}

.form-row {
  display: flex;
  gap: 16px;
}

.form-row .form-group {
  flex: 1;
}

.date-range-picker {
  display: flex;
  align-items: center;
  gap: 8px;
}

.date-range-picker input[type="datetime-local"] {
  flex: 1;
  min-width: 0;
}

.date-separator {
  color: #666;
  font-size: 14px;
}

.form-advanced-options {
  margin-top: 10px;
  border: 1px solid #eee;
  border-radius: 6px;
  overflow: hidden;
}

.advanced-toggle {
  padding: 12px 16px;
  background-color: #f8f9fa;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 500;
  color: #333;
  transition: background-color 0.2s;
}

.advanced-toggle:hover {
  background-color: #f1f3f5;
}

.advanced-options {
  padding: 16px;
  background-color: #fff;
  border-top: 1px solid #eee;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 24px;
  padding-top: 16px;
  border-top: 1px solid #eee;
}

.required {
  color: #ff4d4f;
  margin-left: 2px;
}

/* 加载动画 */
@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: #fff;
  animation: spin 1s ease-in-out infinite;
  margin-right: 8px;
  vertical-align: middle;
}

/* 箭头图标动画 */
.icon-arrow {
  display: inline-block;
  width: 0;
  height: 0;
  border-left: 5px solid transparent;
  border-right: 5px solid transparent;
  border-top: 5px solid #666;
  transition: transform 0.2s;
}

.icon-arrow.expanded {
  transform: rotate(180deg);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .form-row {
    flex-direction: column;
    gap: 16px;
  }
  
  .rule-form-modal {
    width: 95%;
    padding: 16px;
  }
  
  .form-actions {
    flex-direction: column-reverse;
    gap: 10px;
  }
  
  .form-actions .btn {
    width: 100%;
  }
}

/* 响应式调整 - 小屏幕 */
@media (max-width: 480px) {
  .sub-page-container {
    padding: 12px;
  }
  
  .toolbar {
    padding: 12px;
  }
  
  .top-row,
  .filter-sort-row {
    flex-wrap: wrap; /* 允许换行 */
  }

  .search-box {
    flex: 1 1 100%; /* 搜索框完全占据一行 */
  }

  .add-btn {
    flex: 1 1 100%; /* 新增按钮在小屏上单独一行 */
    min-width: unset;
  }
  
  .filter-select,
  .sort-btn {
    flex: 1;
    min-width: 0;
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
  
  .rule-item-actions {
    flex-direction: column;
  }
  
  .action-btn {
    justify-content: center;
  }
  
  .modal-footer {
    flex-direction: column;
    gap: 12px;
  }
  
  .action-buttons {
    width: 100%;
  }
  
  .btn {
    flex: 1;
    justify-content: center;
  }
}

/* 图标样式（需要根据实际图标库调整） */
.icon-search,
.icon-clear,
.icon-sort,
.icon-add,
.icon-close,
.icon-edit,
.icon-delete,
.icon-priority,
.icon-expire,
.icon-time,
.icon-empty {
  font-family: 'Material Icons', 'Font Awesome', sans-serif;
  font-style: normal;
  font-weight: normal;
  speak: none;
}

.icon-search::before { content: '🔍'; }
.icon-clear::before { content: '×'; }
.icon-sort::before { content: '↕'; }
.icon-add::before { content: '+'; }
.icon-close::before { content: '×'; }
.icon-edit::before { content: '✎'; }
.icon-delete::before { content: '🗑'; }
.icon-priority::before { content: '⚡'; }
.icon-expire::before { content: '⏰'; }
.icon-time::before { content: '🕒'; }
.icon-empty::before { content: '📋'; }

/* 表单按钮样式 */
.btn {
  padding: 10px 20px;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
  border: 1px solid transparent;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  min-width: 100px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.btn-primary {
  background-color: #1890ff;
  color: white;
  border-color: #1890ff;
}

.btn-primary:not(:disabled):hover {
  background-color: #40a9ff;
  border-color: #40a9ff;
}

.btn-secondary {
  background-color: #f5f5f5;
  color: #333;
  border-color: #d9d9d9;
}

.btn-secondary:not(:disabled):hover {
  background-color: #e6f7ff;
  border-color: #91d5ff;
  color: #1890ff;
}
</style>