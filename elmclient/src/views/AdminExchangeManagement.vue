<template>
  <div class="backbutton" @click="goBack">
    <i class="fas fa-arrow-left"></i> 
</div>
  <div class="points-exchange-app">
    <!-- 头部 -->
    <div class="top-background">
      <h1>积分兑换管理</h1>
    </div>

    <!-- Tab切换 -->
    <div class="tab-navigation">
      <div class="tab-container">
        <button 
          :class="['tab-item', { 'active': currentTab === 'Rule' }]" 
          @click="switchTab('Rule')"
        >
          <span class="tab-icon">📊</span>
          <span class="tab-label">积分+现金规则</span>
          <span class="tab-count" v-if="ruleList.total > 0">{{ ruleList.total }}</span>
        </button>
        <button 
          :class="['tab-item', { 'active': currentTab === 'Goods' }]" 
          @click="switchTab('Goods')"
        >
          <span class="tab-icon">🎁</span>
          <span class="tab-label">兑换商品</span>
          <span class="tab-count" v-if="goodsList.total > 0">{{ goodsList.total }}</span>
        </button>
      </div>
    </div>

    <!-- 规则列表 -->
    <div class="content-area" v-if="currentTab === 'Rule'">
      <!-- 筛选栏（新增按钮在这里） -->
      <div class="list-header">
        <div class="filter-group">
          <div class="filter-item">
            <select v-model="ruleSearchForm.ruleStatus" class="filter-select" @change="handleSearch('Rule')">
              <option :value="undefined">全部状态</option>
              <option value="1">启用中</option>
              <option value="0">已禁用</option>
            </select>
          </div>
        </div>
        <div class="header-actions">
          <button class="primary-btn" @click="handleAdd('Rule')">
            <span class="icon-add"></span>
            新增规则
          </button>
        </div>
      </div>

      <!-- 规则列表 -->
      <div class="list-container">
        <!-- 加载状态 -->
        <div class="loading-state" v-if="ruleLoading">
          <div class="loading-spinner"></div>
          <p>加载规则中...</p>
        </div>

        <!-- 空状态 -->
        <div class="empty-state" v-if="!ruleLoading && ruleList.list.length === 0">
          <div class="empty-icon">📊</div>
          <h3>暂无兑换规则</h3>
          <p>创建您的第一个积分兑换规则</p>
          <button class="primary-btn" @click="handleAdd('Rule')">
            <span class="icon-add"></span>
            创建规则
          </button>
        </div>

        <!-- 规则列表项 -->
        <div class="item-list">
          <div 
            v-for="rule in ruleList.list" 
            :key="rule.id" 
            class="list-item"
            @click="showDetail(rule, 'Rule')"
          >
            <div class="item-main">
              <!-- 规则布局：标题和状态在同一行 -->
              <div class="item-header rule-header">
                <div class="title-container">
                  <div class="item-title">{{ rule.ruleName }}</div>
                  <span class="item-status" :class="rule.ruleStatus === 1 ? 'active' : 'inactive'">
                    {{ rule.ruleStatus === 1 ? '启用中' : '已禁用' }}
                  </span>
                </div>
              </div>
              
              <div class="item-content">
                <div class="item-info">
                  <div class="info-item">
                    <span class="info-label">兑换比例</span>
                    <span class="info-value highlight">{{ rule.exchangeRatio }}积分 = 1元</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">积分范围</span>
                    <span class="info-value">
                      {{ rule.minPoints || 0 }} ~ {{ rule.maxPoints || '无限制' }}
                    </span>
                  </div>
                </div>
                
                <div class="item-meta">
                  <span class="meta-item">
                    <span class="meta-icon">📅</span>
                    {{ formatSimpleDate(rule.startTime) }}
                  </span>
                  <span v-if="rule.endTime" class="meta-item">
                    <span class="meta-icon">→</span>
                    {{ formatSimpleDate(rule.endTime) }}
                  </span>
                </div>
              </div>
            </div>
            
            <div class="item-actions">
              <button class="action-btn edit" @click.stop="handleEdit(rule)">
                <span class="action-icon">✏️</span>
                编辑
              </button>
              <button 
                class="action-btn status" 
                :class="rule.ruleStatus === 1 ? 'disable' : 'enable'"
                @click.stop="handleToggleStatus(rule)"
              >
                <span class="action-icon" v-if="rule.ruleStatus === 1">⏸️</span>
                <span class="action-icon" v-else>▶️</span>
                {{ rule.ruleStatus === 1 ? '禁用' : '启用' }}
              </button>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="ruleList.total > 0">
          <div class="pagination-info">
            显示 {{ getStartIndex('Rule') }}-{{ getEndIndex('Rule') }} 条，共 {{ ruleList.total }} 条
          </div>
          <div class="pagination-controls">
            <button 
              class="page-btn prev" 
              @click="handlePageChange('Rule', ruleSearchForm.pageNum - 1)"
              :disabled="ruleSearchForm.pageNum <= 1"
            >
              <span class="icon-arrow-left"></span>
            </button>
            <span class="page-current">{{ ruleSearchForm.pageNum }}</span>
            <span class="page-total">/ {{ Math.ceil(ruleList.total / ruleSearchForm.pageSize) }}</span>
            <button 
              class="page-btn next" 
              @click="handlePageChange('Rule', ruleSearchForm.pageNum + 1)"
              :disabled="ruleSearchForm.pageNum >= Math.ceil(ruleList.total / ruleSearchForm.pageSize)"
            >
              <span class="icon-arrow-right"></span>
            </button>
            <select v-model="ruleSearchForm.pageSize" class="page-size" @change="handlePageSizeChange('Rule')">
              <option :value="10">10条/页</option>
              <option :value="20">20条/页</option>
              <option :value="50">50条/页</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="content-area" v-if="currentTab === 'Goods'">
      <!-- 筛选栏（新增按钮在这里） -->
      <div class="list-header">
        <div class="filter-group">
          <div class="filter-item">
            <select v-model="goodsSearchForm.ruleStatus" class="filter-select" @change="handleSearch('Goods')">
              <option :value="undefined">全部状态</option>
              <option value="1">启用中</option>
              <option value="0">已禁用</option>
            </select>
          </div>
        </div>
        <div class="header-actions">
          <button class="primary-btn" @click="handleAdd('Goods')">
            <span class="icon-add"></span>
            新增商品
          </button>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="list-container">
        <!-- 加载状态 -->
        <div class="loading-state" v-if="goodsLoading">
          <div class="loading-spinner"></div>
          <p>加载商品中...</p>
        </div>

        <!-- 空状态 -->
        <div class="empty-state" v-if="!goodsLoading && goodsList.list.length === 0">
          <div class="empty-icon">🎁</div>
          <h3>暂无兑换商品</h3>
          <p>创建您的第一个兑换商品</p>
          <button class="primary-btn" @click="handleAdd('Goods')">
            <span class="icon-add"></span>
            创建商品
          </button>
        </div>

        <!-- 商品列表项 -->
        <div class="item-list">
          <div 
            v-for="goods in goodsList.list" 
            :key="goods.id" 
            class="list-item"
            @click="showDetail(goods, 'Goods')"
          >
            <div class="item-main">
              <!-- 商品布局：紧凑布局，不显示图片 -->
              <div class="item-header goods-header">
                <div class="title-container">
                  <div class="item-title">
                    <span class="food-name">{{ goods.foodName || goods.ruleName }}</span>
                    <span v-if="goods.foodId" class="food-id">(ID: {{ goods.foodId }})</span>
                  </div>
                  <div class="item-tags">
                    <span class="item-status" :class="goods.ruleStatus === 1 ? 'active' : 'inactive'">
                      {{ goods.ruleStatus === 1 ? '启用中' : '已禁用' }}
                    </span>
                    <span v-if="goods.stockQuantity < 10 && goods.ruleStatus === 1" class="stock-warning">
                      ⚠️ 库存紧张
                    </span>
                  </div>
                </div>
              </div>
              
              <div class="item-content">
                <div class="item-info compact-info">
                  <div class="info-item">
                    <span class="info-label">所需积分</span>
                    <span class="info-value highlight">{{ goods.requiredPoints }}积分</span>
                  </div>
                  <div class="info-item">
                    <span class="info-label">库存</span>
                    <span class="info-value">{{ goods.stockQuantity }}件</span>
                  </div>
                </div>
                
                <div class="item-meta">
                  <span class="meta-item">
                    <span class="meta-icon">📅</span>
                    {{ formatSimpleDate(goods.startTime) }}
                  </span>
                  <span v-if="goods.endTime" class="meta-item">
                    <span class="meta-icon">→</span>
                    {{ formatSimpleDate(goods.endTime) }}
                  </span>
                </div>
              </div>
            </div>
            
            <div class="item-actions">
              <button class="action-btn edit" @click.stop="handleEdit(goods)">
                <span class="action-icon">✏️</span>
                编辑
              </button>
              <button 
                class="action-btn status" 
                :class="goods.ruleStatus === 1 ? 'disable' : 'enable'"
                @click.stop="handleToggleStatus(goods)"
              >
                <span class="action-icon" v-if="goods.ruleStatus === 1">⏸️</span>
                <span class="action-icon" v-else>▶️</span>
                {{ goods.ruleStatus === 1 ? '禁用' : '启用' }}
              </button>
            </div>
          </div>
        </div>

        <!-- 分页 -->
        <div class="pagination" v-if="goodsList.total > 0">
          <div class="pagination-info">
            显示 {{ getStartIndex('Goods') }}-{{ getEndIndex('Goods') }} 条，共 {{ goodsList.total }} 条
          </div>
          <div class="pagination-controls">
            <button 
              class="page-btn prev" 
              @click="handlePageChange('Goods', goodsSearchForm.pageNum - 1)"
              :disabled="goodsSearchForm.pageNum <= 1"
            >
              <span class="icon-arrow-left"></span>
            </button>
            <span class="page-current">{{ goodsSearchForm.pageNum }}</span>
            <span class="page-total">/ {{ Math.ceil(goodsList.total / goodsSearchForm.pageSize) }}</span>
            <button 
              class="page-btn next" 
              @click="handlePageChange('Goods', goodsSearchForm.pageNum + 1)"
              :disabled="goodsSearchForm.pageNum >= Math.ceil(goodsList.total / goodsSearchForm.pageSize)"
            >
              <span class="icon-arrow-right"></span>
            </button>
            <select v-model="goodsSearchForm.pageSize" class="page-size" @change="handlePageSizeChange('Goods')">
              <option :value="10">10条/页</option>
              <option :value="20">20条/页</option>
              <option :value="50">50条/页</option>
            </select>
          </div>
        </div>
      </div>
    </div>

    <!-- 详情弹窗 -->
    <div class="detail-modal" v-if="showDetailModal" @click.self="closeDetailModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ detailData.ruleName }}</h2>
          <button class="modal-close" @click="closeDetailModal">
            <span class="icon-close"></span>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="detail-section">
            <h3 class="section-title">
              <span class="section-icon" v-if="detailType === 'Rule'">📊</span>
              <span class="section-icon" v-else>🎁</span>
              {{ detailType === 'Rule' ? '规则详情' : '商品详情' }}
            </h3>
            
            <div class="detail-grid">
              <!-- 通用信息 -->
              <div class="detail-item">
                <label>状态</label>
                <span :class="detailData.ruleStatus === 1 ? 'status-active' : 'status-inactive'">
                  {{ detailData.ruleStatus === 1 ? '启用中' : '已禁用' }}
                </span>
              </div>
              
              <div class="detail-item">
                <label>生效时间</label>
                <span>{{ formatFullDate(detailData.startTime) }}</span>
              </div>
              
              <div class="detail-item">
                <label>结束时间</label>
                <span>{{ detailData.endTime ? formatFullDate(detailData.endTime) : '永久有效' }}</span>
              </div>

              <!-- 规则专属信息 -->
              <template v-if="detailType === 'Rule'">
                <div class="detail-item">
                  <label>兑换比例</label>
                  <span class="highlight">{{ detailData.exchangeRatio }}积分 = 1元</span>
                </div>
                
                <div class="detail-item">
                  <label>最小积分</label>
                  <span>{{ detailData.minPoints || 0 }}分</span>
                </div>
                
                <div class="detail-item">
                  <label>最大积分</label>
                  <span>{{ detailData.maxPoints || '无限制' }}</span>
                </div>
              </template>

              <!-- 商品专属信息 -->
              <template v-if="detailType === 'Goods'">
                <div class="detail-item">
                  <label>所需积分</label>
                  <span class="highlight">{{ detailData.requiredPoints }}分</span>
                </div>
                
                <div class="detail-item">
                  <label>库存数量</label>
                  <span :class="{ 'stock-low': detailData.stockQuantity < 10 }">
                    {{ detailData.stockQuantity }}件
                    <span v-if="detailData.stockQuantity < 10" class="warning-text">(库存紧张)</span>
                  </span>
                </div>
                
                <div class="detail-item">
                  <label>商品ID</label>
                  <span>{{ detailData.foodId || '未设置' }}</span>
                </div>
              </template>
            </div>
          </div>
          
          <div class="action-section">
            <button class="btn secondary" @click="closeDetailModal">关闭</button>
            <button class="btn primary" @click="handleEditFromDetail">
              <span class="icon-edit"></span>
              编辑{{ detailType === 'Rule' ? '规则' : '商品' }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑/新增弹窗 -->
    <div class="edit-modal" v-if="showEditModal" @click.self="closeEditModal">
      <div class="modal-content">
        <div class="modal-header">
          <h2>{{ isEditMode ? '编辑' : '新增' }}{{ formData.ruleType === '0' ? '规则' : '商品' }}</h2>
          <button class="modal-close" @click="closeEditModal">
            <span class="icon-close"></span>
          </button>
        </div>
        
        <div class="modal-body">
          <div class="form-container">
            <div class="form-group">
              <label class="form-label required">名称</label>
              <input 
                type="text" 
                v-model="formData.ruleName" 
                placeholder="请输入名称"
                class="form-input"
              />
            </div>
            
            <div class="form-group" v-if="!isEditMode">
              <label class="form-label required">类型</label>
              <div class="type-selector">
                <button 
                  :class="['type-option', { 'selected': formData.ruleType === '0' }]"
                  @click="formData.ruleType = '0'; handleRuleTypeChange()"
                >
                  <span class="type-icon">📊</span>
                  <span class="type-label">积分+现金规则</span>
                </button>
                <button 
                  :class="['type-option', { 'selected': formData.ruleType === '1' }]"
                  @click="formData.ruleType = '1'; handleRuleTypeChange()"
                >
                  <span class="type-icon">🎁</span>
                  <span class="type-label">兑换商品</span>
                </button>
              </div>
            </div>

            <!-- 规则字段 -->
            <template v-if="formData.ruleType === '0'">
              <div class="form-group">
                <label class="form-label required">兑换比例</label>
                <div class="input-with-suffix">
                  <input 
                    type="number" 
                    v-model.number="formData.exchangeRatio" 
                    placeholder="例如：10"
                    class="form-input"
                    min="1"
                  />
                  <span class="input-suffix">积分 = 1元</span>
                </div>
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label class="form-label">最小积分</label>
                  <div class="input-with-suffix">
                    <input 
                      type="number" 
                      v-model.number="formData.minPoints" 
                      placeholder="0"
                      class="form-input"
                      min="0"
                    />
                    <span class="input-suffix">分</span>
                  </div>
                </div>
                
                <div class="form-group">
                  <label class="form-label">最大积分</label>
                  <div class="input-with-suffix">
                    <input 
                      type="number" 
                      v-model.number="formData.maxPoints" 
                      placeholder="留空为无限制"
                      class="form-input"
                      min="0"
                    />
                    <span class="input-suffix">分</span>
                  </div>
                </div>
              </div>
            </template>

            <!-- 商品字段 -->
            <template v-if="formData.ruleType === '1'">
              <div class="form-group">
                <label class="form-label required">商品ID</label>
                <input 
                  type="number" 
                  v-model.number="formData.foodId" 
                  placeholder="请输入商品ID"
                  class="form-input"
                />
              </div>

              <div class="form-row">
                <div class="form-group">
                  <label class="form-label required">所需积分</label>
                  <div class="input-with-suffix">
                    <input 
                      type="number" 
                      v-model.number="formData.requiredPoints" 
                      placeholder="请输入所需积分"
                      class="form-input"
                      min="1"
                    />
                    <span class="input-suffix">分</span>
                  </div>
                </div>

                <div class="form-group">
                  <label class="form-label">库存数量</label>
                  <div class="input-with-suffix">
                    <input 
                      type="number" 
                      v-model.number="formData.stockQuantity" 
                      placeholder="0"
                      class="form-input"
                      min="0"
                    />
                    <span class="input-suffix">件</span>
                  </div>
                </div>
              </div>
            </template>

            <div class="form-group">
              <label class="form-label">状态</label>
              <div class="status-selector">
                <button 
                  :class="['status-option', { 'selected': formData.ruleStatus === 1 }]"
                  @click="formData.ruleStatus = 1"
                >
                  <span class="status-indicator active"></span>
                  <span class="status-label">启用</span>
                </button>
                <button 
                  :class="['status-option', { 'selected': formData.ruleStatus === 0 }]"
                  @click="formData.ruleStatus = 0"
                >
                  <span class="status-indicator inactive"></span>
                  <span class="status-label">禁用</span>
                </button>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label class="form-label">开始时间</label>
                <input 
                  type="datetime-local" 
                  v-model="formData.startTime"
                  class="form-input"
                />
              </div>
              <div class="form-group">
                <label class="form-label">结束时间</label>
                <input 
                  type="datetime-local" 
                  v-model="formData.endTime"
                  class="form-input"
                  placeholder="可选"
                />
              </div>
            </div>
          </div>
        </div>

        <div class="modal-footer">
          <button class="btn secondary" @click="closeEditModal">取消</button>
          <button 
            class="btn primary" 
            @click="handleSubmit"
            :disabled="submitting"
          >
            <span v-if="submitting" class="spinner"></span>
            {{ submitting ? '保存中...' : '保存' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue';
import request from '../utils/request';
import { toast } from '../utils/toast'; 

// --- 状态管理 ---
const currentTab = ref('Rule');
const ruleLoading = ref(false);
const goodsLoading = ref(false);
const goodsOptionsLoading = ref(false);

// --- 列表数据 ---
const ruleList = reactive({ list: [], total: 0 });
const ruleSearchForm = reactive({
 ruleType: 0,
 ruleStatus: undefined,
 pageNum: 1,
 pageSize: 10
});

const goodsList = reactive({ list: [], total: 0 });
const goodsSearchForm = reactive({
 ruleType: 1,
 ruleStatus: undefined,
 pageNum: 1,
 pageSize: 10
});

// 存储可兑换商品列表（用于新增/编辑规则的选择项和列表数据合并）
const goodsOptions = ref([]); 

// --- 弹窗状态 ---
const showDetailModal = ref(false);
const showEditModal = ref(false);
const isEditMode = ref(false);
const submitting = ref(false);
const detailData = ref(null);
const detailType = ref('');

// --- 表单数据 ---
const formData = reactive({
 id: null,
 ruleName: '',
 ruleType: '0',
 ruleStatus: 1,
 exchangeRatio: 10,
 minPoints: 0,
 maxPoints: null,
 foodId: null, 
 foodName: '', 
 requiredPoints: null,
 stockQuantity: 0,
 startTime: '',
 endTime: ''
});

// --- 核心函数 ---
const switchTab = (tab) => {
 if (currentTab.value === tab) return;
 currentTab.value = tab;
 if (tab === 'Rule' && ruleList.list.length === 0) {
  fetchRules('Rule');
 } else if (tab === 'Goods' && goodsList.list.length === 0) {
  fetchRules('Goods');
 }
};

function goBack() {
  // 这是标准的浏览器API，用于返回历史记录中的上一个页面
  window.history.back(); 
}

/**
 * 接口：GET /api/points/exchange-goods 获取可兑换商品列表
 * 同时获取 foodName 和 foodImg，用于列表展示和表单选择
 */
const fetchGoodsOptions = async () => {
    goodsOptionsLoading.value = true;
    try {
        const response = await request.get('/api/points/exchange-goods');
        
        if (response.success && Array.isArray(response.data)) {
            goodsOptions.value = response.data.map(item => ({
                ...item,
                // 确保 foodId 是数字，方便查找
                foodId: parseInt(item.foodId) 
            }));
        } else {
            toast.error(response.message || '获取可兑换商品列表失败');
        }
    } catch (err) {
        console.error('获取可兑换商品列表失败:', err);
        toast.error('获取可兑换商品列表失败，请检查网络');
    } finally {
        goodsOptionsLoading.value = false;
    }
};

/**
 * 接口：GET /api/marketing/points/exchange-rules 查询规则列表
 */
const fetchRules = async (type) => {
 const search = type === 'Rule' ? ruleSearchForm : goodsSearchForm;
 const listData = type === 'Rule' ? ruleList : goodsList;
 const loadingRef = type === 'Rule' ? ruleLoading : goodsLoading;

 loadingRef.value = true;
 
 try {
  const params = {
   ruleType: search.ruleType,
   ruleStatus: search.ruleStatus,
   pageNum: search.pageNum,
   pageSize: search.pageSize
  };
  
  const response = await request.get('/api/marketing/points/exchange-rules', { params });
  
  if (response.success && response.data) {
        const records = response.data.records || response.data || [];
        const total = response.data.total || records.length;

        // --- 核心修改：合并商品图片和名称到 ruleType: 1 的记录中 ---
        const finalRecords = records.map(rule => {
            if (rule.ruleType === 1 && rule.foodId) {
                const good = goodsOptions.value.find(g => g.foodId === rule.foodId);
                // 将 foodName 和 foodImg 合并到规则项中
                return {
                    ...rule,
                    foodName: good?.foodName || '商品不存在',
                    foodImg: good?.foodImg || '/default-image.png' 
                };
            }
            return rule;
        });
        // -----------------------------------------------------------------

    listData.list = finalRecords;
    listData.total = total;
  } else {
   toast.error(response.message || `获取${type === 'Rule' ? '规则' : '商品'}列表失败`);
  }
 } catch (err) {
  console.error(`获取${type === 'Rule' ? '规则' : '商品'}列表失败:`, err);
  toast.error(`获取${type === 'Rule' ? '规则' : '商品'}列表失败，请检查网络`);
 } finally {
  loadingRef.value = false;
 }
};

const handleSearch = (type) => {
 const search = type === 'Rule' ? ruleSearchForm : goodsSearchForm;
 search.pageNum = 1;
 fetchRules(type);
};

const handlePageChange = (type, pageNum) => {
 const search = type === 'Rule' ? ruleSearchForm : goodsSearchForm;
 const totalPages = Math.ceil((type === 'Rule' ? ruleList : goodsList).total / search.pageSize);

 if (pageNum < 1 || pageNum > totalPages) return;
 search.pageNum = pageNum;
 fetchRules(type);
};

const handlePageSizeChange = (type) => {
 const search = type === 'Rule' ? ruleSearchForm : goodsSearchForm;
 search.pageNum = 1;
 fetchRules(type);
};

const getStartIndex = (type) => {
 const search = type === 'Rule' ? ruleSearchForm : goodsSearchForm;
 const list = type === 'Rule' ? ruleList : goodsList;
 if (list.total === 0) return 0;
 return (search.pageNum - 1) * search.pageSize + 1;
};

const getEndIndex = (type) => {
 const search = type === 'Rule' ? ruleSearchForm : goodsSearchForm;
 const list = type === 'Rule' ? ruleList : goodsList;
 return Math.min(search.pageNum * search.pageSize, list.total);
};

// --- 详情弹窗 (略) ---
const showDetail = (item, type) => {
 detailData.value = { ...item };
 detailType.value = type;
 showDetailModal.value = true;
};

const closeDetailModal = () => {
 showDetailModal.value = false;
 detailData.value = null;
};

const handleEditFromDetail = () => {
 closeDetailModal();
 handleEdit(detailData.value);
};

// --- 编辑/新增 ---
const handleAdd = (type) => {
 isEditMode.value = false;
 resetForm();
 formData.ruleType = type === 'Rule' ? '0' : '1';
 showEditModal.value = true;
};

const handleEdit = (item) => {
 isEditMode.value = true;
 Object.assign(formData, {
  id: item.id,
  ruleName: item.ruleName,
  ruleType: item.ruleType.toString(),
  ruleStatus: item.ruleStatus,
  exchangeRatio: item.exchangeRatio,
  minPoints: item.minPoints,
  maxPoints: item.maxPoints,
  foodId: item.foodId,
    // 确保 foodName 字段存在
  foodName: item.foodName, 
  requiredPoints: item.requiredPoints,
  stockQuantity: item.stockQuantity,
  startTime: item.startTime ? formatDateForInput(item.startTime) : '',
  endTime: item.endTime ? formatDateForInput(item.endTime) : ''
 });
 showEditModal.value = true;
};

/**
 * 接口：PUT /api/marketing/points/exchange-rules/{id} (更新规则状态)
 */
const handleToggleStatus = async (item) => {
 const newStatus = item.ruleStatus === 1 ? 0 : 1;
 const isRule = item.ruleType === 0;
 const message = `${isRule ? '规则' : '商品'} "${item.ruleName}" 将被${newStatus === 1 ? '启用' : '禁用'}，确认操作吗？`;
 
 if (!confirm(message)) return;
 
 try {
    // 构造请求体，只包含必要的字段
    const updateData = {
        id: item.id,
        ruleName: item.ruleName,
        ruleType: item.ruleType,
        ruleStatus: newStatus,
        exchangeRatio: item.exchangeRatio,
        minPoints: item.minPoints,
        maxPoints: item.maxPoints,
        foodId: item.foodId,
        requiredPoints: item.requiredPoints,
        stockQuantity: item.stockQuantity,
        startTime: item.startTime,
        endTime: item.endTime,
    };
    
  const response = await request.put(`/api/marketing/points/exchange-rules/${item.id}`, updateData);
  
  if (response.success) {
   toast.success(newStatus === 1 ? `${isRule ? '规则' : '商品'}已启用` : `${isRule ? '规则' : '商品'}已禁用`);
   fetchRules(isRule ? 'Rule' : 'Goods');
  } else {
   toast.error(response.message || '操作失败');
  }
 } catch (err) {
  console.error('切换状态失败:', err);
  toast.error('操作失败，请重试');
 }
};

/**
 * 接口：DELETE /api/marketing/points/exchange-rules/{id} (删除规则)
 */
const handleDelete = async (item) => {
    const isRule = item.ruleType === 0;
    const message = `确认删除${isRule ? '规则' : '商品'} "${item.ruleName}" 吗？删除后不可恢复。`;
    
    if (!confirm(message)) return;

    try {
        const response = await request.delete(`/api/marketing/points/exchange-rules/${item.id}`);
        
        if (response.success) {
            toast.success('删除成功');
            // 重新加载当前列表
            fetchRules(isRule ? 'Rule' : 'Goods');
        } else {
            toast.error(response.message || '删除失败');
        }
    } catch (err) {
        console.error('删除失败:', err);
        toast.error('删除失败，请重试');
    }
};


/**
 * 接口：POST /api/marketing/points/exchange-rules (创建规则)
 * 接口：PUT /api/marketing/points/exchange-rules/{id} (更新规则)
 */
const handleSubmit = async () => {
 if (!formData.ruleName.trim()) {
  toast.error('请输入名称');
  return;
 }

 if (formData.ruleType === '0') {
  if (!formData.exchangeRatio || formData.exchangeRatio < 1) {
   toast.error('请输入有效的兑换比例（需大于0）');
   return;
  }
 } else if (formData.ruleType === '1') {
  if (!formData.foodId) {
        toast.error('请选择兑换商品');
        return;
    }
  if (!formData.requiredPoints || formData.requiredPoints < 1) {
   toast.error('请输入有效的所需积分（需大于0）');
   return;
  }
  if (formData.stockQuantity === null || formData.stockQuantity === undefined || formData.stockQuantity < 0) {
        toast.error('请输入有效的库存数量（需大于等于0）');
        return;
    }
 }

 submitting.value = true;
 
 try {
  // 构造提交数据 (注意清理不需要的字段，以符合 API 定义)
  const submitData = {
   id: formData.id, // PUT 接口需要
   ruleName: formData.ruleName,
   ruleStatus: formData.ruleStatus,
   ruleType: parseInt(formData.ruleType),
   startTime: formData.startTime || null,
   endTime: formData.endTime || null,
   // 积分+现金 (ruleType=0) 字段
   exchangeRatio: formData.ruleType === '0' ? formData.exchangeRatio : undefined,
   minPoints: formData.ruleType === '0' ? (formData.minPoints || 0) : undefined,
   maxPoints: formData.ruleType === '0' ? (formData.maxPoints || null) : undefined,
   // 兑换商品 (ruleType=1) 字段
   foodId: formData.ruleType === '1' ? formData.foodId : undefined,
   requiredPoints: formData.ruleType === '1' ? formData.requiredPoints : undefined,
   stockQuantity: formData.ruleType === '1' ? (formData.stockQuantity || 0) : undefined,
  };

  // 清理掉值为 undefined 的字段（可选，但通常推荐）
    Object.keys(submitData).forEach(key => (submitData[key] === undefined) && delete submitData[key]);


  let response;
  if (isEditMode.value) {
        // 更新 (PUT)
   response = await request.put(`/api/marketing/points/exchange-rules/${formData.id}`, submitData);
  } else {
        // 创建 (POST)
   response = await request.post('/api/marketing/points/exchange-rules', submitData);
  }

  if (response.success) {
   toast.success(isEditMode.value ? '保存成功' : '创建成功');
   closeEditModal();
   fetchRules(submitData.ruleType === 0 ? 'Rule' : 'Goods');
  } else {
   toast.error(response.message || '操作失败');
  }
 } catch (err) {
  console.error('保存失败:', err);
  toast.error('保存失败，请检查网络');
 } finally {
  submitting.value = false;
 }
};

const handleFoodIdChange = (foodId) => {
	const selectedGood = goodsOptions.value.find(g => g.foodId === foodId);
	if (selectedGood) {
		formData.foodName = selectedGood.foodName;
        // 如果是新增模式，自动带入商品的默认所需积分和库存
		if (!isEditMode.value) {
			formData.requiredPoints = selectedGood.requiredPoints || null;
			formData.stockQuantity = selectedGood.stockQuantity || 0;
		}
	} else {
		formData.foodName = '';
	}
};

const handleRuleTypeChange = () => {
 if (formData.ruleType === '0') {
    // 切换到 积分+现金 规则，清空商品相关字段
  formData.foodId = null;
    formData.foodName = '';
  formData.requiredPoints = null;
  formData.stockQuantity = 0;
 } else {
    // 切换到 兑换商品 规则，清空积分+现金相关字段
  formData.exchangeRatio = 10; 
  formData.minPoints = 0;
  formData.maxPoints = null;
 }
};

const closeEditModal = () => {
 showEditModal.value = false;
 resetForm();
};

const resetForm = () => {
 Object.assign(formData, {
  id: null,
  ruleName: '',
  ruleType: '0',
  ruleStatus: 1,
  exchangeRatio: 10,
  minPoints: 0,
  maxPoints: null,
  foodId: null,
    foodName: '',
  requiredPoints: null,
  stockQuantity: 0,
  startTime: '',
  endTime: ''
 });
};

// --- 日期格式化 (保持不变) ---
const formatSimpleDate = (dateString) => {
 if (!dateString) return '永久';
 try {
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', { month: '2-digit', day: '2-digit' });
 } catch {
  return dateString;
 }
};

const formatFullDate = (dateString) => {
 if (!dateString) return '永久';
 try {
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', { 
   year: 'numeric', 
   month: '2-digit', 
   day: '2-digit',
   hour: '2-digit',
   minute: '2-digit'
  }).replace(/\//g, '-');
 } catch {
  return dateString;
 }
};

const formatDateForInput = (dateString) => {
 if (!dateString) return '';
 try {
  const date = new Date(dateString);
  return date.toISOString().slice(0, 16);
 } catch {
  return dateString;
 }
};

// --- 挂载时调用 ---
onMounted(async () => {
  await fetchGoodsOptions(); // 先加载商品选项，确保 fetchRules 可以进行数据合并
 fetchRules('Rule'); // 初始加载规则列表
});
</script>

<style scoped>
.backbutton {
    /* 基础定位 */
    position: fixed;
    top: 0; /* 从顶部开始计算 */
    left: 0; /* 贴近屏幕左侧 */
    z-index: 1001; /* 确保在顶部背景之上 */

    /* 容器居中对齐 */
    height: 100px; /* 匹配 top-background 的高度 */
    display: flex;
    align-items: center; /* 垂直居中 */
    padding: 0 15px; /* 左右内边距，提供空间感 */

    /* 按钮图标/文字的实际样式 */
    /* 假设内部有一个图标或文字，例如 <i class="icon"></i> */
    color: #ffffff; /* 确保文字或图标颜色是白色，与蓝色背景形成高对比度 */
    font-size: 24px; /* 图标大小 */
    cursor: pointer;
    transition: transform 0.2s ease-out; /* 增加点击动画 */
}
/* 基础样式 */
.points-exchange-app {
  max-width: 1200px;
  margin: 0 auto;
  padding: 120px 20px 20px; /* 顶部内边距增加到120px（100px头部高度 + 20px间距） */
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
  box-sizing: border-box;
}

/* 头部样式 */
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

.top-background h1 {
  color: white;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  padding: 0;
}

/* Tab导航 */
.tab-navigation {
  margin-bottom: 30px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  padding: 8px;
}

.tab-container {
  display: flex;
  gap: 4px;
}

.tab-item {
  flex: 1;
  padding: 16px 20px;
  border: none;
  background: transparent;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  font-size: 14px;
  color: #666;
  border-radius: 8px;
  transition: all 0.2s ease;
  position: relative;
}

.tab-item:hover {
  background: #f8f9fa;
}

.tab-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.tab-icon {
  font-size: 18px;
}

.tab-label {
  font-weight: 500;
}

.tab-count {
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
}

.tab-item:not(.active) .tab-count {
  background: #f0f0f0;
  color: #666;
}

/* 列表头部（包含状态筛选和新增按钮） */
.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 16px;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 12px;
}

.filter-select {
  padding: 10px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  background: white;
  color: #333;
  font-size: 14px;
  min-width: 140px;
  cursor: pointer;
  transition: border-color 0.2s;
}

.filter-select:focus {
  outline: none;
  border-color: #667eea;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.primary-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 8px;
  transition: all 0.2s ease;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.primary-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.3);
}

/* 列表容器 */
.list-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

/* 列表项样式 */
.item-list {
  max-height: 600px;
  overflow-y: auto;
}

.list-item {
  padding: 16px 24px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: all 0.2s ease;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.list-item:hover {
  background: #f8f9fa;
}

.list-item:last-child {
  border-bottom: none;
}

.item-main {
  flex: 1;
}

/* 优化：规则布局 - 标题和状态在同一行 */
.rule-header .title-container {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.rule-header .item-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

/* 优化：商品布局 - 更紧凑 */
.goods-header .title-container {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.goods-header .item-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.food-id {
  font-size: 12px;
  color: #999;
  font-weight: normal;
}

.item-tags {
  display: flex;
  gap: 8px;
  align-items: center;
}

.item-status {
  font-size: 12px;
  font-weight: 500;
  padding: 4px 10px;
  border-radius: 12px;
  white-space: nowrap;
}

.item-status.active {
  background: rgba(34, 197, 94, 0.1);
  color: #16a34a;
}

.item-status.inactive {
  background: rgba(239, 68, 68, 0.1);
  color: #dc2626;
}

.stock-warning {
  font-size: 12px;
  color: #f97316;
  background: rgba(249, 115, 22, 0.1);
  padding: 2px 8px;
  border-radius: 10px;
  white-space: nowrap;
}

.item-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-info {
  display: flex;
  gap: 24px;
}

.compact-info {
  gap: 20px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 12px;
  color: #999;
}

.info-value {
  font-size: 14px;
  color: #333;
  font-weight: 500;
}

.info-value.highlight {
  color: #667eea;
  font-weight: 600;
}

.item-meta {
  display: flex;
  gap: 16px;
  font-size: 13px;
  color: #999;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.meta-icon {
  font-size: 12px;
}

.item-actions {
  display: flex;
  gap: 8px;
  margin-left: 20px;
}

.action-btn {
  padding: 8px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  background: white;
  color: #666;
  font-size: 13px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.2s;
  white-space: nowrap;
}

.action-btn:hover {
  transform: translateY(-1px);
}

.action-btn.edit:hover {
  background: #f8f9fa;
  border-color: #ccc;
}

.action-btn.status.enable {
  border-color: #16a34a;
  color: #16a34a;
}

.action-btn.status.enable:hover {
  background: rgba(34, 197, 94, 0.1);
}

.action-btn.status.disable {
  border-color: #dc2626;
  color: #dc2626;
}

.action-btn.status.disable:hover {
  background: rgba(239, 68, 68, 0.1);
}

/* 空状态 */
.empty-state {
  padding: 80px 20px;
  text-align: center;
  color: #999;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty-state h3 {
  font-size: 20px;
  font-weight: 600;
  color: #666;
  margin: 0 0 8px 0;
}

.empty-state p {
  font-size: 14px;
  margin: 0 0 24px 0;
}

/* 加载状态 */
.loading-state {
  padding: 60px 20px;
  text-align: center;
  color: #999;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f0f0f0;
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 分页样式 */
.pagination {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-top: 1px solid #f0f0f0;
  background: #f8f9fa;
}

.pagination-info {
  font-size: 14px;
  color: #666;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: 8px;
}

.page-btn {
  width: 36px;
  height: 36px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  background: white;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.page-btn:hover:not(:disabled) {
  border-color: #667eea;
  color: #667eea;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-current {
  font-size: 14px;
  font-weight: 600;
  color: #1a1a1a;
  min-width: 20px;
  text-align: center;
}

.page-total {
  font-size: 14px;
  color: #999;
  margin: 0 8px;
}

.page-size {
  padding: 8px 12px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  background: white;
  color: #666;
  font-size: 14px;
  cursor: pointer;
  margin-left: 12px;
}

/* 弹窗样式 */
.detail-modal,
.edit-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 16px;
  width: 100%;
  max-width: 800px;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  animation: modalSlideIn 0.3s ease-out;
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 24px;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.modal-close {
  width: 32px;
  height: 32px;
  border: none;
  background: #f8f9fa;
  border-radius: 8px;
  color: #666;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
}

.modal-close:hover {
  background: #f0f0f0;
}

/* 详情弹窗样式 */
.modal-body {
  padding: 24px;
}

.detail-section {
  margin-bottom: 32px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0 0 20px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.section-icon {
  font-size: 20px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.detail-item label {
  font-size: 13px;
  color: #999;
  font-weight: 500;
}

.detail-item span {
  font-size: 15px;
  color: #333;
}

.detail-item .highlight {
  color: #667eea;
  font-weight: 600;
}

.detail-item .status-active {
  color: #16a34a;
  font-weight: 500;
}

.detail-item .status-inactive {
  color: #dc2626;
  font-weight: 500;
}

.detail-item .stock-low {
  color: #f97316;
}

.detail-item .warning-text {
  font-size: 12px;
  opacity: 0.8;
}

.action-section {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 24px;
  border-top: 1px solid #f0f0f0;
}

/* 编辑弹窗样式 */
.form-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  display: flex;
  align-items: center;
  gap: 4px;
}

.form-label.required::after {
  content: '*';
  color: #dc2626;
}

.form-input {
  padding: 12px 16px;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  font-size: 14px;
  color: #333;
  background: white;
  transition: all 0.2s;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
}

.input-with-suffix {
  position: relative;
  display: flex;
  align-items: center;
}

.input-with-suffix .form-input {
  flex: 1;
  padding-right: 80px;
}

.input-suffix {
  position: absolute;
  right: 16px;
  color: #999;
  font-size: 14px;
}

.type-selector {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  margin-top: 8px;
}

.type-option {
  padding: 16px;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  background: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.type-option:hover {
  border-color: #667eea;
}

.type-option.selected {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.type-icon {
  font-size: 24px;
}

.type-label {
  font-size: 14px;
  font-weight: 500;
  color: #333;
}

.status-selector {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}

.status-option {
  flex: 1;
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.2s;
}

.status-option:hover {
  border-color: #ccc;
}

.status-option.selected {
  border-color: #667eea;
  background: rgba(102, 126, 234, 0.05);
}

.status-indicator {
  width: 10px;
  height: 10px;
  border-radius: 50%;
}

.status-indicator.active {
  background: #16a34a;
}

.status-indicator.inactive {
  background: #dc2626;
}

.status-label {
  font-size: 14px;
  color: #333;
}

.form-row {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 24px;
  border-top: 1px solid #f0f0f0;
  background: #f8f9fa;
}

.btn {
  padding: 12px 24px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.2s;
  border: none;
  min-width: 100px;
}

.btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.2);
}

.btn.primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(102, 126, 234, 0.3);
}

.btn.secondary {
  background: white;
  color: #666;
  border: 1px solid #e0e0e0;
}

.btn.secondary:hover {
  background: #f8f9fa;
  border-color: #ccc;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app-header {
    flex-direction: column;
    gap: 16px;
  }
  
  .list-header {
    flex-direction: column;
    align-items: stretch;
    gap: 12px;
  }
  
  .filter-group {
    width: 100%;
  }
  
  .filter-select {
    flex: 1;
  }
  
  .header-actions {
    width: 100%;
  }
  
  .primary-btn {
    width: 100%;
    justify-content: center;
  }
  
  .rule-header .title-container,
  .goods-header .title-container {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .goods-header .title-container {
    align-items: stretch;
  }
  
  .item-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .item-info {
    flex-direction: column;
    gap: 12px;
  }
  
  .item-actions {
    width: 100%;
    margin-left: 0;
    margin-top: 16px;
    justify-content: flex-end;
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
  
  .form-row {
    grid-template-columns: 1fr;
    gap: 16px;
  }
  
  .type-selector {
    grid-template-columns: 1fr;
  }
  
  .modal-content {
    max-height: 95vh;
  }
  
  .list-item {
    flex-direction: column;
    align-items: stretch;
  }
  
  .pagination {
    flex-direction: column;
    gap: 16px;
    align-items: stretch;
  }
  
  .pagination-controls {
    justify-content: center;
  }
}

@media (max-width: 480px) {
  .list-item {
    padding: 12px 16px;
  }
  
  .item-content {
    gap: 12px;
  }
  
  .item-meta {
    flex-wrap: wrap;
    gap: 8px;
  }
  
  .item-actions {
    flex-direction: column;
    gap: 8px;
  }
  
  .action-btn {
    width: 100%;
    justify-content: center;
  }
}
</style>