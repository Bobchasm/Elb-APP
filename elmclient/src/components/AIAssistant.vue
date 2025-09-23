<template>
  <div class="ai-assistant">
    <!-- AI助手触发按钮 -->
    <div class="ai-trigger" @click="toggleChat" :class="{ active: showChat }">
      <i class="fa fa-robot"></i>
      <span v-if="!showChat">AI助手</span>
    </div>

    <!-- AI聊天窗口 -->
    <transition name="slide-up">
      <div v-if="showChat" class="ai-chat-container">
        <!-- 聊天头部 -->
        <div class="chat-header">
          <div class="header-left">
            <i class="fa fa-robot"></i>
            <span>饿了么AI助手</span>
          </div>
          <div class="header-right">
            <button @click="showSettings = !showSettings" class="btn-settings" title="设置">
              <i class="fa fa-cog"></i>
            </button>
            <button @click="clearChat" class="btn-clear" title="清空对话">
              <i class="fa fa-trash"></i>
            </button>
            <button @click="toggleChat" class="btn-close" title="关闭">
              <i class="fa fa-times"></i>
            </button>
          </div>
        </div>

        <!-- 设置面板 -->
        <div v-if="showSettings" class="settings-panel">
          <h4>🔧 AI助手设置</h4>
          
          <div class="setting-item">
            <label>阿里云API密钥：</label>
            <input 
              v-model="tempApiKey" 
              type="password" 
              placeholder="sk-xxxxxxxxxxxxxxxx"
              class="api-key-input"
            />
            <div class="setting-buttons">
              <button @click="saveApiKey" class="btn-save">保存</button>
              <button @click="testApiKey" class="btn-test" :disabled="!tempApiKey">测试</button>
            </div>
          </div>

          <div class="setting-item">
            <div class="api-status">
              <span :class="['status-indicator', apiStatus.type]"></span>
              <span>{{ apiStatus.message }}</span>
            </div>
          </div>

          <div class="setting-help">
            <p>📖 <strong>如何获取API密钥：</strong></p>
            <ol>
              <li>访问 <a href="https://dashscope.console.aliyun.com/" target="_blank">阿里云DashScope控制台</a></li>
              <li>开通通义千问服务</li>
              <li>创建API Key</li>
              <li>复制并粘贴到上方输入框</li>
            </ol>
            <p class="security-note">🔒 您的API密钥仅存储在本地浏览器中，不会上传到服务器</p>
          </div>
        </div>

        <!-- 聊天消息区域 -->
        <div v-if="!showSettings" class="chat-messages" ref="messagesContainer">
          <div v-if="messages.length === 0" class="welcome-message">
            <div class="welcome-icon">🍔</div>
            <h3>您好！我是饿了么AI助手</h3>
            <p>我可以帮您：</p>
            <ul>
              <li>🔍 推荐美食和商家</li>
              <li>📋 查看订单状态</li>
              <li>❓ 解答使用问题</li>
              <li>💡 提供优惠建议</li>
            </ul>
          </div>

          <div
            v-for="(message, index) in messages"
            :key="index"
            :class="['message', message.type]"
          >
            <div class="message-avatar">
              <i :class="message.type === 'user' ? 'fa fa-user' : 'fa fa-robot'"></i>
            </div>
            <div class="message-content">
              <div class="message-text" v-html="formatMessage(message.content)"></div>
              <div class="message-time">{{ formatTime(message.timestamp) }}</div>
            </div>
          </div>

          <div v-if="isTyping" class="message ai typing">
            <div class="message-avatar">
              <i class="fa fa-robot"></i>
            </div>
            <div class="message-content">
              <div class="typing-indicator">
                <span></span>
                <span></span>
                <span></span>
              </div>
            </div>
          </div>
        </div>

        <!-- 输入区域 -->
        <div class="chat-input">
          <div class="input-container">
            <input
              v-model="inputMessage"
              @keyup.enter="sendMessage"
              placeholder="输入您的问题..."
              :disabled="isTyping"
              ref="messageInput"
            />
            <button
              @click="sendMessage"
              :disabled="!inputMessage.trim() || isTyping"
              class="send-btn"
            >
              <i class="fa fa-paper-plane"></i>
            </button>
          </div>
          
          <!-- 快捷问题 -->
          <div v-if="messages.length === 0" class="quick-questions">
            <button
              v-for="question in quickQuestions"
              :key="question"
              @click="askQuickQuestion(question)"
              class="quick-btn"
            >
              {{ question }}
            </button>
          </div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import { ref, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import aiService from '../services/aiService'

export default {
  name: 'AIAssistant',
  setup() {
    const router = useRouter()
    const showChat = ref(false)
    const messages = ref([])
    const inputMessage = ref('')
    const isTyping = ref(false)
    const messagesContainer = ref(null)
    const messageInput = ref(null)
    
    // 设置相关
    const showSettings = ref(false)
    const tempApiKey = ref('')
    const apiStatus = ref({
      type: 'warning',
      message: '未配置API密钥 - 当前使用本地回复'
    })

    // 快捷问题
    const quickQuestions = ref([
      '推荐附近的美食',
      '今天有什么优惠',
      '如何查看订单',
      '配送费怎么算'
    ])

    // AI服务配置 - 现在通过aiService统一管理
    const AI_CONFIG = {
      maxRetries: 3,
      timeout: 10000
    }

    // 切换聊天窗口
    const toggleChat = async () => {
      showChat.value = !showChat.value
      if (showChat.value) {
        await nextTick()
        messageInput.value?.focus()
      }
    }

    // 清空对话
    const clearChat = () => {
      messages.value = []
    }

    // 发送消息
    const sendMessage = async () => {
      if (!inputMessage.value.trim() || isTyping.value) return

      const userMessage = {
        type: 'user',
        content: inputMessage.value.trim(),
        timestamp: new Date()
      }

      messages.value.push(userMessage)
      const userInput = inputMessage.value.trim()
      inputMessage.value = ''

      await scrollToBottom()
      await getAIResponse(userInput)
    }

    // 快捷问题
    const askQuickQuestion = (question) => {
      inputMessage.value = question
      sendMessage()
    }

    // 获取AI响应
    const getAIResponse = async (userInput) => {
      isTyping.value = true

      try {
        await simulateTyping()
        
        // 使用AI服务获取回复
        const response = await aiService.sendMessage(userInput, messages.value)
        addAIMessage(response)

      } catch (error) {
        console.error('AI响应失败:', error)
        addAIMessage('抱歉，我暂时无法回答您的问题。请稍后再试，或者联系客服获取帮助。')
      } finally {
        isTyping.value = false
      }
    }


    // 模拟打字效果
    const simulateTyping = () => {
      return new Promise(resolve => {
        setTimeout(resolve, 1000 + Math.random() * 1000)
      })
    }

    // 添加AI消息
    const addAIMessage = (content) => {
      messages.value.push({
        type: 'ai',
        content,
        timestamp: new Date()
      })
      scrollToBottom()
    }

    // 滚动到底部
    const scrollToBottom = async () => {
      await nextTick()
      if (messagesContainer.value) {
        messagesContainer.value.scrollTop = messagesContainer.value.scrollHeight
      }
    }

    // 格式化消息内容
    const formatMessage = (content) => {
      return content
        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
        .replace(/\n/g, '<br>')
        .replace(/•/g, '•')
    }

    // 格式化时间
    const formatTime = (date) => {
      return date.toLocaleTimeString('zh-CN', { 
        hour: '2-digit', 
        minute: '2-digit' 
      })
    }

    // 保存API密钥
    const saveApiKey = () => {
      if (!tempApiKey.value.trim()) {
        apiStatus.value = {
          type: 'error',
          message: '请输入API密钥'
        }
        return
      }

      // 保存到本地存储和全局变量
      localStorage.setItem('aliyun_api_key', tempApiKey.value.trim())
      window.ALIYUN_API_KEY = tempApiKey.value.trim()

      apiStatus.value = {
        type: 'success',
        message: 'API密钥已保存，现在可以使用阿里云AI回复'
      }

      console.log('✅ 阿里云API密钥已保存')
    }

    // 测试API密钥
    const testApiKey = async () => {
      if (!tempApiKey.value.trim()) return

      apiStatus.value = {
        type: 'loading',
        message: '正在测试API密钥...'
      }

      try {
        // 临时设置API密钥进行测试
        const originalKey = window.ALIYUN_API_KEY
        window.ALIYUN_API_KEY = tempApiKey.value.trim()

        const testReply = await aiService.callAliyunAPI('你好', [])
        
        if (testReply) {
          apiStatus.value = {
            type: 'success',
            message: 'API密钥测试成功！AI服务可正常使用'
          }
        } else {
          throw new Error('API返回为空')
        }

        // 恢复原始密钥
        window.ALIYUN_API_KEY = originalKey
      } catch (error) {
        console.error('API测试失败:', error)
        apiStatus.value = {
          type: 'error',
          message: `API测试失败: ${error.message}`
        }

        // 恢复原始密钥
        window.ALIYUN_API_KEY = localStorage.getItem('aliyun_api_key')
      }
    }

    // 初始化API密钥状态
    const initApiKeyStatus = () => {
      const savedKey = localStorage.getItem('aliyun_api_key')
      if (savedKey) {
        window.ALIYUN_API_KEY = savedKey
        tempApiKey.value = savedKey
        apiStatus.value = {
          type: 'success',
          message: '已配置API密钥 - 使用阿里云AI回复'
        }
      }
    }

    // 初始化
    initApiKeyStatus()

    return {
      showChat,
      messages,
      inputMessage,
      isTyping,
      messagesContainer,
      messageInput,
      quickQuestions,
      showSettings,
      tempApiKey,
      apiStatus,
      toggleChat,
      clearChat,
      sendMessage,
      askQuickQuestion,
      formatMessage,
      formatTime,
      saveApiKey,
      testApiKey
    }
  }
}
</script>

<style scoped>
.ai-assistant {
  position: fixed;
  bottom: 300px; /* 从20px调整到80px，往上移动 */
  right: 20px;
  z-index: 9999; /* 调整到最高层级，确保不被遮挡 */
}

/* 触发按钮 */
.ai-trigger {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 25px;
  padding: 12px 20px;
  cursor: pointer;
  box-shadow: 0 4px 20px rgba(102, 126, 234, 0.4);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.ai-trigger:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 25px rgba(102, 126, 234, 0.6);
}

.ai-trigger.active {
  border-radius: 50%;
  padding: 15px;
  width: 50px;
  height: 50px;
  justify-content: center;
}

.ai-trigger.active span {
  display: none;
}

/* 聊天容器 */
.ai-chat-container {
  position: absolute;
  bottom: 70px;
  right: 0;
  width: 350px;
  height: 500px;
  background: white;
  border-radius: 15px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  overflow: hidden;
  z-index: 10000; /* 确保聊天窗口也在最前面 */
}

/* 聊天头部 */
.chat-header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
  font-weight: 600;
}

.header-right {
  display: flex;
  gap: 10px;
}

.btn-settings, .btn-clear, .btn-close {
  background: rgba(255, 255, 255, 0.2);
  border: none;
  color: white;
  width: 30px;
  height: 30px;
  border-radius: 50%;
  cursor: pointer;
  transition: background 0.3s;
}

.btn-settings:hover, .btn-clear:hover, .btn-close:hover {
  background: rgba(255, 255, 255, 0.3);
}

/* 消息区域 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.welcome-message {
  text-align: center;
  color: #666;
}

.welcome-icon {
  font-size: 40px;
  margin-bottom: 10px;
}

.welcome-message h3 {
  margin: 10px 0;
  color: #333;
}

.welcome-message ul {
  text-align: left;
  display: inline-block;
  margin: 15px 0;
}

.welcome-message li {
  margin: 5px 0;
  font-size: 14px;
}

/* 消息样式 */
.message {
  display: flex;
  gap: 10px;
  animation: fadeInUp 0.3s ease;
}

.message.user {
  flex-direction: row-reverse;
}

.message-avatar {
  width: 35px;
  height: 35px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 14px;
  flex-shrink: 0;
}

.message.user .message-avatar {
  background: #667eea;
  color: white;
}

.message.ai .message-avatar {
  background: #f0f0f0;
  color: #666;
}

.message-content {
  max-width: 70%;
}

.message.user .message-content {
  text-align: right;
}

.message-text {
  background: #f0f0f0;
  padding: 10px 15px;
  border-radius: 18px;
  font-size: 14px;
  line-height: 1.4;
}

.message.user .message-text {
  background: #667eea;
  color: white;
  border-bottom-right-radius: 8px;
}

.message.ai .message-text {
  border-bottom-left-radius: 8px;
}

.message-time {
  font-size: 11px;
  color: #999;
  margin-top: 5px;
}

/* 打字指示器 */
.typing-indicator {
  display: flex;
  gap: 4px;
  padding: 15px;
  background: #f0f0f0;
  border-radius: 18px;
  border-bottom-left-radius: 8px;
}

.typing-indicator span {
  width: 6px;
  height: 6px;
  background: #999;
  border-radius: 50%;
  animation: typing 1.4s infinite;
}

.typing-indicator span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-indicator span:nth-child(3) {
  animation-delay: 0.4s;
}

/* 输入区域 */
.chat-input {
  padding: 15px 20px;
  border-top: 1px solid #f0f0f0;
}

.input-container {
  display: flex;
  gap: 10px;
  align-items: center;
}

.input-container input {
  flex: 1;
  border: 1px solid #ddd;
  border-radius: 20px;
  padding: 10px 15px;
  outline: none;
  font-size: 14px;
}

.input-container input:focus {
  border-color: #667eea;
}

.send-btn {
  background: #667eea;
  color: white;
  border: none;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  cursor: pointer;
  transition: background 0.3s;
}

.send-btn:hover:not(:disabled) {
  background: #5a67d8;
}

.send-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

/* 快捷问题 */
.quick-questions {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.quick-btn {
  background: #f8f9fa;
  border: 1px solid #e9ecef;
  color: #495057;
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.quick-btn:hover {
  background: #667eea;
  color: white;
  border-color: #667eea;
}

/* 动画 */
.slide-up-enter-active, .slide-up-leave-active {
  transition: all 0.3s ease;
}

.slide-up-enter-from {
  transform: translateY(20px);
  opacity: 0;
}

.slide-up-leave-to {
  transform: translateY(20px);
  opacity: 0;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-10px);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .ai-chat-container {
    width: 90vw;
    height: 70vh;
    right: 5vw;
  }
  
  .ai-assistant {
    right: 15px;
    bottom: 15px;
  }
}

/* 设置面板样式 */
.settings-panel {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
}

.settings-panel h4 {
  margin: 0 0 20px 0;
  color: #333;
  font-size: 16px;
}

.setting-item {
  margin-bottom: 20px;
}

.setting-item label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #555;
}

.api-key-input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 6px;
  font-size: 14px;
  margin-bottom: 10px;
}

.api-key-input:focus {
  outline: none;
  border-color: #667eea;
}

.setting-buttons {
  display: flex;
  gap: 10px;
}

.btn-save, .btn-test {
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  transition: background 0.3s;
}

.btn-save {
  background: #667eea;
  color: white;
}

.btn-save:hover {
  background: #5a67d8;
}

.btn-test {
  background: #f8f9fa;
  color: #495057;
  border: 1px solid #dee2e6;
}

.btn-test:hover:not(:disabled) {
  background: #e9ecef;
}

.btn-test:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.api-status {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px;
  border-radius: 6px;
  font-size: 14px;
  margin-bottom: 15px;
}

.status-indicator {
  width: 8px;
  height: 8px;
  border-radius: 50%;
}

.status-indicator.success {
  background: #28a745;
}

.status-indicator.error {
  background: #dc3545;
}

.status-indicator.warning {
  background: #ffc107;
}

.status-indicator.loading {
  background: #17a2b8;
  animation: pulse 1s infinite;
}

.setting-help {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 6px;
  font-size: 13px;
  line-height: 1.5;
}

.setting-help ol {
  margin: 10px 0;
  padding-left: 20px;
}

.setting-help li {
  margin: 5px 0;
}

.setting-help a {
  color: #667eea;
  text-decoration: none;
}

.setting-help a:hover {
  text-decoration: underline;
}

.security-note {
  margin-top: 15px;
  color: #666;
  font-style: italic;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

/* 滚动条样式 */
.chat-messages::-webkit-scrollbar, .settings-panel::-webkit-scrollbar {
  width: 4px;
}

.chat-messages::-webkit-scrollbar-track, .settings-panel::-webkit-scrollbar-track {
  background: #f1f1f1;
}

.chat-messages::-webkit-scrollbar-thumb, .settings-panel::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 2px;
}

.chat-messages::-webkit-scrollbar-thumb:hover, .settings-panel::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
