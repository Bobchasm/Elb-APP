<template>
  <div class="wallet-container">
    <BackButton style="margin-top: 2vw;" />
    <div class="header">
      <h1>虚拟钱包</h1>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 钱包信息 -->
    <div v-else class="wallet-content">
      <!-- 余额卡片 -->
      <div class="balance-card">
        <div class="balance-label">账户余额</div>
        <div class="balance-amount">¥{{ walletInfo.balance?.toFixed(2) || '0.00' }}</div>
        <div class="balance-actions">
          <button class="action-btn recharge-btn" @click="showRechargeModal = true">
            <i class="fas fa-plus-circle"></i>
            充值
          </button>
          <button class="action-btn withdraw-btn" @click="showWithdrawModal = true">
            <i class="fas fa-minus-circle"></i>
            提现
          </button>
        </div>
      </div>

      <!-- VIP用户透支信息 -->
      <div v-if="walletInfo.isVip && walletInfo.overdraftLimit > 0" class="overdraft-card">
        <div class="overdraft-header">
          <span class="overdraft-label">可透支额度</span>
          <span class="vip-badge">VIP</span>
        </div>
        <div class="overdraft-info">
          <div class="overdraft-item">
            <span>剩余可透支：</span>
            <span class="overdraft-amount">¥{{ (walletInfo.overdraftLimit - walletInfo.usedOverdraft).toFixed(2) }}</span>
          </div>
          <div class="overdraft-item">
            <span>可透支总额：</span>
            <span class="overdraft-total">¥{{ walletInfo.overdraftLimit?.toFixed(2) || '0.00' }}</span>
          </div>
          <div v-if="walletInfo.usedOverdraft > 0" class="overdraft-item">
            <span>已使用透支：</span>
            <span class="overdraft-used">¥{{ walletInfo.usedOverdraft?.toFixed(2) || '0.00' }}</span>
          </div>
        </div>
        <button v-if="walletInfo.usedOverdraft > 0" class="repay-btn" @click="showRepayModal = true">
          <i class="fas fa-credit-card"></i>
          去还款
        </button>
      </div>

      <!-- 非VIP用户提示 -->
      <div v-if="!walletInfo.isVip" class="vip-promotion-card">
        <div class="vip-promotion-content">
          <i class="fas fa-crown vip-icon"></i>
          <div class="vip-text">
            <div class="vip-title">成为VIP享受透支额度</div>
            <div class="vip-desc">开通VIP即可获得透支额度，享受更多权益</div>
          </div>
        </div>
        <button class="vip-btn" @click="showVipModal = true">
          <i class="fas fa-star"></i>
          去成为VIP
        </button>
      </div>

      <!-- 功能入口 -->
      <div class="function-section">
        <div class="function-item" @click="navigateToTransactions">
          <div class="function-icon">
            <i class="fas fa-list-alt"></i>
          </div>
          <span class="function-text">交易明细</span>
          <i class="fas fa-chevron-right"></i>
        </div>
      </div>
    </div>

    <!-- 充值弹窗 -->
    <div v-if="showRechargeModal" class="modal-overlay" @click.self="showRechargeModal = false">
      <div class="modal-content recharge-modal">
        <div class="modal-header">
          <h3>充值</h3>
          <i class="fas fa-times close-btn" @click="showRechargeModal = false"></i>
        </div>
        <div class="modal-body">
          <div class="input-group">
            <label>充值金额</label>
            <input type="number" v-model.number="rechargeAmount" placeholder="请输入充值金额" min="0.01" step="0.01" />
          </div>
          <div class="reward-info" v-if="rechargeRules && rechargeAmount">
            <div class="info-section-title">充值信息</div>
            <div class="info-item">
              <span class="info-label">充值金额：</span>
              <span class="info-value">¥{{ (rechargeAmount || 0).toFixed(2) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">充值奖励率：</span>
              <span class="highlight">{{ (rechargeRules.rewardRate * 100).toFixed(2) }}%</span>
            </div>
            <div class="info-item">
              <span class="info-label">充值奖励：</span>
              <span class="highlight reward-amount">¥{{ ((rechargeAmount || 0) * rechargeRules.rewardRate).toFixed(2) }}</span>
            </div>
            <div class="info-item total-item">
              <span class="info-label">到账总额：</span>
              <span class="highlight total-amount">¥{{ ((rechargeAmount || 0) * (1 + rechargeRules.rewardRate)).toFixed(2) }}</span>
            </div>
          </div>
          <div class="rules-link">
            <a href="#" @click.prevent="showRechargeRules = true">
              <i class="fas fa-info-circle"></i>
              查看充值与奖励规则
            </a>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="showRechargeModal = false">取消</button>
          <button class="confirm-btn" @click="handleRecharge" :disabled="!rechargeAmount || rechargeAmount <= 0">
            确认充值
          </button>
        </div>
      </div>
    </div>

    <!-- 提现弹窗 -->
    <div v-if="showWithdrawModal" class="modal-overlay" @click.self="showWithdrawModal = false">
      <div class="modal-content withdraw-modal">
        <div class="modal-header">
          <h3>提现</h3>
          <i class="fas fa-times close-btn" @click="showWithdrawModal = false"></i>
        </div>
        <div class="modal-body">
          <div class="input-group">
            <label>提现金额</label>
            <input type="number" v-model.number="withdrawAmount" placeholder="请输入提现金额" min="0.01" step="0.01" />
          </div>
          <div class="fee-info" v-if="withdrawRules && withdrawAmount">
            <div class="info-section-title">提现信息</div>
            <div class="info-item">
              <span class="info-label">提现金额：</span>
              <span class="info-value">¥{{ (withdrawAmount || 0).toFixed(2) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">提现手续费率：</span>
              <span class="highlight">{{ (withdrawRules.feeRate * 100).toFixed(2) }}%</span>
            </div>
            <div class="info-item">
              <span class="info-label">手续费：</span>
              <span class="highlight fee-amount">¥{{ ((withdrawAmount || 0) * withdrawRules.feeRate).toFixed(2) }}</span>
            </div>
            <div class="info-item total-item">
              <span class="info-label">实际到账：</span>
              <span class="highlight total-amount">¥{{ ((withdrawAmount || 0) * (1 - withdrawRules.feeRate)).toFixed(2) }}</span>
            </div>
          </div>
          <div class="rules-link">
            <a href="#" @click.prevent="showWithdrawRules = true">
              <i class="fas fa-info-circle"></i>
              查看充值与奖励规则
            </a>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="showWithdrawModal = false">取消</button>
          <button class="confirm-btn" @click="handleWithdraw" :disabled="!withdrawAmount || withdrawAmount <= 0">
            确认提现
          </button>
        </div>
      </div>
    </div>

    <!-- 还款弹窗 -->
    <div v-if="showRepayModal" class="modal-overlay" @click.self="showRepayModal = false">
      <div class="modal-content repay-modal">
        <div class="modal-header">
          <h3>还款</h3>
          <i class="fas fa-times close-btn" @click="showRepayModal = false"></i>
        </div>
        <div class="modal-body">
          <div class="repay-info">
            <div class="info-item">
              <span>待还金额：</span>
              <span class="highlight">¥{{ walletInfo.usedOverdraft?.toFixed(2) || '0.00' }}</span>
            </div>
            <div class="info-item">
              <span>钱包余额：</span>
              <span>¥{{ walletInfo.balance?.toFixed(2) || '0.00' }}</span>
            </div>
          </div>
          <div class="repay-method">
            <label class="method-label">还款方式</label>
            <div class="method-options">
              <label class="method-option" :class="{ active: repayMethod === 'wallet' }">
                <input type="radio" v-model="repayMethod" value="wallet" />
                <div class="method-content">
                  <i class="fas fa-wallet"></i>
                  <span>钱包余额还款</span>
                </div>
              </label>
              <label class="method-option" :class="{ active: repayMethod === 'thirdparty' }">
                <input type="radio" v-model="repayMethod" value="thirdparty" />
                <div class="method-content">
                  <i class="fas fa-credit-card"></i>
                  <span>第三方支付</span>
                </div>
              </label>
            </div>
            <div class="method-tip" v-if="repayMethod === 'wallet' && walletInfo.balance < walletInfo.usedOverdraft">
              <i class="fas fa-exclamation-circle"></i>
              <span>钱包余额不足，请使用第三方支付</span>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="showRepayModal = false">取消</button>
          <button class="confirm-btn" @click="handleRepay" :disabled="repayMethod === 'wallet' && walletInfo.balance < walletInfo.usedOverdraft">
            确认还款
          </button>
        </div>
      </div>
    </div>

    <!-- 充值规则弹窗 -->
    <div v-if="showRechargeRules" class="modal-overlay" @click.self="closeRulesModal">
      <div class="modal-content rules-modal">
        <div class="modal-header">
          <h3>充值与奖励规则</h3>
          <i class="fas fa-times close-btn" @click="closeRulesModal"></i>
        </div>
        <div class="modal-body rules-content">
          <div class="rule-text" v-if="rulesText">
            <div v-html="rulesText"></div>
          </div>
          <div class="rule-text" v-else>
            <p>1. 充值金额需大于0.01元</p>
            <p>2. 充值奖励率：{{ rechargeRules ? (rechargeRules.rewardRate * 100).toFixed(2) + '%' : '待配置' }}</p>
            <p>3. 奖励金额将在充值成功后立即到账</p>
            <p>4. 提现时将扣除之前获得的充值奖励</p>
            <p>5. 提现手续费率：{{ withdrawRules ? (withdrawRules.feeRate * 100).toFixed(2) + '%' : '待配置' }}</p>
            <p>6. 具体规则以平台公告为准</p>
          </div>
        </div>
        <div class="modal-footer">
          <button class="confirm-btn" @click="closeRulesModal">返回</button>
        </div>
      </div>
    </div>

    <!-- 提现规则弹窗 -->
    <div v-if="showWithdrawRules" class="modal-overlay" @click.self="closeRulesModal">
      <div class="modal-content rules-modal">
        <div class="modal-header">
          <h3>充值与奖励规则</h3>
          <i class="fas fa-times close-btn" @click="closeRulesModal"></i>
        </div>
        <div class="modal-body rules-content">
          <div class="rule-text" v-if="rulesText">
            <div v-html="rulesText"></div>
          </div>
          <div class="rule-text" v-else>
            <p>1. 提现金额需大于0.01元</p>
            <p>2. 提现手续费率：{{ withdrawRules ? (withdrawRules.feeRate * 100).toFixed(2) + '%' : '待配置' }}</p>
            <p>3. 提现时将扣除之前获得的充值奖励</p>
            <p>4. 充值奖励率：{{ rechargeRules ? (rechargeRules.rewardRate * 100).toFixed(2) + '%' : '待配置' }}</p>
            <p>5. 具体规则以平台公告为准</p>
          </div>
        </div>
        <div class="modal-footer">
          <button class="confirm-btn" @click="closeRulesModal">返回</button>
        </div>
      </div>
    </div>

    <!-- VIP信息弹窗 -->
    <div v-if="showVipModal" class="modal-overlay" @click.self="showVipModal = false">
      <div class="modal-content vip-modal">
        <div class="modal-header">
          <h3>VIP会员信息</h3>
          <i class="fas fa-times close-btn" @click="showVipModal = false"></i>
        </div>
        <div class="modal-body vip-content">
          <div class="vip-levels">
            <div 
              v-for="level in vipLevels" 
              :key="level.id" 
              class="vip-level-item"
              :class="{ active: selectedVipLevel?.id === level.id }"
              @click="selectedVipLevel = level"
            >
              <div class="level-header">
                <i class="fas fa-crown" :style="{ color: level.color }"></i>
                <span class="level-name">{{ level.name }}</span>
              </div>
              <div class="level-benefits">
                <div class="benefit-item" v-for="benefit in level.benefits" :key="benefit">
                  <i class="fas fa-check-circle"></i>
                  <span>{{ benefit }}</span>
                </div>
              </div>
              <div class="level-price">¥{{ level.price }}/月</div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="cancel-btn" @click="showVipModal = false">取消</button>
          <button class="confirm-btn" @click="handleVipUpgrade" :disabled="!selectedVipLevel">
            选择此等级
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '../utils/request';
import { toast } from '../utils/toast';
import BackButton from '../components/BackButton.vue';

export default {
  name: 'Wallet',
  components: {
    BackButton
  },
  setup() {
    const router = useRouter();

    const getCurrentUser = () => {
      try {
        const localUser = localStorage.getItem('userInfo');
        if (localUser) return JSON.parse(localUser);
        const sessionUser = sessionStorage.getItem('userInfo');
        if (sessionUser) return JSON.parse(sessionUser);
      } catch (error) {
        console.error('解析用户信息失败:', error);
      }
      return null;
    };

    const getWalletInfoKey = () => {
      const user = getCurrentUser();
      return user ? `walletInfo_${user.id}` : 'walletInfo_guest';
    };

    const getWalletTransactionsKey = () => {
      const user = getCurrentUser();
      return user ? `walletTransactions_${user.id}` : 'walletTransactions_guest';
    };
    const loading = ref(true);
    const walletInfo = ref({
      balance: 0,
      isVip: false,
      overdraftLimit: 0,
      usedOverdraft: 0
    });
    const showRechargeModal = ref(false);
    const showWithdrawModal = ref(false);
    const showRepayModal = ref(false);
    const showRechargeRules = ref(false);
    const showWithdrawRules = ref(false);
    const showVipModal = ref(false);
    const rechargeAmount = ref(null);
    const withdrawAmount = ref(null);
    const repayMethod = ref('wallet');
    const rechargeRules = ref(null);
    const withdrawRules = ref(null);
    const selectedVipLevel = ref(null);
    const rulesText = ref(null); // 规则文本，可以从后端获取
    const vipLevels = ref([
      {
        id: 1,
        name: 'VIP1',
        price: 9.9,
        color: '#FFD700',
        benefits: ['透支额度：¥100', '充值奖励率提升5%', '专属客服']
      },
      {
        id: 2,
        name: 'VIP2',
        price: 19.9,
        color: '#FF6B6B',
        benefits: ['透支额度：¥500', '充值奖励率提升10%', '专属客服', '优先处理']
      },
      {
        id: 3,
        name: 'VIP3',
        price: 39.9,
        color: '#9B59B6',
        benefits: ['透支额度：¥2000', '充值奖励率提升15%', '专属客服', '优先处理', '免提现手续费']
      }
    ]);

    // 获取钱包信息
    const fetchWalletInfo = async () => {
      try {
        loading.value = true;
        
        // ========== 前端模拟模式（用于测试，不连接后端） ==========
        const savedWalletInfo = localStorage.getItem(getWalletInfoKey());
        if (savedWalletInfo) {
          walletInfo.value = JSON.parse(savedWalletInfo);
        } else {
          walletInfo.value = {
            balance: 0,
            isVip: false,
            overdraftLimit: 0,
            usedOverdraft: 0
          };
        }
        // ========== 前端模拟模式结束 ==========
        
        // ========== 后端调用逻辑（已注释，需要时取消注释） ==========
        // const response = await request.get('/api/wallet/info');
        // if (response.success) {
        //   walletInfo.value = response.data;
        // } else {
        //   // 如果钱包不存在，创建钱包
        //   if (response.message && response.message.includes('不存在')) {
        //     await createWallet();
        //   } else {
        //     toast.error('获取钱包信息失败');
        //   }
        // }
        // ========== 后端调用逻辑结束 ==========
      } catch (error) {
        console.error('获取钱包信息失败:', error);
        // ========== 前端模拟模式 ==========
        walletInfo.value = {
          balance: 0,
          isVip: false,
          overdraftLimit: 0,
          usedOverdraft: 0
        };
        // ========== 后端调用逻辑（已注释） ==========
        // // 如果是404，说明钱包不存在，创建钱包
        // if (error.response?.status === 404) {
        //   await createWallet();
        // } else {
        //   toast.error('获取钱包信息失败');
        // }
        // ========== 后端调用逻辑结束 ==========
      } finally {
        loading.value = false;
      }
    };

    // 创建钱包
    const createWallet = async () => {
      try {
        // ========== 前端模拟模式（用于测试，不连接后端） ==========
        const newWalletInfo = {
          balance: 0,
          isVip: false,
          overdraftLimit: 0,
          usedOverdraft: 0
        };
        walletInfo.value = newWalletInfo;
        localStorage.setItem(getWalletInfoKey(), JSON.stringify(newWalletInfo));

        addTransactionRecord({
          transactionType: 'create',
          amount: 0,
          reason: '钱包开通成功'
        });
        toast.success('钱包已激活');
        // ========== 前端模拟模式结束 ==========
        
        // ========== 后端调用逻辑（已注释，需要时取消注释） ==========
        // const response = await request.post('/api/wallet/create');
        // if (response.success) {
        //   walletInfo.value = response.data;
        //   toast.success('钱包已激活');
        // } else {
        //   toast.error('激活钱包失败');
        // }
        // ========== 后端调用逻辑结束 ==========
      } catch (error) {
        console.error('创建钱包失败:', error);
        toast.error('激活钱包失败');
      }
    };

    const addTransactionRecord = (record) => {
      try {
        const transactions = JSON.parse(localStorage.getItem(getWalletTransactionsKey()) || '[]');
        const transaction = {
          id: Date.now(),
          transactionTime: new Date().toISOString(),
          ...record
        };
        transactions.unshift(transaction);
        localStorage.setItem(getWalletTransactionsKey(), JSON.stringify(transactions));
      } catch (error) {
        console.error('保存交易记录失败:', error);
      }
    };

    // 获取充值/提现规则
    const fetchRules = async () => {
      // ========== 前端模拟模式（用于测试，不连接后端） ==========
      // 使用默认规则
      rechargeRules.value = { rewardRate: 0.1 }; // 默认10%
      withdrawRules.value = { feeRate: 0.1 }; // 默认10%
      rulesText.value = null; // 规则文本
      // ========== 前端模拟模式结束 ==========
      
      // ========== 后端调用逻辑（已注释，需要时取消注释） ==========
      // try {
      //   const response = await request.get('/api/wallet/rules');
      //   if (response.success) {
      //     rechargeRules.value = response.data.rechargeRules || { rewardRate: 0.1 }; // 默认10%
      //     withdrawRules.value = response.data.withdrawRules || { feeRate: 0.1 }; // 默认10%
      //     rulesText.value = response.data.rulesText || null; // 规则文本
      //   }
      // } catch (error) {
      //   console.error('获取规则失败:', error);
      //   // 使用默认规则
      //   rechargeRules.value = { rewardRate: 0.1 };
      //   withdrawRules.value = { feeRate: 0.1 };
      // }
      // ========== 后端调用逻辑结束 ==========
    };

    // 关闭规则弹窗
    const closeRulesModal = () => {
      showRechargeRules.value = false;
      showWithdrawRules.value = false;
    };

    // 处理充值
    const handleRecharge = async () => {
      if (!rechargeAmount.value || rechargeAmount.value <= 0) {
        toast.warning('请输入有效的充值金额');
        return;
      }
      
      // ========== 前端模拟模式（用于测试，不连接后端） ==========
      try {
        // 模拟延迟
        await new Promise(resolve => setTimeout(resolve, 500));
        
        // 计算充值奖励
        const reward = rechargeAmount.value * (rechargeRules.value?.rewardRate || 0.1);
        const totalAmount = rechargeAmount.value + reward;
        
        // 更新钱包余额
        const savedWalletInfo = localStorage.getItem(getWalletInfoKey());
        let walletData = savedWalletInfo ? JSON.parse(savedWalletInfo) : {
          balance: 0,
          isVip: false,
          overdraftLimit: 0,
          usedOverdraft: 0
        };
        
        walletData.balance = (walletData.balance || 0) + totalAmount;
        walletInfo.value = walletData;
        localStorage.setItem(getWalletInfoKey(), JSON.stringify(walletData));
        
        addTransactionRecord({
          transactionType: 'recharge',
          amount: totalAmount,
          rewardAmount: reward,
          reason: `充值 ¥${(rechargeAmount.value).toFixed(2)}，奖励 ¥${reward.toFixed(2)}`
        });

        toast.success('充值成功');
        showRechargeModal.value = false;
        rechargeAmount.value = null;
        // ========== 前端模拟模式结束 ==========
        return; // 直接返回，不执行后面的代码
        
        // ========== 后端调用逻辑（已注释，需要时取消注释） ==========
        // const response = await request.post('/api/wallet/recharge', {
        //   amount: rechargeAmount.value
        // });
        // if (response.success) {
        //   toast.success('充值成功');
        //   showRechargeModal.value = false;
        //   rechargeAmount.value = null;
        //   await fetchWalletInfo();
        // } else {
        //   toast.error('充值失败：' + (response.message || '未知错误'));
        // }
        // ========== 后端调用逻辑结束 ==========
      } catch (error) {
        console.error('充值失败:', error);
        toast.error('充值失败，请重试');
      }
    };

    // 处理提现
    const handleWithdraw = async () => {
      if (!withdrawAmount.value || withdrawAmount.value <= 0) {
        toast.warning('请输入有效的提现金额');
        return;
      }
      
      // 计算可用余额（包括透支额度）
      const availableBalance = walletInfo.value.balance + 
        (walletInfo.value.isVip ? (walletInfo.value.overdraftLimit - walletInfo.value.usedOverdraft) : 0);
      
      if (withdrawAmount.value > availableBalance) {
        toast.warning('余额不足');
        return;
      }
      
      // ========== 前端模拟模式（用于测试，不连接后端） ==========
      try {
        // 模拟延迟
        await new Promise(resolve => setTimeout(resolve, 500));
        
        // 计算手续费
        const fee = withdrawAmount.value * (withdrawRules.value?.feeRate || 0.1);
        const actualAmount = withdrawAmount.value - fee;
        
        // 更新钱包余额
        const savedWalletInfo = localStorage.getItem(getWalletInfoKey());
        let walletData = savedWalletInfo ? JSON.parse(savedWalletInfo) : {
          balance: 0,
          isVip: false,
          overdraftLimit: 0,
          usedOverdraft: 0
        };
        
        // 扣除余额，如果余额不足则使用透支额度
        const withdrawAmountValue = withdrawAmount.value;
        let usedOverdraftAmount = 0;
        if (walletData.balance >= withdrawAmountValue) {
          // 余额足够，直接扣除
          walletData.balance = (walletData.balance || 0) - withdrawAmountValue;
        } else {
          // 余额不足，使用透支额度
          const needOverdraft = withdrawAmountValue - (walletData.balance || 0);
          walletData.balance = 0;
          walletData.usedOverdraft = (walletData.usedOverdraft || 0) + needOverdraft;
          usedOverdraftAmount = needOverdraft;
        }
        
        walletInfo.value = walletData;
        localStorage.setItem(getWalletInfoKey(), JSON.stringify(walletData));
        
        addTransactionRecord({
          transactionType: 'withdraw',
          amount: -withdrawAmountValue,
          fee,
          reason: `提现申请 ¥${withdrawAmountValue.toFixed(2)}，手续费 ¥${fee.toFixed(2)}，实际到账 ¥${actualAmount.toFixed(2)}${usedOverdraftAmount > 0 ? `，使用透支 ¥${usedOverdraftAmount.toFixed(2)}` : ''}`
        });

        toast.success('提现成功');
        showWithdrawModal.value = false;
        withdrawAmount.value = null;
        // ========== 前端模拟模式结束 ==========
        return; // 直接返回，不执行后面的代码
        
        // ========== 后端调用逻辑（已注释，需要时取消注释） ==========
        // const response = await request.post('/api/wallet/withdraw', {
        //   amount: withdrawAmount.value
        // });
        // if (response.success) {
        //   toast.success('提现成功');
        //   showWithdrawModal.value = false;
        //   withdrawAmount.value = null;
        //   await fetchWalletInfo();
        // } else {
        //   toast.error('提现失败：' + (response.message || '未知错误'));
        // }
        // ========== 后端调用逻辑结束 ==========
      } catch (error) {
        console.error('提现失败:', error);
        toast.error('提现失败，请重试');
      }
    };

    // 处理还款
    const handleRepay = async () => {
      if (repayMethod.value === 'wallet') {
        if (walletInfo.value.balance < walletInfo.value.usedOverdraft) {
          toast.warning('钱包余额不足，请使用第三方支付');
          return;
        }
        
        // ========== 前端模拟模式（用于测试，不连接后端） ==========
        try {
          // 模拟延迟
          await new Promise(resolve => setTimeout(resolve, 500));
          
          // 更新钱包信息
          const savedWalletInfo = localStorage.getItem(getWalletInfoKey());
          let walletData = savedWalletInfo ? JSON.parse(savedWalletInfo) : {
            balance: 0,
            isVip: false,
            overdraftLimit: 0,
            usedOverdraft: 0
          };
          
          const repayAmount = walletData.usedOverdraft || 0;
          if (repayAmount <= 0) {
            toast.info('暂无透支需要还款');
            return;
          }
          walletData.balance = Math.max(0, (walletData.balance || 0) - repayAmount);
          walletData.usedOverdraft = 0;
          walletInfo.value = walletData;
          localStorage.setItem(getWalletInfoKey(), JSON.stringify(walletData));

          addTransactionRecord({
            transactionType: 'repay',
            amount: -repayAmount,
            reason: '钱包余额还款'
          });
          
          toast.success('还款成功');
          showRepayModal.value = false;
          // ========== 前端模拟模式结束 ==========
          return; // 直接返回，不执行后面的代码
          
          // ========== 后端调用逻辑（已注释，需要时取消注释） ==========
          // const response = await request.post('/api/wallet/repay', {
          //   method: 'wallet',
          //   amount: walletInfo.value.usedOverdraft
          // });
          // if (response.success) {
          //   toast.success('还款成功');
          //   showRepayModal.value = false;
          //   await fetchWalletInfo();
          // } else {
          //   toast.error('还款失败：' + (response.message || '未知错误'));
          // }
          // ========== 后端调用逻辑结束 ==========
        } catch (error) {
          console.error('还款失败:', error);
          toast.error('还款失败，请重试');
        }
      } else {
        // 第三方支付还款
        toast.info('第三方支付功能待开发');
        // 这里可以跳转到第三方支付页面
      }
    };

    // 跳转到交易明细
    const navigateToTransactions = () => {
      router.push('/wallet/transactions');
    };

    // 处理VIP升级
    const handleVipUpgrade = async () => {
      if (!selectedVipLevel.value) {
        toast.warning('请选择VIP等级');
        return;
      }
      
      // ========== 前端模拟模式（用于测试，不连接后端） ==========
      try {
        // 模拟延迟
        await new Promise(resolve => setTimeout(resolve, 500));
        
        // 更新钱包信息
        const savedWalletInfo = localStorage.getItem(getWalletInfoKey());
        let walletData = savedWalletInfo ? JSON.parse(savedWalletInfo) : {
          balance: 0,
          isVip: false,
          overdraftLimit: 0,
          usedOverdraft: 0
        };
        
        const vipPrice = selectedVipLevel.value.price;
        const availableBalance = walletData.balance + 
          (walletData.isVip ? (walletData.overdraftLimit - walletData.usedOverdraft) : 0);

        if (availableBalance < vipPrice) {
          toast.warning('余额不足');
          return;
        }

        if (walletData.balance >= vipPrice) {
          walletData.balance -= vipPrice;
        } else {
          const needOverdraft = vipPrice - (walletData.balance || 0);
          walletData.balance = 0;
          walletData.usedOverdraft = (walletData.usedOverdraft || 0) + needOverdraft;
        }

        // 根据VIP等级设置透支额度
        const overdraftLimits = { 1: 100, 2: 500, 3: 2000 };
        walletData.isVip = true;
        walletData.overdraftLimit = overdraftLimits[selectedVipLevel.value.id] || 0;
        walletInfo.value = walletData;
        localStorage.setItem(getWalletInfoKey(), JSON.stringify(walletData));

        addTransactionRecord({
          transactionType: 'upgrade',
          amount: -vipPrice,
          reason: `升级至 ${selectedVipLevel.value.name}，透支额度提升至 ¥${walletData.overdraftLimit}`
        });
        
        toast.success('VIP升级成功');
        showVipModal.value = false;
        selectedVipLevel.value = null;
        // ========== 前端模拟模式结束 ==========
        return; // 直接返回，不执行后面的代码
        
        // ========== 后端调用逻辑（已注释，需要时取消注释） ==========
        // const response = await request.post('/api/wallet/upgrade-vip', {
        //   vipLevelId: selectedVipLevel.value.id
        // });
        // if (response.success) {
        //   toast.success('VIP升级成功');
        //   showVipModal.value = false;
        //   selectedVipLevel.value = null;
        //   await fetchWalletInfo();
        // } else {
        //   toast.error('VIP升级失败：' + (response.message || '未知错误'));
        // }
        // ========== 后端调用逻辑结束 ==========
      } catch (error) {
        console.error('VIP升级失败:', error);
        toast.error('VIP升级失败，请重试');
      }
    };

    onMounted(() => {
      fetchWalletInfo();
      fetchRules();
    });

    return {
      loading,
      walletInfo,
      showRechargeModal,
      showWithdrawModal,
      showRepayModal,
      showRechargeRules,
      showWithdrawRules,
      rechargeAmount,
      withdrawAmount,
      repayMethod,
      rechargeRules,
      withdrawRules,
      handleRecharge,
      handleWithdraw,
      handleRepay,
      navigateToTransactions,
      showVipModal,
      selectedVipLevel,
      vipLevels,
      handleVipUpgrade,
      rulesText,
      closeRulesModal
    };
  }
};
</script>

<style scoped>
.wallet-container {
  max-width: 600px;
  margin: 0 auto;
  min-height: 100vh;
  background-color: #f5f7fa;
  padding-bottom: 20px;
}

.header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  max-width: 600px;
  margin: 0 auto;
  height: 12vw;
  background-color: #0097FF;
  color: #fff;
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.header h1 {
  font-size: 4.8vw;
  margin: 0;
  font-weight: 500;
}

.wallet-content {
  padding-top: 14vw;
  padding: 14vw 4vw 4vw;
}

.balance-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4vw;
  padding: 6vw;
  margin-bottom: 4vw;
  color: white;
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.3);
}

.balance-label {
  font-size: 3.6vw;
  opacity: 0.9;
  margin-bottom: 2vw;
}

.balance-amount {
  font-size: 10vw;
  font-weight: bold;
  margin-bottom: 4vw;
}

.balance-actions {
  display: flex;
  gap: 3vw;
}

.action-btn {
  flex: 1;
  padding: 3vw;
  border: none;
  border-radius: 2vw;
  font-size: 3.6vw;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1.5vw;
  transition: all 0.3s;
}

.recharge-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  backdrop-filter: blur(10px);
}

.recharge-btn:active {
  background: rgba(255, 255, 255, 0.3);
}

.withdraw-btn {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  backdrop-filter: blur(10px);
}

.withdraw-btn:active {
  background: rgba(255, 255, 255, 0.3);
}

.overdraft-card {
  background: white;
  border-radius: 4vw;
  padding: 4vw;
  margin-bottom: 4vw;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.overdraft-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 3vw;
}

.overdraft-label {
  font-size: 4vw;
  font-weight: 500;
  color: #333;
}

.vip-badge {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: white;
  padding: 1vw 2vw;
  border-radius: 1vw;
  font-size: 2.8vw;
  font-weight: bold;
}

.overdraft-info {
  margin-bottom: 3vw;
}

.overdraft-item {
  display: flex;
  justify-content: space-between;
  padding: 2vw 0;
  font-size: 3.6vw;
  color: #666;
  border-bottom: 1px solid #f0f0f0;
}

.overdraft-item:last-child {
  border-bottom: none;
}

.overdraft-amount {
  color: #0097FF;
  font-weight: 500;
}

.overdraft-total {
  color: #666;
}

.overdraft-used {
  color: #ff6b6b;
  font-weight: 500;
}

.repay-btn {
  width: 100%;
  padding: 3vw;
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a6f 100%);
  color: white;
  border: none;
  border-radius: 2vw;
  font-size: 3.6vw;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 1.5vw;
  transition: all 0.3s;
}

.repay-btn:active {
  transform: scale(0.98);
}

.function-section {
  background: white;
  border-radius: 4vw;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.function-item {
  display: flex;
  align-items: center;
  padding: 4vw;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.3s;
}

.function-item:last-child {
  border-bottom: none;
}

.function-item:active {
  background: #f5f7fa;
}

.function-icon {
  width: 8vw;
  height: 8vw;
  background: #e6f0ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 3vw;
}

.function-icon i {
  font-size: 4vw;
  color: #0097FF;
}

.function-text {
  flex: 1;
  font-size: 4vw;
  color: #333;
}

.function-item i.fa-chevron-right {
  color: #ccc;
  font-size: 3.6vw;
}

.loading-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20vw;
  color: #666;
}

.spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #0097FF;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 弹窗样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 2000;
}

/* 规则弹窗应该在充值/提现弹窗之上 */
.rules-modal {
  z-index: 2001;
}

.modal-content {
  background: white;
  border-radius: 4vw;
  width: 85%;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 4vw;
  border-bottom: 1px solid #f0f0f0;
}

.modal-header h3 {
  font-size: 4.5vw;
  margin: 0;
  color: #333;
}

.close-btn {
  font-size: 5vw;
  color: #999;
  cursor: pointer;
}

.modal-body {
  padding: 4vw;
}

.input-group {
  margin-bottom: 4vw;
}

.input-group label {
  display: block;
  font-size: 3.6vw;
  color: #666;
  margin-bottom: 2vw;
}

.input-group input {
  width: 100%;
  padding: 3vw;
  border: 1px solid #ddd;
  border-radius: 2vw;
  font-size: 4vw;
  box-sizing: border-box;
}

.reward-info,
.fee-info,
.repay-info {
  background: #f5f7fa;
  padding: 3vw;
  border-radius: 2vw;
  margin-bottom: 3vw;
}

.info-section-title {
  font-size: 4vw;
  font-weight: 600;
  color: #333;
  margin-bottom: 3vw;
  padding-bottom: 2vw;
  border-bottom: 1px solid #e0e0e0;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 2.5vw 0;
  font-size: 3.6vw;
  color: #666;
  border-bottom: 1px solid #f0f0f0;
}

.info-item:last-child {
  border-bottom: none;
}

.info-item.total-item {
  margin-top: 2vw;
  padding-top: 3vw;
  border-top: 2px solid #e0e0e0;
  border-bottom: none;
  font-weight: 600;
}

.info-label {
  color: #666;
  flex: 1;
}

.info-value {
  color: #333;
  font-weight: 500;
}

.highlight {
  color: #0097FF;
  font-weight: 500;
}

.reward-amount {
  color: #52c41a;
  font-weight: 600;
}

.fee-amount {
  color: #ff4d4f;
  font-weight: 600;
}

.total-amount {
  color: #0097FF;
  font-size: 4.2vw;
  font-weight: 700;
}

.rules-link {
  text-align: center;
  margin-top: 3vw;
  padding-top: 3vw;
  border-top: 1px dashed #e0e0e0;
}

.rules-link a {
  font-size: 3.2vw;
  color: #0097FF;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  gap: 1vw;
  transition: color 0.3s;
}

.rules-link a:hover {
  color: #0080e6;
}

.rules-link a i {
  font-size: 3.6vw;
}

.repay-method {
  margin-top: 4vw;
}

.method-label {
  display: block;
  font-size: 3.6vw;
  color: #666;
  margin-bottom: 2vw;
  font-weight: 500;
}

.method-options {
  display: flex;
  flex-direction: column;
  gap: 2vw;
}

.method-option {
  display: flex;
  align-items: center;
  padding: 3vw;
  border: 2px solid #ddd;
  border-radius: 2vw;
  cursor: pointer;
  transition: all 0.3s;
  background: #fff;
}

.method-option:hover {
  border-color: #0097FF;
  background: #f0f8ff;
}

.method-option.active {
  border-color: #0097FF;
  background: #e6f4ff;
}

.method-option input {
  margin-right: 2vw;
  width: 4vw;
  height: 4vw;
  cursor: pointer;
}

.method-content {
  display: flex;
  align-items: center;
  gap: 2vw;
  flex: 1;
}

.method-content i {
  font-size: 4.5vw;
  color: #0097FF;
}

.method-content span {
  font-size: 3.6vw;
  color: #333;
}

.method-tip {
  margin-top: 2vw;
  padding: 2vw;
  background: #fff7e6;
  border: 1px solid #ffd591;
  border-radius: 2vw;
  display: flex;
  align-items: center;
  gap: 1.5vw;
  font-size: 3.2vw;
  color: #d46b08;
}

.method-tip i {
  font-size: 3.6vw;
  color: #fa8c16;
}

.modal-footer {
  display: flex;
  gap: 3vw;
  padding: 4vw;
  border-top: 1px solid #f0f0f0;
}

.modal-footer button {
  flex: 1;
  padding: 3.5vw;
  border: none;
  border-radius: 2vw;
  font-size: 4vw;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.cancel-btn {
  background: #f5f5f5;
  color: #666;
}

.cancel-btn:active {
  background: #e0e0e0;
}

.confirm-btn {
  background: #0097FF;
  color: white;
}

.confirm-btn:active {
  background: #0080e6;
}

.confirm-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}

.rules-content {
  max-height: 50vh;
  overflow-y: auto;
}

.rule-text {
  font-size: 3.6vw;
  line-height: 1.8;
  color: #666;
}

.rule-text p {
  margin: 2vw 0;
}

/* VIP相关样式 */
.vip-promotion-card {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border-radius: 4vw;
  padding: 4vw;
  margin-bottom: 4vw;
  box-shadow: 0 4px 12px rgba(245, 87, 108, 0.3);
}

.vip-promotion-content {
  display: flex;
  align-items: center;
  margin-bottom: 3vw;
}

.vip-icon {
  font-size: 8vw;
  color: #FFD700;
  margin-right: 3vw;
}

.vip-text {
  flex: 1;
  color: white;
}

.vip-title {
  font-size: 4.5vw;
  font-weight: bold;
  margin-bottom: 1vw;
}

.vip-desc {
  font-size: 3.2vw;
  opacity: 0.9;
}

.vip-btn {
  width: 100%;
  padding: 3vw;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 2vw;
  font-size: 3.6vw;
  font-weight: 500;
  cursor: pointer;
  backdrop-filter: blur(10px);
  transition: all 0.3s;
}

.vip-btn:active {
  background: rgba(255, 255, 255, 0.3);
}

.vip-modal {
  max-height: 85vh;
}

.vip-content {
  max-height: 60vh;
  overflow-y: auto;
}

.vip-levels {
  display: flex;
  flex-direction: column;
  gap: 3vw;
}

.vip-level-item {
  border: 2px solid #e0e0e0;
  border-radius: 3vw;
  padding: 4vw;
  cursor: pointer;
  transition: all 0.3s;
}

.vip-level-item.active {
  border-color: #0097FF;
  background: #f0f8ff;
}

.level-header {
  display: flex;
  align-items: center;
  margin-bottom: 3vw;
}

.level-header i {
  font-size: 5vw;
  margin-right: 2vw;
}

.level-name {
  font-size: 4.5vw;
  font-weight: bold;
  color: #333;
}

.level-benefits {
  margin-bottom: 2vw;
}

.benefit-item {
  display: flex;
  align-items: center;
  margin-bottom: 1.5vw;
  font-size: 3.6vw;
  color: #666;
}

.benefit-item i {
  color: #52c41a;
  margin-right: 2vw;
  font-size: 3.2vw;
}

.level-price {
  text-align: right;
  font-size: 4.5vw;
  font-weight: bold;
  color: #0097FF;
}
</style>

