package com.tju.elm.point.service.impl;

import com.tju.elm.point.mapper.*;
import com.tju.elm.point.service.PointsLotteryService;
import com.tju.elm.point.service.PointsService;
import com.tju.elm.point.zoo.pojo.dto.PointsAddDTO;
import com.tju.elm.point.zoo.pojo.entity.PointsAccount;
import com.tju.elm.point.zoo.pojo.entity.PointsLotteryRecord;
import com.tju.elm.point.zoo.pojo.entity.PointsLotteryRule;
import com.tju.elm.point.zoo.pojo.vo.LotteryPrizeVO;
import com.tju.elm.point.zoo.pojo.vo.PointsLotteryInfoVO;
import com.tju.elm.point.zoo.pojo.vo.PointsLotteryRecordVO;
import com.tju.elm.point.zoo.pojo.vo.PointsLotteryResultVO;
import exception.APIException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import result.ResultCodeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 积分抽奖服务实现类
 */
@Slf4j
@Service
public class PointsLotteryServiceImpl implements PointsLotteryService {

    @Autowired
    private PointsLotteryRecordMapper lotteryRecordMapper;

    @Autowired
    private PointsLotteryRuleMapper lotteryRuleMapper;

    @Autowired
    private PointsAccountMapper pointsAccountMapper;

    @Autowired
    private PointsService pointsService;

    @Autowired
    private PointsTransactionMapper pointsTransactionMapper;

    @Autowired
    private PointsExpirationMapper pointsExpirationMapper;

//    @Autowired
//    private UserMapper userMapper;

    private static final Random random = new Random();
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * 获取用户抽奖信息
     */
    @Override
    public PointsLotteryInfoVO getLotteryInfo(Long userId) {
        // 1. 查询用户积分账户，获取会员等级
        PointsAccount account = pointsAccountMapper.selectByUserId(userId);
        if (account == null) {
            throw new APIException(ResultCodeEnum.VALUE_MISSED);
        }

        Integer memberLevel = account.getMemberLevel() != null ? account.getMemberLevel() : 0;

        // 2. 获取每月抽奖次数限制
        Integer monthlyLimit = getMonthlyLimit(memberLevel);

        // 3. 统计本月已抽奖次数
        String currentMonth = LocalDateTime.now().format(MONTH_FORMATTER);
        Integer usedChances = lotteryRecordMapper.countByUserIdAndMonth(userId, currentMonth);

        // 4. 计算剩余次数
        Integer remainingChances = Math.max(0, monthlyLimit - usedChances);

        // 5. 获取奖池配置
        List<LotteryPrizeVO> prizes = getLotteryPrizes(memberLevel);

        // 6. 构建返回对象
        PointsLotteryInfoVO vo = new PointsLotteryInfoVO();
        vo.setMemberLevel(memberLevel);
        vo.setMemberLevelName(getMemberLevelName(memberLevel));
        vo.setMonthlyLimit(monthlyLimit);
        vo.setUsedChances(usedChances);
        vo.setRemainingChances(remainingChances);
        vo.setCanLottery(remainingChances > 0);
        vo.setPrizes(prizes);

        return vo;
    }

//    /**
//     * 执行抽奖
//     */
//    @Override
//    @Transactional
//    public PointsLotteryResultVO doLottery(Long userId) {
//        // 1. 检查抽奖资格
//        PointsLotteryInfoVO info = getLotteryInfo(userId);
//        if (!info.getCanLottery()) {
//            throw new APIException("LOTTERY_NO_CHANCE", "本月抽奖次数已用完");
//        }
//
//        // 2. 查询积分账户（带行锁）
//        PointsAccount account = pointsAccountMapper.selectForUpdate(userId);
//        if (account == null) {
//            throw new APIException(ResultCodeEnum.VALUE_MISSED);
//        }
//
//        Integer memberLevel = account.getMemberLevel() != null ? account.getMemberLevel() : 0;
//
//        // 3. 执行抽奖算法
//        LotteryResult lotteryResult = performLottery(memberLevel, account.getAvailablePoints());
//
//        // 4. 记录抽奖记录
//        String currentMonth = LocalDateTime.now().format(MONTH_FORMATTER);
//        PointsLotteryRecord record = new PointsLotteryRecord();
//        record.setUserId(userId);
//        record.setMemberLevel(memberLevel);
//        record.setLotteryType(lotteryResult.getLotteryType());
//        record.setPointsReward(lotteryResult.getPointsReward());
//        record.setPointsMultiplier(lotteryResult.getPointsMultiplier());
//        record.setOriginalPoints(lotteryResult.getOriginalPoints());
//        record.setLotteryMonth(currentMonth);
//        record.setTransactionId(null); // 先设为null，如果中奖再更新
//        record.setCreateTime(LocalDateTime.now());
//        Long currentUserId = getCurrentUserId();
//        record.setCreator(currentUserId);
//        record.setUpdater(currentUserId);
//        record.setIsDeleted(false);
//        record.setUpdateTime(LocalDateTime.now());
//        lotteryRecordMapper.insert(record);
//
//        // 5. 如果中奖，增加积分
//        Long transactionId = null;
//        if (lotteryResult.getPointsReward() != null && lotteryResult.getPointsReward() > 0) {
//            PointsAddDTO addDTO = new PointsAddDTO();
//            addDTO.setUserId(userId);
//            addDTO.setPoints(lotteryResult.getPointsReward());
//            addDTO.setPointsSource(3); // 3-行为积分（抽奖属于行为积分）
//            addDTO.setDescription(lotteryResult.getDescription());
//            // 积分有效期为15天
//            addDTO.setExpireTime(LocalDateTime.now().plusDays(15));
//            transactionId = pointsService.addPoints(addDTO);
//
//            // 更新抽奖记录的transactionId
//            lotteryRecordMapper.updateTransactionId(record.getId(), transactionId,
//                    LocalDateTime.now(), currentUserId);
//        }
//
//        // 6. 重新查询剩余次数
//        Integer usedChances = lotteryRecordMapper.countByUserIdAndMonth(userId, currentMonth);
//        Integer remainingChances = Math.max(0, getMonthlyLimit(memberLevel) - usedChances);
//
//        // 7. 构建返回结果
//        PointsLotteryResultVO resultVO = new PointsLotteryResultVO();
//        resultVO.setRecordId(record.getId());
//        resultVO.setLotteryType(lotteryResult.getLotteryType());
//        resultVO.setLotteryTypeName(getLotteryTypeName(lotteryResult.getLotteryType()));
//        resultVO.setPointsReward(lotteryResult.getPointsReward());
//        resultVO.setPointsMultiplier(lotteryResult.getPointsMultiplier());
//        resultVO.setOriginalPoints(lotteryResult.getOriginalPoints());
//        resultVO.setCreateTime(record.getCreateTime());
//        resultVO.setRemainingChances(remainingChances);
//        resultVO.setDescription(lotteryResult.getDescription());
//
//        log.info("用户 {} 抽奖结果：类型={}, 积分={}", userId, lotteryResult.getLotteryType(), lotteryResult.getPointsReward());
//
//        return resultVO;
//    }

    /**
     * 查询用户抽奖记录
     */
    @Override
    public List<PointsLotteryRecordVO> getLotteryRecords(Long userId, Integer limit) {
        if (limit == null || limit <= 0) {
            limit = 10; // 默认10条
        }

        List<PointsLotteryRecord> records = lotteryRecordMapper.selectRecentByUserId(userId, limit);
        List<PointsLotteryRecordVO> voList = new ArrayList<>();

        for (PointsLotteryRecord record : records) {
            PointsLotteryRecordVO vo = new PointsLotteryRecordVO();
            BeanUtils.copyProperties(record, vo);
            vo.setMemberLevelName(getMemberLevelName(record.getMemberLevel()));
            vo.setLotteryTypeName(getLotteryTypeName(record.getLotteryType()));
            voList.add(vo);
        }

        return voList;
    }

    /**
     * 执行抽奖算法
     * @param memberLevel 会员等级
     * @param currentPoints 当前可用积分（用于积分翻倍计算）
     * @return 抽奖结果
     */
    private LotteryResult performLottery(Integer memberLevel, Long currentPoints) {
        // 根据会员等级确定奖池和概率
        LotteryPool pool = getLotteryPool(memberLevel);

        // 生成随机数（0-100）
        int randomValue = random.nextInt(100);

        // 根据概率确定中奖类型
        int cumulativeProbability = 0;
        for (LotteryPrize prize : pool.getPrizes()) {
            cumulativeProbability += prize.getProbability();
            if (randomValue < cumulativeProbability) {
                return processPrize(prize, currentPoints);
            }
        }

        // 默认返回没中奖
        return new LotteryResult(0, null, null, null, "很遗憾，未中奖");
    }

    /**
     * 处理奖品（计算实际获得的积分）
     */
    private LotteryResult processPrize(LotteryPrize prize, Long currentPoints) {
        if (prize.getType() == 0) {
            // 没中奖
            return new LotteryResult(0, 0L, null, null, "很遗憾，未中奖");
        } else if (prize.getType() == 1) {
            // 固定积分
            return new LotteryResult(1, prize.getPoints(), null, null, 
                    String.format("恭喜中奖！获得 %d 积分", prize.getPoints()));
        } else if (prize.getType() == 2) {
            // 积分翻倍
            if (currentPoints == null || currentPoints <= 0) {
                // 如果没有积分，则给固定积分
                return new LotteryResult(1, 100L, null, null, "恭喜中奖！由于您暂无积分，积分翻倍改为奖励 100 积分");
            }
            Long rewardPoints = currentPoints; // 翻倍就是当前积分数量
            return new LotteryResult(2, rewardPoints, BigDecimal.valueOf(2.0), currentPoints,
                    String.format("恭喜中奖！积分翻倍，获得 %d 积分", rewardPoints));
        }

        return new LotteryResult(0, 0L, null, null, "很遗憾，未中奖");
    }

    /**
     * 根据会员等级获取奖池（从数据库读取规则）
     */
    private LotteryPool getLotteryPool(Integer memberLevel) {
        LotteryPool pool = new LotteryPool();
        
        // 普通用户没有抽奖机会
        if (memberLevel == null || memberLevel < 1 || memberLevel > 3) {
            pool.addPrize(new LotteryPrize(0, 0L, 100)); // 100%没中奖
            return pool;
        }
        
        // 从数据库查询规则
        List<PointsLotteryRule> rules = lotteryRuleMapper.selectByMemberLevel(memberLevel);
        
        if (rules == null || rules.isEmpty()) {
            // 如果没有配置规则，返回默认值
            log.warn("会员等级 {} 没有配置抽奖规则，使用默认规则", memberLevel);
            pool.addPrize(new LotteryPrize(0, 0L, 100)); // 100%没中奖
            return pool;
        }
        
        // 将数据库规则转换为内部奖品对象
        for (PointsLotteryRule rule : rules) {
            pool.addPrize(new LotteryPrize(
                rule.getPrizeType(),
                rule.getPrizePoints(),
                rule.getProbability()
            ));
        }

        return pool;
    }
    
    /**
     * 获取会员等级对应的奖池配置（供前端展示）
     */
    private List<LotteryPrizeVO> getLotteryPrizes(Integer memberLevel) {
        List<LotteryPrizeVO> prizes = new ArrayList<>();
        
        // 普通用户没有抽奖机会
        if (memberLevel == null || memberLevel < 1 || memberLevel > 3) {
            LotteryPrizeVO vo = new LotteryPrizeVO();
            vo.setType(0);
            vo.setTypeName("没中奖");
            vo.setPoints(0L);
            vo.setProbability(100);
            vo.setDescription("普通用户无抽奖机会");
            prizes.add(vo);
            return prizes;
        }
        
        // 从数据库查询规则
        List<PointsLotteryRule> rules = lotteryRuleMapper.selectByMemberLevel(memberLevel);
        
        if (rules == null || rules.isEmpty()) {
            log.warn("会员等级 {} 没有配置抽奖规则", memberLevel);
            return prizes;
        }
        
        // 转换为VO
        for (PointsLotteryRule rule : rules) {
            LotteryPrizeVO vo = new LotteryPrizeVO();
            vo.setType(rule.getPrizeType());
            vo.setTypeName(getPrizeTypeName(rule.getPrizeType()));
            vo.setPoints(rule.getPrizePoints());
            vo.setMultiplier(rule.getPrizeMultiplier());
            vo.setProbability(rule.getProbability());
            vo.setDescription(rule.getPrizeDescription());
            prizes.add(vo);
        }
        
        return prizes;
    }

    /**
     * 获取每月抽奖次数限制
     */
    private Integer getMonthlyLimit(Integer memberLevel) {
        switch (memberLevel) {
            case 1: return 1; // 白银会员每月1次
            case 2: return 2; // 黄金会员每月2次
            case 3: return 3; // 钻石会员每月3次
            default: return 0; // 普通用户没有抽奖机会
        }
    }

    /**
     * 获取会员等级名称
     */
    private String getMemberLevelName(Integer memberLevel) {
        if (memberLevel == null) {
            return "普通用户";
        }
        switch (memberLevel) {
            case 0: return "普通用户";
            case 1: return "白银会员";
            case 2: return "黄金会员";
            case 3: return "钻石会员";
            default: return "普通用户";
        }
    }

    /**
     * 获取抽奖类型名称
     */
    private String getLotteryTypeName(Integer lotteryType) {
        if (lotteryType == null) {
            return "未知";
        }
        switch (lotteryType) {
            case 0: return "没中奖";
            case 1: return "固定积分";
            case 2: return "积分翻倍";
            default: return "未知";
        }
    }
    
    /**
     * 获取奖品类型名称
     */
    private String getPrizeTypeName(Integer prizeType) {
        if (prizeType == null) {
            return "未知";
        }
        switch (prizeType) {
            case 0: return "没中奖";
            case 1: return "固定积分";
            case 2: return "积分翻倍";
            default: return "未知";
        }
    }

//    /**
//     * 获取当前用户ID
//     */
//    private Long getCurrentUserId() {
//        return userMapper.getUserIdByUsername(
//                com.tju.elm_bk.utils.SecurityUtils.getCurrentUsername().orElse(null));
//    }

    /**
     * 抽奖结果内部类
     */
    private static class LotteryResult {
        private Integer lotteryType;
        private Long pointsReward;
        private BigDecimal pointsMultiplier;
        private Long originalPoints;
        private String description;

        public LotteryResult(Integer lotteryType, Long pointsReward, BigDecimal pointsMultiplier,
                           Long originalPoints, String description) {
            this.lotteryType = lotteryType;
            this.pointsReward = pointsReward;
            this.pointsMultiplier = pointsMultiplier;
            this.originalPoints = originalPoints;
            this.description = description;
        }

        public Integer getLotteryType() { return lotteryType; }
        public Long getPointsReward() { return pointsReward; }
        public BigDecimal getPointsMultiplier() { return pointsMultiplier; }
        public Long getOriginalPoints() { return originalPoints; }
        public String getDescription() { return description; }
    }

    /**
     * 奖池内部类
     */
    private static class LotteryPool {
        private List<LotteryPrize> prizes = new ArrayList<>();

        public void addPrize(LotteryPrize prize) {
            prizes.add(prize);
        }

        public List<LotteryPrize> getPrizes() {
            return prizes;
        }
    }

    /**
     * 奖品内部类
     */
    private static class LotteryPrize {
        private Integer type; // 0-没中奖 1-固定积分 2-积分翻倍
        private Long points; // 固定积分数量
        private Integer probability; // 概率（0-100）

        public LotteryPrize(Integer type, Long points, Integer probability) {
            this.type = type;
            this.points = points;
            this.probability = probability;
        }

        public Integer getType() { return type; }
        public Long getPoints() { return points; }
        public Integer getProbability() { return probability; }
    }
}

