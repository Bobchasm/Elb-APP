<template>
  <div class="transactions-container">
    <BackButton style="margin-top: 2vw;" />
    <div class="header">
      <h1>交易明细</h1>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-state">
      <div class="spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 交易列表 -->
    <div v-else class="transactions-content">
      <!-- 筛选器 -->
      <div class="filter-section">
        <div class="refresh-section">
          <button class="refresh-btn" @click="fetchTransactions">
            <i class="fas fa-sync-alt"></i>
            刷新交易记录
          </button>
        </div>
        <div class="filter-tabs">
          <div 
            class="filter-tab" 
            :class="{ active: currentFilter === 'all' }"
            @click="currentFilter = 'all'"
          >
            全部
          </div>
          <div 
            class="filter-tab" 
            :class="{ active: currentFilter === 'recharge' }"
            @click="currentFilter = 'recharge'"
          >
            充值
          </div>
          <div 
            class="filter-tab" 
            :class="{ active: currentFilter === 'withdraw' }"
            @click="currentFilter = 'withdraw'"
          >
            提现
          </div>
          <div 
            class="filter-tab" 
            :class="{ active: currentFilter === 'payment' }"
            @click="currentFilter = 'payment'"
          >
            支付
          </div>
          <div 
            class="filter-tab" 
            :class="{ active: currentFilter === 'received' }"
            @click="currentFilter = 'received'"
          >
            收款
          </div>
        </div>
      </div>

      <!-- 交易列表 -->
      <div class="transactions-list">
        <div 
          v-for="transaction in filteredTransactions" 
          :key="transaction.id" 
          class="transaction-item"
          @click="viewDetail(transaction.id)"
        >
          <div class="transaction-icon" :class="getTransactionTypeClass(transaction.transactionType)">
            <i :class="getTransactionIcon(transaction.transactionType)"></i>
          </div>
          <div class="transaction-info">
            <div class="transaction-header">
              <div class="transaction-title">
                <span class="transaction-type">{{ getTransactionTypeName(transaction.transactionType) }}</span>
                <span class="transaction-type-badge" v-if="transaction.transactionType">
                  {{ transaction.transactionType }}
                </span>
              </div>
              <span class="transaction-amount" :class="getAmountClass(transaction.transactionType)">
                {{ getAmountPrefix(transaction.transactionType) }}¥{{ Math.abs(transaction.amount).toFixed(2) }}
              </span>
            </div>
            <div class="transaction-details">
              <div class="transaction-reason" v-if="transaction.reason">
                <span class="label">原因：</span>
                <span class="reason-text">{{ transaction.reason }}</span>
              </div>
              <div class="transaction-time">
                <i class="fas fa-clock"></i>
                {{ formatTime(transaction.transactionTime || transaction.createTime) }}
              </div>
              <div class="transaction-counterparty" v-if="transaction.counterpartyAccount">
                <span class="label">{{ transaction.transactionType === 'payment' ? '收款方：' : '付款方：' }}</span>
                <span>{{ transaction.counterpartyAccount }}</span>
              </div>
              <div class="transaction-order" v-if="transaction.orderId">
                <span class="label">订单号：</span>
                <span>{{ transaction.orderId }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div v-if="filteredTransactions.length === 0" class="empty-state">
          <i class="fas fa-inbox"></i>
          <p>暂无交易记录</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '../utils/request';
import { toast } from '../utils/toast';
import BackButton from '../components/BackButton.vue';
import eventBus from '../utils/eventBus';

export default {
  name: 'WalletTransactions',
  components: {
    BackButton
  },
  setup() {
    const router = useRouter();
    const loading = ref(true);
    const transactions = ref([]);
    const currentFilter = ref('all');

    // 获取交易明细
    const fetchTransactions = async () => {
      try {
        loading.value = true;
        // 直接调用交易明细接口，获取真实数据
        const params = {
          type: -1, // 全部类型
          status: -1, // 全部状态
          startDate: '1970-01-01',
          endDate: new Date().toISOString().split('T')[0]
        };
        
        console.log('调用交易明细接口，参数:', params);
        const response = await request.get('/api/wallet/transaction/list', { params });
        console.log('交易明细接口响应:', response);
        
        if (response && response.success) {
          const list = Array.isArray(response.data) ? response.data : [];
          console.log('交易明细原始数据:', list);
          
          // 直接使用后端返回的真实数据，不添加任何假数据
          transactions.value = list.map(it => ({
            id: it.id,
            transactionType: getTypeFromNumber(it.type),
            amount: it.amount ?? 0,
            fee: it.fee ?? 0,
            transactionTime: it.createTime,
            inOrOut: it.inOrOut, // 0-支出 1-收入
            reason: it.reason ?? ''
          }));
          
          // 不显示任何提示，让用户看到真实的数据状态
        } else {
          toast.error('获取交易明细失败: ' + (response?.message || '接口调用失败'));
        }
      } catch (error) {
        console.error('获取交易明细失败:', error);
        toast.error('获取交易明细失败');
      } finally {
        loading.value = false;
      }
    };

    // 根据数字类型转换为字符串类型
    const getTypeFromNumber = (typeNum) => {
      const typeMap = {
        0: 'payment',   // 支付
        1: 'received',  // 收款
        2: 'withdraw',  // 提现
        3: 'recharge'   // 充值
      };
      return typeMap[typeNum] || 'unknown';
    };

    const viewDetailByOrder = async (orderId) => {
      try {
        const response = await request.get('/api/wallet/transaction/detail/order', {
          params: { orderId }
        });
        if (response && response.success && response.data) {
          const d = response.data;
          const detail = {
            id: d.id,
            type: d.type, // 交易类型 0-支付 1-收款 2-提现 3-充值
            amount: d.amount, // 操作金额
            fee: d.fee, // 手续费或奖励
            createTime: d.createTime, // 交易时间
            inOrOut: d.inOrOut, // 支出还是收入 0-支出 1-收入
            status: d.status, // 操作金额是否为冻结 0-否 1-是
            fromAccount: d.from_account, // 转出钱包 交易类型为充值时值为0
            toAccount: d.to_account, // 转入钱包 交易类型为提现时值为0
            fromAccountName: d.from_account_name, // 转出钱包用户姓名 交易类型为充值时值为null
            toAccountName: d.to_account_name, // 转入钱包用户姓名 交易类型为提现时值为null
            feeRate: d.fee_rate // 手续费率或奖励率
          };
          
          // 格式化显示订单详情
          const typeNames = { 0: '支付', 1: '收款', 2: '提现', 3: '充值' };
          const inOutNames = { 0: '支出', 1: '收入' };
          const statusNames = { 0: '正常', 1: '冻结' };
          
          let detailInfo = `订单交易详情#${detail.id}\n`;
          detailInfo += `订单ID: ${orderId}\n`;
          detailInfo += `类型: ${typeNames[detail.type] || '未知'}\n`;
          detailInfo += `金额: ¥${detail.amount}\n`;
          detailInfo += `手续费: ¥${detail.fee}\n`;
          detailInfo += `收支: ${inOutNames[detail.inOrOut] || '未知'}\n`;
          detailInfo += `状态: ${statusNames[detail.status] || '未知'}\n`;
          detailInfo += `时间: ${detail.createTime}\n`;
          
          if (detail.fromAccountName) {
            detailInfo += `转出方: ${detail.fromAccountName}\n`;
          }
          if (detail.toAccountName) {
            detailInfo += `转入方: ${detail.toAccountName}\n`;
          }
          if (detail.feeRate) {
            detailInfo += `费率: ${(detail.feeRate * 100).toFixed(2)}%\n`;
          }
          
          alert(detailInfo);
        } else {
          toast.error('获取订单明细失败');
        }
      } catch (e) {
        console.error('获取订单明细失败:', e);
        toast.error('获取订单明细失败');
      }
    };

    // 查看单条明细详情
    const viewDetail = async (id) => {
      try {
        const response = await request.get('/api/wallet/transaction/detail', {
          params: { transactionId: id }
        });
        if (response && response.success && response.data) {
          const d = response.data;
          const detail = {
            id: d.id,
            type: d.type, // 交易类型 0-支付 1-收款 2-提现 3-充值
            amount: d.amount, // 操作金额
            fee: d.fee, // 手续费或奖励
            createTime: d.createTime, // 交易时间
            inOrOut: d.inOrOut, // 支出还是收入 0-支出 1-收入
            status: d.status, // 操作金额是否为冻结 0-否 1-是
            fromAccount: d.from_account, // 转出钱包 交易类型为充值时值为0
            toAccount: d.to_account, // 转入钱包 交易类型为提现时值为0
            fromAccountName: d.from_account_name, // 转出钱包用户姓名 交易类型为充值时值为null
            toAccountName: d.to_account_name, // 转入钱包用户姓名 交易类型为提现时值为null
            feeRate: d.fee_rate // 手续费率或奖励率
          };
          
          // 格式化显示详情
          const typeNames = { 0: '支付', 1: '收款', 2: '提现', 3: '充值' };
          const inOutNames = { 0: '支出', 1: '收入' };
          const statusNames = { 0: '正常', 1: '冻结' };
          
          let detailInfo = `交易详情#${detail.id}\n`;
          detailInfo += `类型: ${typeNames[detail.type] || '未知'}\n`;
          detailInfo += `金额: ¥${detail.amount}\n`;
          detailInfo += `手续费: ¥${detail.fee}\n`;
          detailInfo += `收支: ${inOutNames[detail.inOrOut] || '未知'}\n`;
          detailInfo += `状态: ${statusNames[detail.status] || '未知'}\n`;
          detailInfo += `时间: ${detail.createTime}\n`;
          
          if (detail.fromAccountName) {
            detailInfo += `转出方: ${detail.fromAccountName}\n`;
          }
          if (detail.toAccountName) {
            detailInfo += `转入方: ${detail.toAccountName}\n`;
          }
          if (detail.feeRate) {
            detailInfo += `费率: ${(detail.feeRate * 100).toFixed(2)}%\n`;
          }
          
          alert(detailInfo);
        } else {
          toast.error('获取明细详情失败');
        }
      } catch (e) {
        console.error('获取明细详情失败:', e);
        toast.error('获取明细详情失败');
      }
    };

    // 筛选后的交易列表
    const filteredTransactions = computed(() => {
      if (currentFilter.value === 'all') {
        return transactions.value;
      }
      return transactions.value.filter(t => {
        const type = t.transactionType?.toLowerCase();
        if (currentFilter.value === 'recharge') return type === 'recharge';
        if (currentFilter.value === 'withdraw') return type === 'withdraw';
        if (currentFilter.value === 'payment') return type === 'payment';
        if (currentFilter.value === 'received') return type === 'received' || type === 'received_payment';
        return true;
      });
    });

    // 获取交易类型名称
    const getTransactionTypeName = (type) => {
      const typeMap = {
        'recharge': '充值',
        'withdraw': '提现',
        'payment': '支付',
        'received': '收款',
        'received_payment': '收款',
        'overdraft': '透支',
        'repay': '还款',
        'reward': '奖励',
        'fee': '手续费'
      };
      return typeMap[type?.toLowerCase()] || type || '未知';
    };

    // 获取交易类型图标
    const getTransactionIcon = (type) => {
      const iconMap = {
        'recharge': 'fas fa-plus-circle',
        'withdraw': 'fas fa-minus-circle',
        'payment': 'fas fa-arrow-right',
        'received': 'fas fa-arrow-left',
        'received_payment': 'fas fa-arrow-left',
        'overdraft': 'fas fa-credit-card',
        'repay': 'fas fa-undo',
        'reward': 'fas fa-gift',
        'fee': 'fas fa-coins'
      };
      return iconMap[type?.toLowerCase()] || 'fas fa-exchange-alt';
    };

    // 获取交易类型样式类
    const getTransactionTypeClass = (type) => {
      const classMap = {
        'recharge': 'type-recharge',
        'withdraw': 'type-withdraw',
        'payment': 'type-payment',
        'received': 'type-received',
        'received_payment': 'type-received',
        'overdraft': 'type-overdraft',
        'repay': 'type-repay',
        'reward': 'type-reward',
        'fee': 'type-fee'
      };
      return classMap[type?.toLowerCase()] || 'type-default';
    };

    // 获取金额前缀
    const getAmountPrefix = (type) => {
      const incomeTypes = ['recharge', 'received', 'received_payment', 'reward'];
      const expenseTypes = ['withdraw', 'payment', 'overdraft', 'fee'];
      if (incomeTypes.includes(type?.toLowerCase())) {
        return '+';
      }
      if (expenseTypes.includes(type?.toLowerCase())) {
        return '-';
      }
      return '';
    };

    // 获取金额样式类
    const getAmountClass = (type) => {
      const incomeTypes = ['recharge', 'received', 'received_payment', 'reward'];
      if (incomeTypes.includes(type?.toLowerCase())) {
        return 'amount-income';
      }
      return 'amount-expense';
    };

    // 格式化时间
    const formatTime = (timeStr) => {
      if (!timeStr) return '';
      const date = new Date(timeStr);
      return date.toLocaleString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit',
        second: '2-digit'
      });
    };

    // 监听交易更新事件
    const handleTransactionUpdate = () => {
      console.log('收到交易更新事件，开始刷新交易明细');
      fetchTransactions();
    };

    onMounted(() => {
      fetchTransactions();
      // 监听交易更新事件
      eventBus.on('transactionUpdated', handleTransactionUpdate);
    });

    onUnmounted(() => {
      // 清理事件监听
      eventBus.off('transactionUpdated', handleTransactionUpdate);
    });

    return {
      loading,
      transactions,
      currentFilter,
      filteredTransactions,
      fetchTransactions,
      getTypeFromNumber,
      getTransactionTypeName,
      getTransactionIcon,
      getTransactionTypeClass,
      getAmountPrefix,
      getAmountClass,
      formatTime,
      viewDetail,
      viewDetailByOrder
    };
  }
};
</script>

<style scoped>
.transactions-container {
  max-width: 600px;
  margin: 0 auto;
  min-height: 100vh;
  background-color: #f5f7fa;
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

.transactions-content {
  padding-top: 14vw;
}

.filter-section {
  background: white;
  padding: 3vw 4vw;
  margin-bottom: 2vw;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.refresh-section {
  margin-bottom: 3vw;
}

.refresh-btn {
  background: #0097FF;
  color: white;
  border: none;
  padding: 2.5vw 4vw;
  border-radius: 2vw;
  font-size: 3.4vw;
  display: flex;
  align-items: center;
  gap: 1.5vw;
  cursor: pointer;
  transition: all 0.3s;
}

.refresh-btn:hover {
  background: #0080e6;
}

.refresh-btn:active {
  transform: scale(0.98);
}

.filter-tabs {
  display: flex;
  gap: 2vw;
  overflow-x: auto;
  -webkit-overflow-scrolling: touch;
}

.filter-tab {
  padding: 2vw 4vw;
  border-radius: 4vw;
  font-size: 3.6vw;
  color: #666;
  background: #f5f7fa;
  white-space: nowrap;
  cursor: pointer;
  transition: all 0.3s;
}

.filter-tab.active {
  background: #0097FF;
  color: white;
}

.transactions-list {
  padding: 0 4vw;
}

.transaction-item {
  background: white;
  border-radius: 3vw;
  padding: 4vw;
  margin-bottom: 3vw;
  display: flex;
  align-items: flex-start;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.transaction-icon {
  width: 10vw;
  height: 10vw;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 3vw;
  flex-shrink: 0;
}

.transaction-icon i {
  font-size: 5vw;
  color: white;
}

.type-recharge {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.type-withdraw {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.type-payment {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.type-received {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.type-overdraft {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.type-repay {
  background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
}

.type-reward {
  background: linear-gradient(135deg, #fad961 0%, #f76b1c 100%);
}

.type-fee {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.type-default {
  background: linear-gradient(135deg, #d299c2 0%, #fef9d7 100%);
}

.transaction-info {
  flex: 1;
  min-width: 0;
}

.transaction-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2vw;
}

.transaction-title {
  display: flex;
  align-items: center;
  gap: 2vw;
}

.transaction-type {
  font-size: 4vw;
  font-weight: 500;
  color: #333;
}

.transaction-type-badge {
  font-size: 2.8vw;
  padding: 0.5vw 1.5vw;
  background: #f0f0f0;
  border-radius: 1vw;
  color: #666;
  font-weight: normal;
}

.transaction-amount {
  font-size: 4.5vw;
  font-weight: bold;
}

.amount-income {
  color: #52c41a;
}

.amount-expense {
  color: #ff4d4f;
}

.transaction-details {
  font-size: 3.2vw;
  color: #999;
  line-height: 1.6;
}

.transaction-reason {
  margin-bottom: 1.5vw;
  color: #666;
  display: flex;
  align-items: flex-start;
  gap: 1vw;
}

.reason-text {
  flex: 1;
  word-break: break-word;
}

.transaction-time {
  margin-bottom: 1vw;
  display: flex;
  align-items: center;
  gap: 1vw;
}

.transaction-time i {
  font-size: 3vw;
  color: #999;
}

.transaction-counterparty {
  color: #999;
  margin-bottom: 1vw;
}

.transaction-order {
  color: #999;
  font-size: 3vw;
}

.label {
  color: #999;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20vw;
  color: #999;
}

.empty-state i {
  font-size: 15vw;
  margin-bottom: 4vw;
  color: #ddd;
}

.empty-state p {
  font-size: 4vw;
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
</style>

