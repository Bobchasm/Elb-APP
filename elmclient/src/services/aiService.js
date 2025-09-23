/**
 * AI助手服务
 * 负责处理与AI相关的API调用
 */

import request from '../utils/request'

class AIService {
  /**
   * 发送消息给AI助手
   * @param {string} message - 用户消息
   * @param {Array} history - 聊天历史（可选）
   * @returns {Promise<string>} AI回复
   */
  async sendMessage(message, history = []) {
    try {
      // 优先尝试阿里云API
      if (this.isAliyunAPIAvailable()) {
        return await this.callAliyunAPI(message, history)
      }
      
      // 备用：后端代理
      const response = await request.post('/api/ai/chat', {
        message,
        history
      })

      if (response.success) {
        return response.data.reply
      } else {
        throw new Error(response.message || 'AI服务响应异常')
      }
    } catch (error) {
      console.error('AI服务调用失败:', error)
      
      // 最终备用：本地智能回复
      return this.getLocalReply(message)
    }
  }

  /**
   * 检查阿里云API是否可用
   * @returns {boolean}
   */
  isAliyunAPIAvailable() {
    // 检查是否设置了API密钥
    return !!(process.env.VUE_APP_ALIYUN_API_KEY || window.ALIYUN_API_KEY)
  }

  /**
   * 直接调用阿里云通义千问API
   * @param {string} message - 用户消息
   * @param {Array} history - 聊天历史
   * @returns {Promise<string>} AI回复
   */
  async callAliyunAPI(message, history = []) {
    const API_KEY = process.env.VUE_APP_ALIYUN_API_KEY || window.ALIYUN_API_KEY
    
    if (!API_KEY) {
      throw new Error('未配置阿里云API密钥')
    }

    // 构建消息历史
    const messages = [
      {
        role: 'system',
        content: `你是饿了么平台的AI助手，专门帮助用户解决外卖订餐相关的问题。你应该：
1. 友好、专业地回答用户问题
2. 重点关注美食推荐、订单查询、优惠活动、配送服务等话题
3. 如果用户询问与外卖无关的内容，礼貌地引导回到平台相关话题
4. 回答要简洁明了，适合移动端显示，使用emoji让回答更生动
5. 当用户询问具体商家信息时，可以推荐虾滑火锅、螺狮粉、黄焖鸡米饭等热门商家`
      }
    ]

    // 添加历史对话（最近10条）
    if (history.length > 0) {
      const recentHistory = history.slice(-10)
      recentHistory.forEach(msg => {
        if (msg.type === 'user') {
          messages.push({ role: 'user', content: msg.content })
        } else if (msg.type === 'ai') {
          messages.push({ role: 'assistant', content: msg.content })
        }
      })
    }

    // 添加当前消息
    messages.push({ role: 'user', content: message })

    const requestBody = {
      model: 'qwen-turbo',
      input: { messages },
      parameters: {
        max_tokens: 800,
        temperature: 0.8,
        top_p: 0.9
      }
    }

    console.log('🤖 调用阿里云通义千问API...')

    const response = await fetch('https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${API_KEY}`,
        'X-DashScope-SSE': 'disable'
      },
      body: JSON.stringify(requestBody)
    })

    if (!response.ok) {
      throw new Error(`阿里云API请求失败: ${response.status} ${response.statusText}`)
    }

    const data = await response.json()

    if (data.output && data.output.choices && data.output.choices[0]) {
      const aiReply = data.output.choices[0].message.content
      console.log('✅ 阿里云API回复成功')
      return aiReply
    } else {
      throw new Error('阿里云API响应格式不正确')
    }
  }

  /**
   * 本地智能回复（备用方案）
   * @param {string} message - 用户消息
   * @returns {string} 回复内容
   */
  getLocalReply(message) {
    const lowerMessage = message.toLowerCase()

    // 美食推荐
    if (this.containsKeywords(lowerMessage, ['推荐', '美食', '商家', '好吃'])) {
      return `🍔 为您推荐附近热门美食：

**今日人气商家**
• 虾滑火锅 - 🔥 销量冠军，麻辣鲜香
• 螺狮粉 - 🍜 正宗柳州味，酸辣开胃  
• 黄焖鸡米饭 - 🍖 经济实惠，营养丰富

**特色小食**
• 包子粥铺 - 早餐首选
• 甜品饮品 - 下午茶时光
• 地方小吃 - 家乡的味道

点击商家即可查看详情并下单哦！`
    }

    // 优惠活动
    if (this.containsKeywords(lowerMessage, ['优惠', '活动', '折扣', '券'])) {
      return `💰 今日优惠大放送：

**新用户福利**
🎁 首单立减20元
🚚 免配送费券3张
💝 新人专享券包

**全场活动**  
💵 满50减10元
💵 满100减25元
🏪 指定商家满减更多

**会员特权**
👑 超级会员享9折
📦 每月专属券包
🚀 免费配送权益

立即下单享受优惠吧！`
    }

    // 订单相关
    if (this.containsKeywords(lowerMessage, ['订单', '查看', '状态', '物流'])) {
      return `📋 订单查询帮助：

**查看方式**
📱 底部菜单 → "订单"
👤 个人中心 → "我的订单"

**订单状态**
🟡 待确认 - 商家确认中
🔵 制作中 - 美食制作中
🚚 配送中 - 骑手配送中
✅ 已完成 - 请确认收货

**遇到问题？**
📞 联系商家或客服
🔄 申请退款（符合条件）

有其他问题可以继续问我哦！`
    }

    // 配送相关
    if (this.containsKeywords(lowerMessage, ['配送', '送达', '时间', '费用'])) {
      return `🚚 配送服务说明：

**配送费用**
📍 3公里内：5-8元起
📏 超出距离：每公里+1元
🌧️ 恶劣天气：可能加收服务费

**配送时间**
⚡ 平均30-45分钟
🍽️ 用餐高峰可能延长
⏰ 具体时间见订单详情

**免配送费**
👑 超级会员免费
🎫 使用免配送券
🏪 部分商家满额免费
🚶 选择"自取"

需要帮助请联系客服！`
    }

    // 支付相关
    if (this.containsKeywords(lowerMessage, ['支付', '付款', '微信', '支付宝'])) {
      return `💳 支付方式：

**支持方式**
💚 微信支付
💙 支付宝
🏦 银行卡支付
💰 账户余额

**支付安全**
🔒 全程加密保护
🛡️ 资金安全保障
📱 指纹/面容支付

**支付问题**
❌ 支付失败：检查网络和余额
⏳ 支付中：请耐心等待
🔄 重新支付：联系客服

安全便捷，放心使用！`
    }

    // 默认回复
    return `😊 感谢您的咨询！

我是饿了么AI助手，可以帮您：
🔍 推荐美食和商家
📋 查询订单状态  
💰 了解优惠活动
🚚 配送服务问题
💳 支付相关帮助

请告诉我您需要什么帮助，我会尽力为您解答！

如需人工客服，请联系在线客服。`
  }

  /**
   * 检查消息是否包含关键词
   * @param {string} message - 消息内容
   * @param {Array} keywords - 关键词数组
   * @returns {boolean} 是否包含
   */
  containsKeywords(message, keywords) {
    return keywords.some(keyword => message.includes(keyword))
  }

  /**
   * 获取智能推荐（基于用户行为）
   * @param {Object} userContext - 用户上下文
   * @returns {string} 推荐内容
   */
  getSmartRecommendation(userContext = {}) {
    const timeOfDay = new Date().getHours()
    
    // 根据时间推荐
    if (timeOfDay >= 6 && timeOfDay < 10) {
      return `🌅 早安！为您推荐营养早餐：

• 包子粥铺 - 热腾腾的包子配小粥
• 豆浆油条 - 经典中式早餐
• 三明治咖啡 - 西式轻食选择`
    } else if (timeOfDay >= 11 && timeOfDay < 14) {
      return `🍽️ 午餐时间到！热门推荐：

• 黄焖鸡米饭 - 营养丰富，分量足
• 麻辣香锅 - 口味丰富，下饭神器  
• 日式料理 - 清淡健康，精致美味`
    } else if (timeOfDay >= 17 && timeOfDay < 20) {
      return `🍜 晚餐推荐：

• 火锅烧烤 - 和朋友一起分享
• 家常菜 - 温馨的家庭味道
• 汤面类 - 暖胃又暖心`
    } else {
      return `🌙 夜宵时光：

• 烧烤串串 - 深夜美食首选
• 粥类轻食 - 养胃又健康
• 甜品饮品 - 治愈系选择`
    }
  }
}

// 导出单例
export default new AIService()

// 使用示例：
/*
import aiService from '@/services/aiService'

// 发送消息
const reply = await aiService.sendMessage('推荐一些好吃的')

// 获取智能推荐
const recommendation = aiService.getSmartRecommendation()
*/
