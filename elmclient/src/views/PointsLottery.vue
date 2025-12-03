<template>
  <div class="simple-lottery-page">
    <div class="header">
      <h3>积分抽奖 (精简版)</h3>
      <span class="rules-link" @click="openRules">活动规则</span>
    </div>

    <div class="points-balance-card card">
      <div class="points-info">
        <span class="label">我的当前积分</span>
        <span class="value">{{ formatPoints(currentPoints) }}</span>
      </div>
      <button class="earn-btn" @click="goToPointsPage">赚取积分</button>
    </div>

    <div class="member-info-card card">
      <div class="member-text">
        <p class="member-name">当前等级：<strong>{{ lotteryInfo.memberLevelName || '加载中...' }}</strong></p>
        <p class="chances-text">
          本月免费机会：
          <span class="chances-count">{{ lotteryInfo.remainingChances }}</span> 次 / {{ lotteryInfo.monthlyLimit }}
        </p>
      </div>
    </div>

    <div class="lottery-area card">
      <h4>🎉 专属奖池</h4>

      <div class="loading-overlay" v-if="loading.info || loading.draw">
        <div class="spinner"></div>
        <p v-if="loading.info">加载抽奖信息...</p>
        <p v-else-if="loading.draw">抽奖进行中，请稍候...</p>
      </div>
      
      <div v-else-if="errorMessage" class="error-state">
        <p>⚠️ {{ errorMessage }}</p>
        <button @click="fetchInitialData">重试</button>
      </div>

      <div v-else-if="!loading.info" class="lottery-content">
        <div class="prize-pool-display">
          <span v-for="(item, index) in simplePrizes" :key="index" class="prize-tag">
            {{ item.name }}
          </span>
        </div>

        <div class="lottery-grid-container">
          <div 
            v-for="(prize, index) in prizeGrid" 
            :key="index" 
            class="grid-item"
            :class="{ 
              'center-button': prize.isButton,
              'active-prize': prize.index === activeIndex,
              'winning-flash': prize.index === finalPrizeIndex && !loading.draw
            }"
            @click="prize.isButton ? startLottery() : null"
          >
            <template v-if="prize.isButton">
              <div 
                class="draw-center-button"
                :class="{ 'disabled': !canStartLottery || loading.draw }"
              >
                <span>点击抽奖</span>
                <span class="cost-text">{{ drawCost }}</span>
              </div>
            </template>
            <template v-else>
              <div class="prize-content">
                <span class="prize-icon">{{ getPrizeIcon(prize.lotteryType) }}</span>
                <span class="prize-name">{{ prize.name }}</span>
                <span class="prize-points" v-if="prize.pointsReward > 0">
                    {{ formatPoints(prize.pointsReward) }} 积分
                </span>
              </div>
            </template>
          </div>
        </div>
      </div>
    </div>

    <div class="history-section card">
      <h4>📋 中奖记录 (近 {{ lotteryRecords.length }} 条)</h4>
      <p v-if="loading.records" class="empty-state">加载中...</p>
      <ul class="prize-list" v-else>
        <li v-for="record in lotteryRecords" :key="record.id">
          <span :class="{ 'prize-win': record.lotteryType !== 0 }">
            {{ formatRecord(record) }}
          </span>
          <span class="time">{{ formatTime(record.createTime) }}</span>
        </li>
        <li class="empty-state" v-if="lotteryRecords.length === 0">暂无中奖记录</li>
      </ul>
    </div>
    
    <!-- 内联 modal 替换组件 -->
    <teleport to="body">
        <div v-if="showRules" class="modal-mask" @click="showRules = false">
            <div class="modal-wrapper">
                <div class="modal-container" @click.stop>
                    <div class="modal-header">
                        <h3>活动规则</h3>
                        <button class="close-btn" @click="showRules = false">×</button>
                    </div>
                    <div class="modal-body">
                        <p>1. <span class="rule-title">会员免费机会：</span>{{ memberRules }}</p>
                        <p>2. <span class="rule-title">抽奖消耗：</span>免费机会用完后，每次抽奖需消耗 100 积分。</p>
                        <p>3. <span class="rule-title">当前奖池：</span>您的专属奖池包含：{{ prizeDescriptions }}</p>
                        <p>4. <span class="rule-title">积分有效期：</span>活动中获得的积分有效期为 15 天。</p>
                        <p>5. <span class="rule-title">其他：</span>中奖记录仅显示最近 5 条。最终解释权归本公司所有。</p>
                        <p class="level-info">当前等级：<span style="color: #ff4d4f;">{{ lotteryInfo.memberLevelName || '加载中...' }}</span></p>
                    </div>
                </div>
            </div>
        </div>
    </teleport>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, defineComponent } from 'vue';
import request from '@/utils/request'; 
import { toast } from '@/utils/toast'; 

// --- 规则弹窗组件定义 (需求 1 优化) ---
const RulesModal = defineComponent({
    props: {
        visible: Boolean,
        memberLevelName: String,
        prizes: { // 接收奖池数据
            type: Array,
            default: () => []
        }
    },
    emits: ['close'],
    setup(props, { emit }) {
        console.log('RulesModal setup called with visible:', props.visible);
        // 整理奖池说明
        const prizeDescriptions = computed(() => {
            if (props.prizes.length === 0) {
                return '奖池信息加载中或未配置。';
            }
            // 排除没中奖（LotteryType=0）的项，整理奖品名称
            const names = props.prizes
                .filter(p => p.lotteryType !== 0)
                .map(p => {
                    if (p.lotteryType === 1 && p.pointsReward > 0) {
                        return `+${(p.pointsReward).toLocaleString()} 积分`;
                    }
                    return p.lotteryTypeName;
                });
            
            // 使用中文连接符连接奖品名称
            return names.join('、');
        });

        // 生成会员规则说明
        const memberRules = computed(() => {
             const level = props.memberLevelName;
             switch (level) {
                case '白银会员': return '每月有 **1** 次免费抽奖机会。';
                case '黄金会员': return '每月有 **2** 次免费抽奖机会。';
                case '钻石会员': return '每月有 **3** 次免费抽奖机会。';
                case '普通会员': 
                default: return '当前为普通用户，暂无免费抽奖机会。';
             }
        });

        return {
            prizeDescriptions,
            memberRules,
        };
    },
    template: `
        <teleport to="body">
            <div v-if="visible" class="modal-mask" @click="$emit('close')">
                <div class="modal-wrapper">
                    <div class="modal-container" @click.stop>
                        <div class="modal-header">
                            <h3>活动规则</h3>
                            <button class="close-btn" @click="$emit('close')">×</button>
                        </div>
                        <div class="modal-body">
                            <p>1. <span class="rule-title">会员免费机会：</span></p>
                            <p class="rule-content">{{ memberRules }} 免费机会每月重置，请及时使用。</p>

                            <p>2. <span class="rule-title">抽奖消耗：</span>免费机会用完后，每次抽奖需消耗 100 积分。</p>

                            <p>3. <span class="rule-title">当前奖池：</span>您的专属奖池包含：{{ prizeDescriptions }}。</p>

                            <p>4. <span class="rule-title">积分有效期：</span>活动中获得的积分有效期为 15 天。</p>
                            <p>5. <span class="rule-title">其他：</span>中奖记录仅显示最近 5 条。最终解释权归本公司所有。</p>

                            <p class="level-info">当前等级：<span style="color: #ff4d4f;">{{ memberLevelName || '加载中...' }}</span></p>
                        </div>
                    </div>
                </div>
            </div>
        </teleport>
    `
});

// --- 状态管理 (保持不变) ---
const showRules = ref(false); 
const currentPoints = ref(0); 
const loading = ref({
    info: true, 
    records: true, 
    draw: false,
});
const errorMessage = ref('');
const activeIndex = ref(-1); 
const finalPrizeIndex = ref(-1); 
const prizeGrid = ref([]);
const lotteryRecords = ref([]);
const winningResult = ref(null); 

const lotteryInfo = ref({
    memberLevel: 0,
    memberLevelName: '普通会员',
    monthlyLimit: 0,
    usedChances: 0,
    remainingChances: 0,
    canLottery: false,
    prizes: [],
});

// 计算属性 (保持不变)
const canStartLottery = computed(() => {
    const neededCost = lotteryInfo.value.remainingChances > 0 ? 0 : 100;
    return lotteryInfo.value.remainingChances > 0 || currentPoints.value >= neededCost; 
});


const drawCost = computed(() => {
    return lotteryInfo.value.remainingChances > 0 ? '免费机会' : '100 积分'; 
});

const simplePrizes = computed(() => {
    return lotteryInfo.value.prizes
        .filter(p => p.lotteryType !== 0)
        .slice(0, 3)
        .map(p => ({ name: p.lotteryTypeName }));
});

// 计算属性用于内联 modal
const prizeDescriptions = computed(() => {
    if (lotteryInfo.value.prizes.length === 0) {
        return '奖池信息加载中或未配置。';
    }
    // 排除没中奖（LotteryType=0）的项，整理奖品名称
    const names = lotteryInfo.value.prizes
        .filter(p => p.lotteryType !== 0)
        .map(p => {
            if (p.lotteryType === 1 && p.pointsReward > 0) {
                return `+${(p.pointsReward).toLocaleString()} 积分`;
            }
            return p.lotteryTypeName;
        });

    // 使用中文连接符连接奖品名称
    return names.join('、');
});

const memberRules = computed(() => {
     const level = lotteryInfo.value.memberLevelName;
     switch (level) {
        case '白银会员': return '每月有 **1** 次免费抽奖机会。';
        case '黄金会员': return '每月有 **2** 次免费抽奖机会。';
        case '钻石会员': return '每月有 **3** 次免费抽奖机会。';
        case '普通会员':
        default: return '当前为普通用户，暂无免费抽奖机会。';
     }
});

// --- 辅助函数：赚取积分跳转 (需求 2) ---

const goToPointsPage = () => {
    // 假设存在一个全局的 router 对象或跳转方法
    if (typeof window.router !== 'undefined' && typeof window.router.push === 'function') {
        window.router.push('/points');
    } else {
        // 在没有 Vue Router 环境下，使用 toast 模拟跳转行为
        toast.info('跳转到 /points 页面逻辑已触发。');
        console.log('Navigate to /points');
    }
};

// --- 调试函数：打开规则弹窗 ---

const openRules = () => {
    console.log('点击活动规则按钮');
    console.log('当前 showRules 值:', showRules.value);
    showRules.value = true;
    console.log('设置后 showRules 值:', showRules.value);

    // 检查 DOM 中是否有 modal 元素
    setTimeout(() => {
        const modalMask = document.querySelector('.modal-mask');
        console.log('modal-mask 元素:', modalMask);
        if (modalMask) {
            console.log('modal-mask 样式:', window.getComputedStyle(modalMask));
        }
    }, 100);
};

// --- 生命周期和数据获取函数 (保持不变) ---
onMounted(() => {
    fetchInitialData();
});

const fetchPointsAccount = async () => {
    try {
        const res = await request.get('/api/points/account');
        if (res.success && res.data) {
            currentPoints.value = res.data.totalPoints || 0; 
        } else {
            console.warn('获取积分账户失败:', res.message);
        }
    } catch (err) {
        console.error('获取积分账户网络错误:', err);
    }
};

const formatPrizesToGrid = (apiPrizes) => {
    const grid = [];
    const prizePositions = [0, 1, 2, 5, 8, 7, 6, 3]; 
    
    const defaultPrize = { id: 'N_A', lotteryTypeName: '再接再厉', lotteryType: 0, pointsReward: 0 };
    const prizesToFill = apiPrizes.slice(0, 8); 
    if (prizesToFill.length < 8) {
        for (let i = prizesToFill.length; i < 8; i++) {
            prizesToFill.push({...defaultPrize, id: `DEF_${i}`}); 
        }
    }
        
    let prizeIndex = 0; 

    for (let i = 0; i < 9; i++) {
        if (i === 4) {
            grid.push({ index: 4, isButton: true, name: '抽奖' }); 
        } else {
            const prizeData = prizesToFill[prizeIndex];

            grid.push({
                index: i,
                isButton: false,
                name: prizeData?.lotteryTypeName || 'N/A',
                lotteryType: prizeData?.lotteryType,
                pointsReward: prizeData?.pointsReward || 0,
                prizeId: prizeData?.id,
            });
            prizeIndex++;
        }
    }
    prizeGrid.value = grid;
};

const fetchInitialData = async () => {
    errorMessage.value = '';
    
    await fetchPointsAccount(); 
    
    loading.value.info = true;
    try {
        const res = await request.get('/api/points/lottery/info');
        if (res.success && res.data) {
            lotteryInfo.value = res.data;
            formatPrizesToGrid(res.data.prizes || []);
        } else {
            errorMessage.value = res.message || '获取抽奖信息失败';
            toast.error(errorMessage.value);
        }
    } catch (err) {
        console.error('获取抽奖信息失败:', err);
        errorMessage.value = '网络错误，无法获取抽奖信息';
    } finally {
        loading.value.info = false;
    }

    loading.value.records = true;
    try {
        const res = await request.get('/api/points/lottery/records', { params: { limit: 5 } });
        if (res.success && res.data) {
            lotteryRecords.value = res.data;
        } else {
            console.warn('获取抽奖记录失败:', res.message);
        }
    } catch (err) {
        console.error('获取抽奖记录失败:', err);
    } finally {
        loading.value.records = false;
    }
};

// --- 抽奖动画逻辑 (保持不变) ---
const GRID_SEQUENCE = [0, 1, 2, 5, 8, 7, 6, 3]; 

const animateLottery = (finalPrizeSeqIndex) => {
    return new Promise(resolve => {
        let startTime = Date.now();
        const minCircles = 3;
        const targetStopStep = minCircles * 8 + finalPrizeSeqIndex; 
        const totalDuration = 3500; 
        
        const easeOutCubic = (t) => 1 - Math.pow(1 - t, 3);
        
        const animate = () => {
            const elapsedTime = Date.now() - startTime;
            const progress = Math.min(1, elapsedTime / totalDuration); 
            
            const totalAnimatedSteps = easeOutCubic(progress) * (targetStopStep + 8);
            
            const currentStep = Math.floor(totalAnimatedSteps);
            const seqIndex = currentStep % 8; 
            
            activeIndex.value = GRID_SEQUENCE[seqIndex];

            if (progress < 1) {
                requestAnimationFrame(animate);
            } else {
                activeIndex.value = GRID_SEQUENCE[finalPrizeSeqIndex]; 
                finalPrizeIndex.value = activeIndex.value;
                resolve();
            }
        };

        requestAnimationFrame(animate);
    });
};

const startLottery = async () => {
    if (!canStartLottery.value) {
        toast.warn('您的抽奖机会已用完或积分不足。');
        return;
    }
    if (loading.value.draw) return;

    loading.value.draw = true;
    activeIndex.value = 4; 
    finalPrizeIndex.value = -1; 

    try {
        const res = await request.post('/api/points/lottery/draw');

        if (res.success && res.data) {
            const result = res.data;
            
            let winningPrize;
            if (result.prizeId) {
                 winningPrize = prizeGrid.value.find(p => p.prizeId === result.prizeId);
            }
            if (!winningPrize) {
                winningPrize = prizeGrid.value.find(p => 
                    p.lotteryType === result.lotteryType
                );
            }

            const finalGridIndex = winningPrize ? winningPrize.index : 3; 
            const finalPrizeSeqIndex = GRID_SEQUENCE.findIndex(i => i === finalGridIndex);
            
            await animateLottery(finalPrizeSeqIndex); 

            winningResult.value = result;
            
            toast.success(`🎉 恭喜您：${result.description || result.lotteryTypeName}`);

            setTimeout(() => {
                fetchInitialData(); 
                loading.value.draw = false;
            }, 1500); 

        } else {
            activeIndex.value = -1; 
            finalPrizeIndex.value = -1;
            toast.error(res.message || '抽奖失败，请稍后重试。');
            loading.value.draw = false; 
        }

    } catch (err) {
        activeIndex.value = -1; 
        finalPrizeIndex.value = -1;
        console.error('抽奖请求失败:', err);
        toast.error('网络错误或系统繁忙，抽奖失败。');
        loading.value.draw = false;
    } 
};

// --- 辅助函数 (保持不变) ---
const formatPoints = (points) => {
    return (points || 0).toLocaleString('en-US'); 
};
const getPrizeIcon = (type) => {
    switch(type) {
        case 1: return '💰'; 
        case 2: return '✨'; 
        case 0: 
        default: return '🍀'; 
    }
};
const formatRecord = (record) => {
    if (record.lotteryType === 1) { 
        return `🎉 恭喜获得 ${formatPoints(record.pointsReward)} 积分`;
    } else if (record.lotteryType === 2) { 
        return `🌟 获得积分翻倍卡 (x${record.pointsMultiplier || '?'})，原积分 ${formatPoints(record.originalPoints || 0)}`;
    } else { 
        return `😢 遗憾，${record.lotteryTypeName}`;
    }
};
const formatTime = (timeStr) => {
    if (!timeStr) return '';
    const date = new Date(timeStr);
    return date.toLocaleString('zh-CN', { hour: '2-digit', minute: '2-digit' }); 
};
</script>

<style scoped>
/* ==================================== */
/* CSS 样式 (保持不变，仅为规则弹窗添加样式，使描述更清晰) */
/* ==================================== */
:root {
  --primary-color: #ff4d4f; /* 核心红色/抽奖按钮 */
  --accent-color: #ffc53d; /* 金色/积分/奖品高亮 */
  --bg-color: #f4f7f9;
  --card-bg: #fff;
}

.simple-lottery-page {
  padding: 0;
  max-width: 600px;
  margin: 0 auto;
  background-color: var(--bg-color);
  min-height: 100vh;
  font-family: 'PingFang SC', 'Helvetica Neue', sans-serif;
  color: #333;
}

.header {
  background-color: var(--card-bg);
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
}
.header h3 { margin: 0; font-size: 1.2rem; color: #333; }
.rules-link { font-size: 0.9rem; color: #1890ff; cursor: pointer; }

.card {
  background-color: var(--card-bg);
  border-radius: 12px;
  padding: 15px;
  margin: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08); 
  position: relative;
}
.card h4 {
  color: #333;
  font-size: 1rem;
  font-weight: 600;
  margin-bottom: 10px;
  border-left: 4px solid var(--primary-color);
  padding-left: 10px;
}

/* 积分余额卡片 */
.points-balance-card {
  background: linear-gradient(90deg, #ffc53d, #ff7a45); 
  color: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.points-info {
  display: flex;
  flex-direction: column;
}
.points-info .label { font-size: 0.9rem; margin-bottom: 5px; }
.points-info .value { 
    font-size: 2rem; 
    font-weight: 900; 
    min-width: 80px; 
}
.earn-btn {
  background-color: white;
  color: #000; /* Changed to black for better visibility */
  border: none;
  padding: 8px 18px;
  border-radius: 20px;
  font-weight: bold;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  cursor: pointer;
  transition: all 0.2s;
}

/* 机会信息卡片 */
.member-info-card {
  margin-top: -5px; 
  border: 1px solid #ffbb96; 
}
.member-text .member-name, .chances-text { margin: 5px 0; font-size: 0.95rem; }
.chances-count { color: var(--primary-color); font-weight: bold; font-size: 1.1rem; }

/* 奖池概览 */
.prize-pool-display {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 15px;
  justify-content: center;
}
.prize-tag {
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 0.8rem;
  background-color: #ffe7ba; 
  color: #fa8c16;
  font-weight: 500;
}

/* 抽奖网格布局 */
.lottery-grid-container {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-template-rows: repeat(3, 1fr);
  width: 70vw; 
  height: 70vw; 
  max-width: 300px;
  max-height: 300px;
  margin: 20px auto;
  border: 4px solid var(--primary-color); 
  border-radius: 8px;
  overflow: hidden;
}

.grid-item {
  border: 1px solid #ffe0b2; 
  display: flex;
  justify-content: center;
  align-items: center;
  text-align: center;
  font-size: 0.8rem;
  background-color: #fff9e6; 
  cursor: default;
  transition: background-color 0.1s;
}

.prize-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 5px;
}
.prize-icon {
    font-size: 1.5rem;
    margin-bottom: 2px;
}
.prize-name { font-weight: 600; color: #333; font-size: 0.8rem; line-height: 1.2;}
.prize-points { 
    font-size: 0.7rem;
    color: #ff7a45; 
    font-weight: bold;
    margin-top: 2px;
}

/* 中奖高亮样式 */
.grid-item.active-prize {
  background-color: var(--accent-color); 
  transform: scale(1.05);
  box-shadow: 0 0 10px rgba(255, 197, 61, 0.8);
  z-index: 5;
}

/* 优化：中奖闪烁效果 */
.grid-item.winning-flash {
    animation: flash-border 0.3s ease-in-out 6 alternate; 
}

@keyframes flash-border {
  0% { 
    background-color: var(--accent-color); 
    box-shadow: 0 0 5px rgba(255, 197, 61, 0.5);
  }
  100% { 
    background-color: #ffaa00; 
    box-shadow: 0 0 15px var(--accent-color);
  }
}

/* 中心抽奖按钮 */
.center-button {
  background: var(--primary-color);
  cursor: pointer;
  color: white;
  border: none;
  font-size: 1.1rem;
}
.draw-center-button {
  display: flex;
  flex-direction: column;
  font-weight: bold;
  padding: 10px;
  line-height: 1.2;
  width: 100%;
  height: 100%;
  justify-content: center;
  background-color: #ff4d4f; /* Explicit red background color */
}
/* 禁用状态优化 */
.draw-center-button.disabled {
    background-color: #ccc !important; 
    cursor: not-allowed !important;
    pointer-events: none !important;
    color: #666 !important;
}

/* 历史记录 */
.prize-list {
  list-style: none;
  padding: 0;
}
.prize-list li {
  padding: 8px 0;
  border-bottom: 1px dotted #eee;
  font-size: 0.9rem;
  display: flex;
  justify-content: space-between;
}
.prize-list .prize-win { color: #2e7d32; font-weight: 600; }
.prize-list .time { color: #999; font-size: 0.8rem; }
.empty-state { text-align: center; color: #999; padding: 10px 0; }

/* 加载和错误状态 */
.loading-overlay {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background: rgba(255, 255, 255, 0.9);
    z-index: 10;
    border-radius: 12px;
    font-size: 1rem;
    color: #666;
}
.error-state {
    text-align: center;
    padding: 20px;
    color: var(--primary-color);
}
.error-state button {
    margin-top: 10px;
    background: var(--primary-color);
    color: white;
    border: none;
    padding: 8px 15px;
    border-radius: 4px;
    cursor: pointer;
}


</style>

<style>
/* 规则弹窗 Modal 样式 - moved to global for teleported content */
.modal-mask {
  position: fixed;
  z-index: 9998;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: opacity 0.3s ease;
}
.modal-wrapper {
  width: 100%;
  display: flex;
  justify-content: center;
}
.modal-container {
  width: 80%;
  max-width: 400px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.33);
  transition: all 0.3s ease;
  overflow: hidden;
}
.modal-header {
  padding: 15px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.modal-header h3 {
  margin: 0;
  font-size: 1.1rem;
  color: #ff4d4f;
}
.close-btn {
    background: none;
    border: none;
    font-size: 1.2rem;
    color: #999;
    cursor: pointer;
}
.modal-body {
  padding: 15px;
  max-height: 70vh;
  overflow-y: auto;
}
.modal-body p {
    font-size: 0.9rem;
    line-height: 1.5;
    color: #555;
    margin-bottom: 8px;
}
/* 规则说明的标题和内容样式，使规则更清晰 */
.modal-body .rule-title {
    font-weight: bold;
    color: #333;
}
.modal-body .rule-content {
    margin-left: 10px;
    color: #666;
    margin-bottom: 12px;
}

.modal-body .level-info {
    margin-top: 15px;
    padding-top: 10px;
    border-top: 1px dashed #ffbb96;
    font-weight: bold;
    color: #888;
}

/* 弹窗过渡动画 */
.modal-enter-active,
.modal-leave-active {
  transition: all 0.3s ease;
}
.modal-enter-from,
.modal-leave-to {
  opacity: 0;
}
.modal-enter-from .modal-container,
.modal-leave-to .modal-container {
  -webkit-transform: scale(1.1);
  transform: scale(1.1);
}
</style>
