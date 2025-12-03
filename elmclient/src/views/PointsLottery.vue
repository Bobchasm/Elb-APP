<template>
  <div class="simple-lottery-page">
    <div class="header">
      <h3>积分抽奖</h3>
      <span class="rules-link" @click="openRules">活动规则</span>
    </div>

    <div class="points-balance-card card">
      <div class="points-info">
        <div class="points-row">
          <span class="label">当前积分</span>
          <span class="value">{{ formatPoints(lotteryInfo.totalPoints || 0) }}</span>
        </div>
        <div class="points-row">
          <span class="label">可用积分</span>
          <span class="value">{{ formatPoints(lotteryInfo.availablePoints || 0) }}</span>
        </div>
      </div>
      <button class="earn-btn" @click="goToPointsPage">更多抽奖机会</button>
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
                <span class="prize-name">{{ prize.name || 'N/A' }}</span>
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
    
    <teleport to="body">
        <div v-if="showRules" class="modal-mask" @click="showRules = false">
            <div class="modal-wrapper">
                <div class="modal-container" @click.stop>
                    <div class="modal-header">
                        <h3>活动规则</h3>
                        <button class="close-btn" @click="showRules = false">×</button>
                    </div>
                    <div class="modal-body">
                        <p>1. <span class="rule-title">会员免费机会：</span></p>
                        <p class="rule-content" v-html="memberRules"></p>

                        <p>2. <span class="rule-title">抽奖消耗：</span>免费机会用完后，每次抽奖需消耗 100 积分。</p>

                        <p>3. <span class="rule-title">当前奖池：</span>{{ prizeDescriptions }}。</p>

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
import { ref, onMounted, computed } from 'vue';
// 假设这是你的自定义请求和提示工具
import request from '@/utils/request'; 
import { toast } from '@/utils/toast'; 

// --- 状态管理 ---
const showRules = ref(false); 
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
    totalPoints: 0, 
    availablePoints: 0, 
    memberLevel: 0,
    memberLevelName: '普通用户',
    monthlyLimit: 0,
    usedChances: 0,
    remainingChances: 0,
    canLottery: false,
    prizes: [], // 存储后端返回的奖品列表
});

// --- 计算属性 (规则和抽奖状态) ---
const canStartLottery = computed(() => {
    const neededCost = lotteryInfo.value.remainingChances > 0 ? 0 : 100;
    return lotteryInfo.value.remainingChances > 0 || lotteryInfo.value.availablePoints >= neededCost; 
});

const drawCost = computed(() => {
    return lotteryInfo.value.remainingChances > 0 ? '免费机会' : '100 积分'; 
});

const simplePrizes = computed(() => {
    // 奖池概览只展示非谢谢参与的奖品，取前三个
    return lotteryInfo.value.prizes
        .filter(p => p.lotteryType !== 0 && p.lotteryTypeName !== '没中奖')
        .slice(0, 3)
        .map(p => ({ name: p.lotteryTypeName }));
});

// 规则弹窗-会员机会描述
const memberRules = computed(() => {
    const level = lotteryInfo.value.memberLevel;
    let limitText = '暂无免费抽奖机会';
    switch (level) {
        case 1: limitText = '每月有 1 次免费抽奖机会'; break;
        case 2: limitText = '每月有 2 次免费抽奖机会'; break;
        case 3: limitText = '每月有 3 次免费抽奖机会'; break;
    }
    return limitText + '。免费机会每月重置，请及时使用。';
});

// 规则弹窗-奖池描述
const prizeDescriptions = computed(() => {
    const level = lotteryInfo.value.memberLevel;
    let pool = '';
    
    switch (level) {
        case 1: 
            pool = '没中奖、+20积分、+50积分、+100积分'; 
            break;
        case 2: 
            pool = '没中奖、+50积分、+100积分、积分翻倍'; 
            break;
        case 3: 
            pool = '没中奖、+100积分、+200积分、积分翻倍'; 
            break;
        case 0:
        default: 
            pool = '没中奖。'; 
            break;
    }
    
    return `当前奖池包含：${pool} `;
});

// --- 辅助函数：赚取积分跳转 ---
const goToPointsPage = () => {
    // 模拟跳转逻辑
    toast.info('跳转到赚取积分页面...');
};

const openRules = () => {
    showRules.value = true;
};

// --- 数据获取函数 ---

const fetchPointsAccount = async () => {
    try {
        const res = await request.get('/api/points/account'); 
        if (res.success && res.data) {
            lotteryInfo.value.totalPoints = res.data.totalPoints || 0; 
            lotteryInfo.value.availablePoints = res.data.availablePoints || 0; 
        } else {
            console.warn('获取积分账户失败:', res.message);
        }
    } catch (err) {
        console.error('获取积分账户网络错误:', err);
    }
};

/**
 * ❗ 需求 1 修复: 确保 8 个格子都有数据
 * 将后端返回的奖品数据格式化并填充到九宫格中。
 * @param {Array} apiPrizes 后端返回的奖品列表
 */
const formatPrizesToGrid = (apiPrizes) => {
    const grid = [];
    // 九宫格的非按钮格子顺序 (顺时针，从 0 开始)
    const GRID_SEQUENCE_MAP = [0, 1, 2, 5, 8, 7, 6, 3]; 
    
    // 优化后的默认占位奖品模板（用于填充不足 8 个的情况）
    const defaultPrizeTemplates = [
        { id: 'EMPTY_1', lotteryTypeName: '今日好运', lotteryType: 0, pointsReward: 0 },
        { id: 'EMPTY_2', lotteryTypeName: '幸运加持', lotteryType: 0, pointsReward: 0 },
        { id: 'EMPTY_3', lotteryTypeName: '谢谢参与', lotteryType: 0, pointsReward: 0 },
        { id: 'EMPTY_4', lotteryTypeName: '再来一次', lotteryType: 0, pointsReward: 0 },
    ];
    
    // 准备 8 个奖品数据 (优先使用后端返回的，不足则使用默认占位)
    let prizesToUse = [...apiPrizes];
    let defaultIndex = 0;
    while (prizesToUse.length < 8) {
        prizesToUse.push(defaultPrizeTemplates[defaultIndex % defaultPrizeTemplates.length]);
        defaultIndex++;
    }

    // 填充九宫格
    let prizeDataIndex = 0;
    for (let i = 0; i < 9; i++) {
        if (i === 4) {
            // 中心格：抽奖按钮
            grid.push({ index: 4, isButton: true, name: '抽奖' }); 
        } else {
            // 根据 GRID_SEQUENCE_MAP 确定当前格子对应的奖品数据索引
            // ❗ 修复核心逻辑：根据当前格子的索引 i，找到它在 GRID_SEQUENCE_MAP 中的位置，
            // 然后再用这个位置去取 prizesToUse 数组中的数据。
            const sequenceIndex = GRID_SEQUENCE_MAP.findIndex(seq => seq === i);
            const finalPrizeData = prizesToUse[sequenceIndex]; 

            // 优化：根据奖励类型优化显示名称
            let prizeName = finalPrizeData?.lotteryTypeName || 'N/A';
            if (finalPrizeData?.lotteryType === 1 && finalPrizeData?.pointsReward > 0) {
                 prizeName = `+${formatPoints(finalPrizeData.pointsReward)} 积分`;
            } else if (finalPrizeData?.lotteryType === 2) {
                 prizeName = '积分翻倍';
            }
            
            grid.push({
                index: i,
                isButton: false,
                name: prizeName, // ❗ 确保这里使用 prizeName
                lotteryType: finalPrizeData?.lotteryType,
                pointsReward: finalPrizeData?.pointsReward || 0,
                prizeId: finalPrizeData?.id,
            });
            prizeDataIndex++;
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
            Object.assign(lotteryInfo.value, res.data);
            
            // 确保积分不被丢失（如果 /lottery/info 接口不返回积分）
            if (!res.data.totalPoints) lotteryInfo.value.totalPoints = lotteryInfo.value.totalPoints;
            if (!res.data.availablePoints) lotteryInfo.value.availablePoints = lotteryInfo.value.availablePoints;
            
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

// --- 抽奖动画逻辑 ---
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

/**
 * ❗ 需求 2 修复：确保动画终点是随机的（即中奖的格子）
 */
const startLottery = async () => {
    if (!canStartLottery.value) {
        toast.warn(lotteryInfo.value.remainingChances > 0 ? '免费机会已用完，积分不足 100。' : '您的积分不足 100。');
        return;
    }
    if (loading.value.draw) return;

    loading.value.draw = true;
    activeIndex.value = 4; // 选中中心按钮
    finalPrizeIndex.value = -1; 

    try {
        const res = await request.post('/api/points/lottery/draw');

        if (res.success && res.data) {
            const result = res.data;
            
            // ❗ 核心修复点 2：根据后端返回的 prizeId 准确找到九宫格中的中奖格子
            const winningPrize = prizeGrid.value.find(p => p.prizeId === result.prizeId);
            
            let finalGridIndex;
            if (winningPrize) {
                finalGridIndex = winningPrize.index;
            } else {
                // 降级策略：如果 prizeId 匹配不到，用 lotteryType 匹配
                const fallbackPrize = prizeGrid.value.find(p => p.lotteryType === result.lotteryType);
                finalGridIndex = fallbackPrize ? fallbackPrize.index : 3; // 默认停在 '今日好运' (index 3)
                console.warn(`未通过 prizeId 匹配到格子，使用降级匹配到 index: ${finalGridIndex}`);
            }

            // 将九宫格索引 (0-8) 转换为动画序列索引 (0-7)
            const finalPrizeSeqIndex = GRID_SEQUENCE.findIndex(i => i === finalGridIndex);
            
            // 启动动画
            await animateLottery(finalPrizeSeqIndex); 

            winningResult.value = result;
            
            toast.success(`🎉 恭喜您：${result.description || winningPrize?.name || result.lotteryTypeName || '中奖啦'}`);

            setTimeout(() => {
                fetchInitialData(); // 刷新积分和机会
                loading.value.draw = false;
            }, 1500); 

        } else {
            // 抽奖失败，重置状态
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
        case 1: return '💰'; // 积分
        case 2: return '✨'; // 翻倍卡
        case 0: 
        default: return '🍀'; // 没中奖/占位
    }
};
const formatRecord = (record) => {
    if (record.lotteryType === 1) { 
        return `🎉 恭喜获得 ${formatPoints(record.pointsReward)} 积分`;
    } else if (record.lotteryType === 2) { 
        return `🌟 获得积分翻倍卡 (原积分 ${formatPoints(record.originalPoints || 0)})`;
    } else { 
        return `😢 遗憾，${record.lotteryTypeName}`;
    }
};
const formatTime = (timeStr) => {
    if (!timeStr) return '';
    const date = new Date(timeStr);
    return date.toLocaleString('zh-CN', { hour: '2-digit', minute: '2-digit' }); 
};

// --- 生命周期 ---
onMounted(() => {
    fetchInitialData();
});
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
.points-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.points-row:last-child {
  margin-bottom: 0;
}
.points-row .label { font-size: 0.9rem; }
.points-row .value {
    font-size: 1.8rem;
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
