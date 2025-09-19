// MerchantInteractionServiceImpl.java
package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.entity.MerchantInteraction;
import com.tju.elm_bk.mapper.MerchantInteractionMapper;
import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.service.MerchantInteractionService;
import com.tju.elm_bk.vo.MerchantInteractionVO;
import com.tju.elm_bk.vo.MerchantStatsVO;
import com.tju.elm_bk.dto.MerchantInteractionDTO;
import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@Slf4j
public class MerchantInteractionServiceImpl implements MerchantInteractionService {

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MerchantInteractionMapper interactionMapper;

    @Override
    @Transactional
    public void updateInteraction(MerchantInteractionDTO dto) {
        try {
            // 参数验证
            if (dto.getUserId() == null) {
                throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
            }
            if (dto.getMerchantId() == null) {
                throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
            }

            // 查询现有记录
            MerchantInteraction interaction = interactionMapper.selectByUserAndMerchant(
                    dto.getUserId(), dto.getMerchantId());

            if (interaction == null) {
                // 创建新记录
                interaction = new MerchantInteraction();
                interaction.setUserId(dto.getUserId());
                interaction.setMerchantId(dto.getMerchantId());
                interaction.setLiked(dto.getLiked() != null ? dto.getLiked() : false);
                interaction.setCollected(dto.getCollected() != null ? dto.getCollected() : false);
                interactionMapper.insert(interaction);
            } else {
                // 更新现有记录
                if (dto.getLiked() != null) {
                    interaction.setLiked(dto.getLiked());
                }
                if (dto.getCollected() != null) {
                    interaction.setCollected(dto.getCollected());
                }
                interactionMapper.update(interaction);
            }

            log.info("用户{}对商家{}的互动状态更新成功", dto.getUserId(), dto.getMerchantId());

        } catch (APIException e) {
            log.warn("业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("更新用户商家互动失败: userId={}, merchantId={}", dto.getUserId(), dto.getMerchantId(), e);
            throw new APIException(ResultCodeEnum.SERVER_ERROR);
        }
    }

    @Override
    public List<MerchantInteractionVO> getUserCollections(Long userId) {
        try {
            if (userId == null) {
                throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
            }
            // 检查用户是否存在
            if (!isUserExists(userId)) {
                throw new APIException(ResultCodeEnum.NOT_FOUND);
            }

            List<MerchantInteractionVO> collections = interactionMapper.selectUserCollections(userId);
            log.info("获取用户{}的收藏列表成功，共{}条记录", userId, collections.size());
            return collections;

        } catch (APIException e) {
            log.warn("业务异常: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("获取用户收藏列表失败: userId={}", userId, e);
            throw new APIException(ResultCodeEnum.SERVER_ERROR);
        }
    }

    private boolean isUserExists(Long userId) {
        Integer count = userMapper.countUserById(userId);
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