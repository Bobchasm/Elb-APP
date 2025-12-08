<template>
  <div class="backbutton" @click="goBack">
    <i class="fas fa-arrow-left"></i> 
</div>
  <div class="sub-page-container">
    <div class="top-background">
      <div class="top-content">
        <h1>积分预警配置</h1>
        <p class="top-subtitle">设置积分到期提醒规则，及时通知用户避免积分过期</p>
      </div>
    </div>
    
    <div class="page-content">
      <div class="content-panel">
        <div class="config-form" v-if="!loading">
          <!-- 启用开关 -->
          <div class="form-section">
            <div class="switch-row">
              <span class="switch-label">启用预警功能</span>
              <label class="switch">
                <input type="checkbox" v-model="configData.isEnabled" :true-value="1" :false-value="0">
                <span class="slider"></span>
              </label>
            </div>
            <p class="form-hint">开启后，系统将在积分到期前按设定的规则发送预警通知</p>
          </div>

          <!-- 预警天数 -->
          <div class="form-section">
            <label class="form-label">
              提前预警天数
              <span class="required">*</span>
            </label>
            <div class="input-group">
              <input 
                type="number" 
                v-model.number="configData.alertDays" 
                :disabled="!configData.isEnabled"
                min="1" 
                max="30"
                placeholder="请输入提前预警的天数"
                class="form-input"
              >
              <span class="input-suffix">天</span>
            </div>
            <p class="form-hint">设置积分到期前多少天开始预警（1-30天）</p>
          </div>

          <!-- 预警周期 -->
          <div class="form-section">
            <label class="form-label">
              预警周期
              <span class="required">*</span>
            </label>
            <div class="input-group">
              <input 
                type="number" 
                v-model.number="configData.alertCycle" 
                :disabled="!configData.isEnabled"
                min="1" 
                max="30"
                placeholder="请输入预警周期"
                class="form-input"
              >
              <span class="input-suffix">天</span>
            </div>
            <p class="form-hint">
              设置每隔多少天重复预警一次（输入1表示每天预警，留空表示只预警一次）
            </p>
          </div>

          <!-- 短信模板 -->
          <div class="form-section">
            <label class="form-label">
              短信模板
              <span class="required">*</span>
            </label>
            <textarea 
              v-model="configData.smsTemplate" 
              :disabled="!configData.isEnabled"
              placeholder="请输入短信模板内容"
              class="form-textarea"
              rows="4"
            ></textarea>
            <div class="template-vars">
              <p class="vars-title">可用变量：</p>
              <div class="vars-tags">
                <span class="var-tag" @click="insertVariable('username')">{username}</span>
                <span class="var-tag" @click="insertVariable('points')">{points}</span>
                <span class="var-tag" @click="insertVariable('expireDate')">{expireDate}</span>
              </div>
              <p class="var-hint">点击变量可插入到模板中</p>
            </div>
            <p class="form-hint">短信模板支持变量替换，系统会自动填充实际值</p>
            
            <!-- 预览区域 -->
            <div class="preview-section" v-if="configData.smsTemplate">
              <h4>预览效果：</h4>
              <div class="preview-content">
                {{ getPreviewText() }}
              </div>
            </div>
          </div>

          <!-- 操作按钮 -->
          <div class="form-actions">
            <button 
              class="warning-btn" 
              @click="saveConfig"
              :disabled="saving || !configData.isEnabled"
            >
              <span v-if="saving" class="loading"></span>
              {{ saving ? '保存中...' : '保存配置' }}
            </button>
            <button 
              class="cancel-btn" 
              @click="resetForm"
              :disabled="saving"
            >
              重置
            </button>
          </div>
        </div>

        <!-- 加载状态 -->
        <div class="loading-state" v-if="loading">
          <div class="loading-spinner"></div>
          <p>加载配置中...</p>
        </div>

        <!-- 错误状态 -->
        <div class="error-state" v-if="error">
          <div class="error-icon">⚠️</div>
          <p>加载配置失败</p>
          <button class="retry-btn" @click="fetchConfig">重试</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { toast } from '../utils/toast';
import request from '../utils/request';

const router = useRouter();
const loading = ref(false);
const saving = ref(false);
const error = ref(false);

// 配置数据
const configData = ref({
  id: null,
  alertDays: 7,
  alertCycle: 1,
  smsTemplate: '尊敬的{username}，您有{points}积分将于{expireDate}过期，请及时使用。',
  isEnabled: 1
});

// 默认配置
const defaultConfig = {
  alertDays: 7,
  alertCycle: 1,
  smsTemplate: '尊敬的{username}，您有{points}积分将于{expireDate}过期，请及时使用。',
  isEnabled: 1
};

function goBack() {
  // 这是标准的浏览器API，用于返回历史记录中的上一个页面
  window.history.back(); 
}

// 获取预警配置
const fetchConfig = async () => {
  loading.value = true;
  error.value = false;
  
  try {
    const response = await request.get('/api/marketing/points/alert-config');
    
    if (response.success && response.data) {
      // 处理可能为null的值
      configData.value = {
        ...defaultConfig,
        ...response.data,
        isEnabled: response.data.isEnabled ? 1 : 0,
        alertCycle: response.data.alertCycle === null ? '' : response.data.alertCycle
      };
    } else {
      // 如果没有数据，使用默认配置
      configData.value = { ...defaultConfig };
    }
  } catch (err) {
    console.error('获取预警配置失败:', err);
    error.value = true;
    toast.error('获取配置失败，请重试');
  } finally {
    loading.value = false;
  }
};

// 保存配置
const saveConfig = async () => {
  // 验证必填字段
  if (!configData.value.alertDays || configData.value.alertDays < 1 || configData.value.alertDays > 30) {
    toast.error('请填写有效的预警天数（1-30）');
    return;
  }

  if (!configData.value.smsTemplate?.trim()) {
    toast.error('请填写短信模板');
    return;
  }

  saving.value = true;
  
  try {
    // 处理alertCycle为null的情况
    const submitData = {
      ...configData.value,
      alertCycle: configData.value.alertCycle === '' ? null : configData.value.alertCycle
    };
    
    const response = await request.put('/api/marketing/points/alert-config', submitData);
    
    if (response.success) {
      toast.success('配置保存成功');
      // 重新获取最新配置
      await fetchConfig();
    } else {
      toast.error(response.message || '保存失败');
    }
  } catch (err) {
    console.error('保存配置失败:', err);
    toast.error('保存失败，请检查网络或稍后重试');
  } finally {
    saving.value = false;
  }
};

// 重置表单
const resetForm = () => {
  if (confirm('确定要重置所有修改吗？')) {
    fetchConfig();
  }
};

// 插入变量到短信模板
const insertVariable = (variable) => {
  if (!configData.value.isEnabled) return;
  
  const textarea = document.querySelector('.form-textarea');
  const start = textarea.selectionStart;
  const end = textarea.selectionEnd;
  const variableText = `{${variable}}`;
  
  configData.value.smsTemplate = 
    configData.value.smsTemplate.substring(0, start) + 
    variableText + 
    configData.value.smsTemplate.substring(end);
  
  // 焦点回到输入框并移动光标
  setTimeout(() => {
    textarea.focus();
    const newPosition = start + variableText.length;
    textarea.setSelectionRange(newPosition, newPosition);
  }, 0);
};

// 获取预览文本
const getPreviewText = () => {
  let text = configData.value.smsTemplate;
  text = text.replace(/{username}/g, '张三');
  text = text.replace(/{points}/g, '150');
  text = text.replace(/{expireDate}/g, '2024-12-31');
  return text;
};

// 初始化
onMounted(() => {
  fetchConfig();
});
</script>

<style scoped>
/* 基础样式 */
.sub-page-container {
  min-height: 100vh;
  background: #f5f7fa;
  margin: 0;
  padding: 110px 20px 20px; /* 增加顶部内边距，确保内容在固定头部下方 */
  width: 100%;
  box-sizing: border-box;
  position: relative;
  z-index: 1;
}

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

.top-background h1 {
  color: white;
  font-size: 20px;
  font-weight: 600;
  margin: 0;
  padding: 0;
}

.top-subtitle {
  display: none; /* 隐藏副标题以保持简洁 */
}

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

/* 页面内容区域 */
.page-content {
  max-width: 600px;
  margin: 0 auto;
  padding: 20px;
  width: 100%;
  box-sizing: border-box;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

/* 内容面板 */
.content-panel {
  background: #ffffff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  overflow: hidden;
}

/* 表单样式 */
.config-form {
  padding: 0;
}

.form-section {
  margin: 0;
  padding: 20px;
  border-bottom: 1px solid #f1f3f6;
  background: #ffffff;
}

.form-section:last-child {
  border-bottom: none;
}

.switch-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.switch-label {
  font-size: 15px;
  font-weight: 600;
  color: #1f2937;
}

.switch {
  position: relative;
  display: inline-block;
  width: 48px;
  height: 24px;
}

.switch input {
  opacity: 0;
  width: 0;
  height: 0;
}

.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #d1d5db;
  transition: .4s;
  border-radius: 24px;
}

.slider:before {
  position: absolute;
  content: "";
  height: 18px;
  width: 18px;
  left: 3px;
  bottom: 3px;
  background-color: white;
  transition: .4s;
  border-radius: 50%;
}

input:checked + .slider {
  background-color: #ff6b6b;
}

input:checked + .slider:before {
  transform: translateX(24px);
}

input:disabled + .slider {
  background-color: #e5e7eb;
  cursor: not-allowed;
}

.form-hint {
  font-size: 13px;
  color: #6b7280;
  margin-top: 8px;
  line-height: 1.4;
}

.form-label {
  display: block;
  font-size: 14px;
  font-weight: 600;
  color: #374151;
  margin-bottom: 8px;
}

.required {
  color: #ff6b6b;
  margin-left: 2px;
}

.input-group {
  position: relative;
  display: flex;
  align-items: center;
}

.form-input {
  width: 100%;
  padding: 10px 40px 10px 12px;
  border: 1.5px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  transition: all 0.2s;
  background: white;
  color: #1f2937;
}

.form-input:focus {
  outline: none;
  border-color: #ff6b6b;
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1);
}

.form-input:disabled {
  background-color: #f9fafb;
  color: #9ca3af;
  cursor: not-allowed;
  border-color: #e5e7eb;
}

.input-suffix {
  position: absolute;
  right: 12px;
  color: #6b7280;
  font-size: 14px;
}

.form-textarea {
  width: 100%;
  padding: 12px;
  border: 1.5px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  line-height: 1.5;
  resize: vertical;
  transition: all 0.2s;
  background: white;
  color: #1f2937;
  font-family: inherit;
  min-height: 100px;
}

.form-textarea:focus {
  outline: none;
  border-color: #ff6b6b;
  box-shadow: 0 0 0 3px rgba(255, 107, 107, 0.1);
}

.form-textarea:disabled {
  background-color: #f9fafb;
  color: #9ca3af;
  cursor: not-allowed;
  border-color: #e5e7eb;
}

/* 模板变量样式 */
.template-vars {
  margin: 12px 0;
  padding: 12px;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.vars-title {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 8px;
  font-weight: 500;
}

.vars-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}

.var-tag {
  padding: 6px 10px;
  background: rgba(255, 107, 107, 0.1);
  color: #ff6b6b;
  border: 1px solid rgba(255, 107, 107, 0.2);
  border-radius: 6px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.var-tag:hover {
  background: rgba(255, 107, 107, 0.2);
  transform: translateY(-1px);
}

.var-hint {
  font-size: 12px;
  color: #9ca3af;
  margin: 0;
}

/* 预览区域 */
.preview-section {
  margin-top: 16px;
  padding: 16px;
  background: #f9fafb;
  border-radius: 8px;
  border: 1px solid #e5e7eb;
}

.preview-section h4 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #374151;
  font-weight: 600;
}

.preview-content {
  padding: 12px;
  background: white;
  border-radius: 6px;
  color: #4b5563;
  line-height: 1.6;
  font-size: 14px;
  border: 1px solid #e5e7eb;
}

/* 操作按钮 */
.form-actions {
  display: flex;
  justify-content: center;
  gap: 16px;
  margin-top: 0;
  padding: 20px;
  background: #ffffff;
  border-top: 1px solid #f1f3f6;
}

.warning-btn {
  padding: 12px 28px;
  background-color: #ff6b6b;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.2s;
  min-width: 120px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 8px;
  flex: 1;
  max-width: 200px;
}

.warning-btn:hover:not(:disabled) {
  background-color: #ee5a52;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.2);
}

.warning-btn:active:not(:disabled) {
  transform: translateY(0);
}

.warning-btn:disabled {
  background-color: #d1d5db;
  color: #9ca3af;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.cancel-btn {
  padding: 12px 28px;
  background-color: #ffffff;
  color: #6b7280;
  border: 1.5px solid #d1d5db;
  border-radius: 8px;
  cursor: pointer;
  font-size: 15px;
  font-weight: 600;
  transition: all 0.2s;
  min-width: 120px;
  flex: 1;
  max-width: 200px;
}

.cancel-btn:hover:not(:disabled) {
  background-color: #f9fafb;
  color: #4b5563;
  border-color: #9ca3af;
}

.cancel-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

/* 加载动画 */
.loading {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 加载状态 */
.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  padding: 40px 20px;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid #f3f4f6;
  border-top-color: #ff6b6b;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

.loading-state p {
  color: #6b7280;
  font-size: 14px;
}

/* 错误状态 */
.error-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 300px;
  text-align: center;
  padding: 40px 20px;
}

.error-icon {
  font-size: 40px;
  margin-bottom: 16px;
  opacity: 0.7;
}

.error-state p {
  color: #ef4444;
  margin-bottom: 20px;
  font-size: 15px;
  font-weight: 500;
}

.retry-btn {
  padding: 10px 20px;
  background-color: #ff6b6b;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: background-color 0.2s;
}

.retry-btn:hover {
  background-color: #ee5a52;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .page-content {
    padding: 16px;
  }
  
  .top-background h1 {
    font-size: 1.3rem;
  }
  
  .top-subtitle {
    font-size: 0.8rem;
  }
  
  .form-section {
    padding: 16px;
  }
  
  .form-actions {
    flex-direction: column;
    gap: 12px;
  }
  
  .warning-btn,
  .cancel-btn {
    max-width: 100%;
    width: 100%;
  }
  
  .vars-tags {
    flex-wrap: wrap;
  }
}

@media (max-width: 480px) {
  .top-background {
    height: 85px;
  }
  
  .top-background h1 {
    font-size: 1.2rem;
  }
  
  .top-subtitle {
    display: none;
  }
  
  .page-content {
    padding: 12px;
  }
}
</style>