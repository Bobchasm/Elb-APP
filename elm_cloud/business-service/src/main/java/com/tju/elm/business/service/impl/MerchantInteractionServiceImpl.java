package com.tju.elm.business.service.impl;

import com.tju.elm.api.client.OrderClient;
import com.tju.elm.api.client.PointClient;
import com.tju.elm.api.client.UserClient;
import com.tju.elm.business.mapper.BusinessMapper;
import com.tju.elm.business.mapper.MerchantInteractionMapper;
import com.tju.elm.business.pojo.dto.MerchantInteractionDTO;
import com.tju.elm.business.pojo.entity.MerchantInteraction;
import com.tju.elm.business.pojo.vo.BusinessSearchVO;
import com.tju.elm.business.pojo.vo.BusinessVO;
import com.tju.elm.business.pojo.vo.MerchantInteractionVO;
import com.tju.elm.business.service.MerchantInteractionService;
import com.tju.elm.business.pojo.vo.MerchantStatsVO;
import exception.APIException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import result.ResultCodeEnum;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MerchantInteractionServiceImpl implements MerchantInteractionService {


    @Autowired
    private MerchantInteractionMapper interactionMapper;
    @Autowired
    private BusinessMapper businessMapper;

    @Autowired
    private PointClient pointClient;
    @Autowired
    private UserClient userClient;
    @Autowired
    private OrderClient orderClient;


    @Override
    @Transactional
    public void updateInteraction(MerchantInteractionDTO dto) {
        log.info("========== 开始更新用户商家互动状态 ==========");
        log.info("接收到的参数: userId={}, merchantId={}, liked={}, collected={}",
            dto.getUserId(), dto.getMerchantId(), dto.getLiked(), dto.getCollected());

        try {
            // 参数验证
            if (dto.getUserId() == null) {
                log.error("参数验证失败: userId 为空");
                throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
            }
            if (dto.getMerchantId() == null) {
                log.error("参数验证失败: merchantId 为空");
                throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
            }

            // 查询现有记录
            MerchantInteraction interaction = interactionMapper.selectByUserAndMerchant(
                    dto.getUserId(), dto.getMerchantId());
            log.info("查询到的互动记录: {}", interaction != null ? "存在" : "不存在");

            // 记录旧状态（用于判断是否是首次点赞/收藏）
            Boolean oldLiked = null;
            Boolean oldCollected = null;

            if (interaction == null) {
                // 创建新记录
                log.info("创建新的互动记录");
                interaction = new MerchantInteraction();
                interaction.setUserId(dto.getUserId());
                interaction.setMerchantId(dto.getMerchantId());
                interaction.setLiked(dto.getLiked() != null ? dto.getLiked() : false);
                interaction.setCollected(dto.getCollected() != null ? dto.getCollected() : false);
                interactionMapper.insert(interaction);

                // 新记录，旧状态都是 false
                oldLiked = false;
                oldCollected = false;
                log.info("新记录创建完成: liked={}, collected={}", interaction.getLiked(), interaction.getCollected());
            } else {
                // 更新现有记录，记录旧状态
                oldLiked = interaction.getLiked();
                oldCollected = interaction.getCollected();
                log.info("更新现有记录，旧状态: oldLiked={}, oldCollected={}", oldLiked, oldCollected);

                if (dto.getLiked() != null) {
                    interaction.setLiked(dto.getLiked());
                }
                if (dto.getCollected() != null) {
                    interaction.setCollected(dto.getCollected());
                }
                interactionMapper.update(interaction);
                log.info("记录更新完成，新状态: liked={}, collected={}", interaction.getLiked(), interaction.getCollected());
            }

            // 行为积分奖励：只有当从 false 变为 true 时才奖励积分（首次点赞/收藏）
            log.info("开始判断是否需要奖励行为积分: oldLiked={}, newLiked={}, oldCollected={}, newCollected={}",
                oldLiked, dto.getLiked(), oldCollected, dto.getCollected());

            try {
                // 判断是否是首次点赞（从 false 变为 true）
                boolean shouldRewardLike = dto.getLiked() != null && dto.getLiked() &&
                    (oldLiked == null || !oldLiked);
                log.info("点赞积分判断: shouldRewardLike={}, dto.getLiked()={}, oldLiked={}",
                    shouldRewardLike, dto.getLiked(), oldLiked);

                if (shouldRewardLike) {
                    log.info("检测到用户{}首次点赞商家{}，准备奖励行为积分", dto.getUserId(), dto.getMerchantId());
                    Long points = pointClient.countPointsByType("like").getData();
                    if (points > 0) {
                        log.info("用户{}首次点赞商家{}，成功获得{}积分", dto.getUserId(), dto.getMerchantId(), points);
                    } else {
                        log.warn("用户{}首次点赞商家{}，但未获得积分（可能未配置规则）", dto.getUserId(), dto.getMerchantId());
                    }
                } else {
                    log.info("用户{}点赞商家{}，但非首次点赞（oldLiked={}, newLiked={}），不奖励积分",
                        dto.getUserId(), dto.getMerchantId(), oldLiked, dto.getLiked());
                }

                // 判断是否是首次收藏（从 false 变为 true）
                boolean shouldRewardCollect = dto.getCollected() != null && dto.getCollected() &&
                    (oldCollected == null || !oldCollected);
                log.info("收藏积分判断: shouldRewardCollect={}, dto.getCollected()={}, oldCollected={}",
                    shouldRewardCollect, dto.getCollected(), oldCollected);

                if (shouldRewardCollect) {
                    log.info("检测到用户{}首次收藏商家{}，准备奖励行为积分", dto.getUserId(), dto.getMerchantId());
                    Long points = pointClient.countPointsByType( "collect").getData();
                    if (points > 0) {
                        log.info("用户{}首次收藏商家{}，成功获得{}积分", dto.getUserId(), dto.getMerchantId(), points);
                    } else {
                        log.warn("用户{}首次收藏商家{}，但未获得积分（可能未配置规则）", dto.getUserId(), dto.getMerchantId());
                    }
                } else {
                    log.info("用户{}收藏商家{}，但非首次收藏（oldCollected={}, newCollected={}），不奖励积分",
                        dto.getUserId(), dto.getMerchantId(), oldCollected, dto.getCollected());
                }
            } catch (Exception e) {
                // 积分处理失败不影响互动状态更新，但记录详细错误日志
                log.error("行为积分处理失败: userId={}, merchantId={}, liked={}, collected={}, oldLiked={}, oldCollected={}, error={}",
                    dto.getUserId(), dto.getMerchantId(), dto.getLiked(), dto.getCollected(),
                    oldLiked, oldCollected, e.getMessage(), e);
                e.printStackTrace(); // 打印完整堆栈信息
            }

            log.info("========== 用户{}对商家{}的互动状态更新成功 ==========", dto.getUserId(), dto.getMerchantId());

        } catch (APIException e) {
            log.error("业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("更新用户商家互动失败: userId={}, merchantId={}, error={}",
                dto.getUserId(), dto.getMerchantId(), e.getMessage(), e);
            e.printStackTrace(); // 打印完整堆栈信息
            throw new APIException(ResultCodeEnum.SERVER_ERROR);
        }
    }

    private BigDecimal calculateRating(Map<String, Object> interactionCounts) {
        try {
            int likeCount = getCount(interactionCounts.get("likeCount"));
            int collectCount = getCount(interactionCounts.get("collectCount"));

            double normalizedRating = 1 + 4 * (0.6 * likeCount / (likeCount + 10.0) + 0.4 * collectCount / (collectCount + 10.0));
            return BigDecimal.valueOf(normalizedRating).setScale(2, RoundingMode.HALF_UP);
        } catch (Exception e) {
            log.error("计算评分失败", e);
            return BigDecimal.ZERO;
        }
    }

    private int getCount(Object countObj) {
        if (countObj instanceof BigDecimal) {
            return ((BigDecimal) countObj).intValue();
        } else if (countObj instanceof Long) {
            return ((Long) countObj).intValue();
        } else if (countObj instanceof Integer) {
            return (Integer) countObj;
        }
        return 0;
    }


    @Override
    public List<BusinessSearchVO> getUserCollections(Long userId) {
        //先写一个通过用户id查询商铺id列表的方法
        List< Long> businessIds = interactionMapper.selectUserCollectionIds(userId);
        //通过商家id查询商铺信息 —— 只没有评分和销售量
        List<BusinessSearchVO> businessSearchVOS=new ArrayList<>();
        for(Long businessId:businessIds){
            BusinessVO businessVO =businessMapper.getBusinessById(businessId);
            BusinessSearchVO businessSearchVO=new BusinessSearchVO();
              //计算每一个商铺的评分和销售量

            int salesCount = orderClient.orderCount(businessId).getData();

            Integer likeCount = interactionMapper.countLikesByMerchantId(businessId);
            Integer collectCount = interactionMapper.countCollectionsByMerchantId(businessId);


            // 计算评分 (点赞权重0.6，收藏权重0.4，归一化到1-5分)
            double normalizedRating = 1 + 4 * (0.6 * likeCount / (likeCount + 10.0) + 0.4 * collectCount / (collectCount + 10.0));
            BigDecimal rating = BigDecimal.valueOf(normalizedRating).setScale(2, RoundingMode.HALF_UP);
            businessSearchVO.setScore(rating);
            businessSearchVO.setSalesCount(salesCount);
            businessSearchVOS.add(new BusinessSearchVO(businessVO.getId(),businessVO.getBusinessName(),businessVO.getBusinessImg(),businessVO.getStartPrice(),businessVO.getDeliveryPrice(),businessSearchVO.getScore(),businessSearchVO.getSalesCount()));


        }
        return businessSearchVOS;

    }

    private boolean isUserExists(Long userId) {
        Integer count = userClient.hasUser(userId).getData();
        return count != null && count > 0; }

    @Override
    public MerchantStatsVO getMerchantStats(Long merchantId) {
        try {
            if (merchantId == null) {
                throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
            }

            // 获取点赞数和收藏数
            Integer likeCount = interactionMapper.countLikesByMerchantId(merchantId);
            Integer collectCount = interactionMapper.countCollectionsByMerchantId(merchantId);
            String merchantName = interactionMapper.selectMerNameById(merchantId);
            // 计算评分 (点赞权重0.6，收藏权重0.4，归一化到1-5分)
            double normalizedRating = 1 + 4 * (0.6 * likeCount / (likeCount + 10.0) + 0.4 * collectCount / (collectCount + 10.0));
            BigDecimal rating = BigDecimal.valueOf(normalizedRating).setScale(2, RoundingMode.HALF_UP);

            MerchantStatsVO stats = new MerchantStatsVO();
            stats.setMerchantId(merchantId);
            stats.setMerchantName(merchantName);
            stats.setLikeCount(likeCount);
            stats.setCollectCount(collectCount);
            stats.setRating(rating);

            log.info("获取商家{}的统计信息成功: 点赞数={}, 收藏数={}, 评分={}",
                    merchantId, likeCount, collectCount, rating);
            return stats;

        } catch (APIException e) {
            log.warn("业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取商家统计信息失败: merchantId={}", merchantId, e);
            throw new APIException(ResultCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public List<MerchantStatsVO>getMerchantStatsByUserId(Long userId){
        List<Long> businessIds =businessMapper.getBusinessIdsByUserIds(userId);
        List<MerchantStatsVO> merchantStatsVOS=new ArrayList<>();
        for(Long businessId:businessIds){
            MerchantStatsVO merchantStatsVO=getMerchantStats(businessId);
            merchantStatsVOS.add(merchantStatsVO);
//            log.info("获取商铺{}的统计信息成功: 点赞数={}, 收藏数={}, 评分={}", businessId, merchantStatsVO.getLikeCount(), merchantStatsVO.getCollectCount(), merchantStatsVO.getRating());

        }
        return merchantStatsVOS;
    }

    @Override
    public MerchantInteractionVO getUserMerchantInteraction(Long userId, Long merchantId) {
        try {
            if (userId == null) {
                throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
            }
            if (merchantId == null) {
                throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
            }

            MerchantInteraction interaction = interactionMapper.selectByUserAndMerchant(userId, merchantId);

            MerchantInteractionVO vo = new MerchantInteractionVO();
            vo.setMerchantId(merchantId);
            // 如果存在互动记录，设置点赞和收藏状态
            if (interaction != null) {
                vo.setLiked(interaction.getLiked());
                vo.setCollected(interaction.getCollected());
            } else {
                // 如果不存在记录，默认都为false
                vo.setLiked(false);
                vo.setCollected(false);
            }

            log.info("获取用户{}对商家{}的互动状态成功", userId, merchantId);
            return vo;

        } catch (APIException e) {
            log.warn("业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取用户商家互动状态失败: userId={}, merchantId={}", userId, merchantId, e);
            throw new APIException(ResultCodeEnum.SERVER_ERROR);
        }
    }
}