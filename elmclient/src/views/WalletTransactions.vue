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
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '../utils/request';
import { toast } from '../utils/toast';
import BackButton from '../components/BackButton.vue';

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
        // Apifox 定义：GET /api/wallet/transaction/list，需要 type/status/startDate/endDate 四个必填查询参数
        const params = {
          // -1 代表全部（若后端不支持，可按需要调整为具体值）
          type: -1,
          status: -1,
          startDate: '1970-01-01',
          endDate: new Date().toISOString()
        };

    const viewDetailByOrder = async (orderId) => {
      try {
        const response = await request.get('/api/wallet/transaction/detail/order', {
          params: { orderId }
        });
        if (response.success && response.data) {
          const d = response.data;
          const mapType = (n) => ({ 0: 'payment', 1: 'received', 2: 'withdraw', 3: 'recharge' }[n] ?? 'unknown');
          const detail = {
            id: d.id,
            transactionType: mapType(d.type),
            amount: d.amount,
            fee: d.fee,
            transactionTime: d.createTime,
            status: d.status,
            fromAccount: d.from_account,
            toAccount: d.to_account,
            fromAccountName: d.from_account_name,
            toAccountName: d.to_account_name,
            feeRate: d.fee_rate
          };
          alert(`订单明细#${detail.id}\n类型: ${getTransactionTypeName(detail.transactionType)}\n金额: ${detail.amount}\n时间: ${detail.transactionTime}`);
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
        if (response.success && response.data) {
          const d = response.data;
          const mapType = (n) => ({ 0: 'payment', 1: 'received', 2: 'withdraw', 3: 'recharge' }[n] ?? 'unknown');
          const detail = {
            id: d.id,
            transactionType: mapType(d.type),
            amount: d.amount,
            fee: d.fee,
            transactionTime: d.createTime,
            status: d.status,
            fromAccount: d.from_account,
            toAccount: d.to_account,
            fromAccountName: d.from_account_name,
            toAccountName: d.to_account_name,
            feeRate: d.fee_rate
          };
          // 简易演示：弹窗查看（后续可改为独立详情页/弹窗）
          alert(`明细#${detail.id}\n类型: ${getTransactionTypeName(detail.transactionType)}\n金额: ${detail.amount}\n时间: ${detail.transactionTime}`);
        } else {
          toast.error('获取明细详情失败');
        }
      } catch (e) {
        console.error('获取明细详情失败:', e);
        toast.error('获取明细详情失败');
      }
    };
        const response = await request.get('/api/wallet/transaction/list', { params });
        if (response.success) {
          const list = Array.isArray(response.data) ? response.data : [];
          const mapType = (n) => {
            const m = { 0: 'payment', 1: 'received', 2: 'withdraw', 3: 'recharge' };
            return m[n] ?? 'unknown';
          };
          transactions.value = list.map(it => ({
            id: it.id,
            transactionType: mapType(it.type),
            amount: it.amount ?? 0,
            fee: it.fee ?? 0,
            transactionTime: it.createTime,
            reason: it.reason ?? ''
          }));
        } else {
          toast.error('获取交易明细失败');
        }
      } catch (error) {
        console.error('获取交易明细失败:', error);
        toast.error('获取交易明细失败');
      } finally {
        loading.value = false;
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

    onMounted(() => {
      fetchTransactions();
    });

    return {
      loading,
      transactions,
      currentFilter,
      filteredTransactions,
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

