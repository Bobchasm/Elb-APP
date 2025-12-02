<template>
    <div class="simple-lottery-page">
        <div class="header">
            <h3>积分抽奖 (精简版)</h3>
            <span class="rules-link">规则</span>
        </div>

        <div class="points-balance-card">
            <div class="points-info">
                <span class="label">我的当前积分</span>
                <span class="value">{{ currentPoints }}</span>
            </div>
            <button class="earn-btn">赚取</button>
        </div>
        
        <div class="member-info-card card">
            <div class="member-text">
                <p class="member-name">当前等级：<strong>{{ memberLevelName }}</strong></p>
                <p class="chances-text">
                    本月剩余免费机会：
                    <span class="chances-count">{{ remainingChances }}</span> 次
                </p>
            </div>
        </div>

        <div class="lottery-area card">
            <h4>专属奖池</h4>
            
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
                    :class="{ 'center-button': prize.isButton }"
                    @click="prize.isButton ? startLottery() : null"
                >
                    <template v-if="prize.isButton">
                        <div class="draw-center-button">
                            <span>点击抽奖</span>
                        </div>
                    </template>
                    <template v-else>
                        <span class="prize-name">{{ prize.name }}</span>
                    </template>
                </div>
            </div>
        </div>

        <div class="history-section card">
            <h4>中奖记录 (近 3 条)</h4>
            <ul class="prize-list">
                <li><span class="prize-win">🎉 恭喜您获得 100 积分</span><span class="time">10:00</span></li>
                <li><span>😢 遗憾 未中奖</span><span class="time">09:30</span></li>
                <li class="empty-state" v-if="false">暂无中奖记录</li>
            </ul>
        </div>
    </div>
</template>

<script setup>
import { ref } from 'vue';

// --- 静态或模拟数据 ---
const currentPoints = ref(1234);
const remainingChances = ref(1);
const memberLevelName = ref('黄金会员');

const simplePrizes = [
    { name: '+50 积分' }, 
    { name: '积分翻倍' }, 
    { name: '再接再厉' },
];

const prizeNames = ['+50 积分', '再接再厉', '+100 积分', '翻倍卡'];

// --- 简化逻辑：创建九宫格结构 ---
const createPrizeGrid = () => {
    const grid = [];
    let prizeIndex = 0;
    for (let i = 0; i < 9; i++) {
        if (i === 4) {
            grid.push({ isButton: true }); 
        } else {
            // 循环使用 4 种奖品名称填充 8 个格子
            grid.push({
                isButton: false,
                name: prizeNames[prizeIndex % prizeNames.length],
            });
            prizeIndex++;
        }
    }
    return grid;
};

const prizeGrid = createPrizeGrid();

// --- 简单交互函数 ---
const startLottery = () => {
    // 仅弹出提示，不执行复杂的抽奖动画和 API
    alert('点击了抽奖按钮！ (需补全抽奖逻辑)');
};
</script>

<style scoped>
/* ==================================== */
/* 极简样式 - 只保留结构和基础布局 */
/* ==================================== */
.simple-lottery-page {
    padding: 0;
    max-width: 600px;
    margin: 0 auto;
    background-color: #f4f7f9;
    min-height: 100vh;
    font-family: sans-serif;
}
.header {
    background-color: #fff;
    padding: 15px 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
}
.header h3 { margin: 0; font-size: 1.2rem; }
.rules-link { font-size: 0.9rem; color: #2979ff; }

.card {
    background-color: #fff;
    border-radius: 8px;
    padding: 15px;
    margin: 15px;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}
.card h4 {
    color: #333;
    font-size: 1rem;
    margin-bottom: 10px;
    border-left: 3px solid #ff9800;
    padding-left: 8px;
}

/* 积分余额卡片 */
.points-balance-card {
    background: #ff9800;
    color: white;
    display: flex;
    align-items: center;
    justify-content: space-between;
}
.points-info .label { font-size: 0.8rem; }
.points-info .value { font-size: 1.5rem; font-weight: bold; }
.earn-btn {
    background-color: white;
    color: #ff9800;
    border: none;
    padding: 5px 10px;
    border-radius: 15px;
}

/* 机会信息卡片 */
.member-info-card {
    border: 1px solid #eee;
    margin-top: -5px;
}
.member-text .member-name, .chances-text { margin: 0; font-size: 0.9rem; }
.chances-count { color: #f44336; font-weight: bold; }

/* 奖池展示区 */
.prize-pool-display {
    display: flex;
    flex-wrap: wrap;
    gap: 5px;
    margin-bottom: 10px;
}
.prize-tag {
    padding: 4px 8px;
    border-radius: 15px;
    font-size: 0.8rem;
    background-color: #f0f0f0;
}

/* 九宫格布局 */
.lottery-grid-container {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    grid-template-rows: repeat(3, 1fr);
    width: 250px;
    height: 250px;
    margin: 10px auto;
    border: 1px solid #ddd;
}

.grid-item {
    border: 1px solid #ddd;
    display: flex;
    justify-content: center;
    align-items: center;
    text-align: center;
    font-size: 0.8rem;
    background-color: #fff;
}
.prize-name { font-weight: 500; }

/* 中心按钮 */
.center-button {
    background-color: #f44336;
    cursor: pointer;
    color: white;
}
.draw-center-button {
    font-weight: bold;
    padding: 10px;
}

/* 历史记录 */
.prize-list {
    list-style: none;
    padding: 0;
}
.prize-list li {
    padding: 5px 0;
    border-bottom: 1px dotted #eee;
    font-size: 0.85rem;
    display: flex;
    justify-content: space-between;
}
.prize-list .prize-win { color: #2e7d32; font-weight: 600; }
.prize-list .time { color: #999; font-size: 0.75rem; }
.empty-state { text-align: center; color: #999; padding: 5px 0; }
</style>