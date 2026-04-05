package com.tju.elm.business.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.tju.elm.api.client.OrderClient;
import com.tju.elm.business.mapper.BusinessMapper;
import com.tju.elm.business.mapper.MerchantInteractionMapper;
import com.tju.elm.business.pojo.vo.BusinessSearchVO;
import com.tju.elm.business.pojo.vo.MerchantStatsVO;
import com.tju.elm.business.service.MerchantInteractionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessEsService {

    private final ElasticsearchClient esClient;
    private final MerchantInteractionService interactionService;
    private final OrderClient orderClient;
    private final BusinessMapper businessMapper;
    private final MerchantInteractionMapper interactionMapper;

    private static final String INDEX_NAME = "business_index";


    public List<BusinessSearchVO> searchBusinesses(String keyword, boolean isScore, boolean isSales) {
        try {
            // 构建es查询
            SearchResponse<BusinessDocument> response = esClient.search(s -> {
                s.index(INDEX_NAME);

                if (keyword != null && !keyword.isEmpty()) {
                    s.query(q -> q
                            .bool(b -> b
                                    .should(sh -> sh.match(m -> m
                                            .field("businessName")
                                            .query(keyword)
                                            .boost(3.0F)  // 商铺名权重最高
                                    ))
                                    .should(sh -> sh.match(m -> m
                                            .field("businessExplain")
                                            .query(keyword)
                                            .boost(2.0F)  // 商铺描述权重中等
                                    ))
                                    .should(sh -> sh.match(m -> m
                                            .field("businessAddress")
                                            .query(keyword)
                                            .boost(1.0F)  // 地址权重较低
                                    ))
                                    .minimumShouldMatch("1")
                            )
                    );
                } else {
                    // 没有关键词时查询所有
                    s.query(q -> q.matchAll(m -> m));
                }

                // 排序（ES排序需要在Java代码中完成，因为评分和销量是动态的）
                // 这里只做相关性排序
                if (keyword != null && !keyword.isEmpty()) {
                    s.sort(sort -> sort.score(score -> score.order(co.elastic.clients.elasticsearch._types.SortOrder.Desc)));
                }

                s.size(100);  // 最多返回 100 条

                return s;
            }, BusinessDocument.class);

            // 处理搜索结果
            List<BusinessDocument> businesses = new ArrayList<>();
            for (Hit<BusinessDocument> hit : response.hits().hits()) {
                BusinessDocument doc = hit.source();
                if (doc != null) {
                    businesses.add(doc);
                }
            }

            // 转换为VO并计算评分和销量
            List<BusinessSearchVO> result = convertToSearchVO(businesses);

            // 排序
            if (isScore || isSales) {
                sortBusinesses(result, isScore, isSales);
            }

            return result;

        } catch (Exception e) {
            // 降级使用数据库查询
            return fallbackSearch(keyword, isScore, isSales);
        }
    }

    /**
     * es查询结果转换为SearchVO并计算评分和销量
     */
    private List<BusinessSearchVO> convertToSearchVO(List<BusinessDocument> businesses) {
        List<BusinessSearchVO> result = new ArrayList<>();

        for (BusinessDocument business : businesses) {
            BusinessSearchVO vo = new BusinessSearchVO();
            vo.setId(business.getId());
            vo.setBusinessName(business.getBusinessName());
            vo.setBusinessImg(business.getBusinessImg());
            vo.setStartPrice(business.getStartPrice());
            vo.setDeliveryPrice(business.getDeliveryPrice());

            MerchantStatsVO merchantStatsVO = interactionService.getMerchantStats(business.getId());

            // 获取评分
            Integer likeCount = merchantStatsVO.getLikeCount();
            Integer collectCount = merchantStatsVO.getCollectCount();
            double normalizedRating = 1 + 4 * (0.6 * likeCount / (likeCount + 10.0) + 0.4 * collectCount / (collectCount + 10.0));
            vo.setScore(BigDecimal.valueOf(normalizedRating).setScale(2, RoundingMode.HALF_UP));

            // 获取销量
            Integer salesCount = orderClient.orderCount(business.getId()).getData();
            vo.setSalesCount(salesCount);

            result.add(vo);
        }

        return result;
    }

    /**
     * 排序商铺
     */
    private void sortBusinesses(List<BusinessSearchVO> businesses, boolean isScore, boolean isSales) {
        Comparator<BusinessSearchVO> comparator = null;

        if (isScore && isSales) {
            comparator = Comparator.comparing(BusinessSearchVO::getScore, Comparator.reverseOrder())
                    .thenComparing(BusinessSearchVO::getSalesCount, Comparator.reverseOrder());
        } else if (isScore) {
            comparator = Comparator.comparing(BusinessSearchVO::getScore, Comparator.reverseOrder());
        } else if (isSales) {
            comparator = Comparator.comparing(BusinessSearchVO::getSalesCount, Comparator.reverseOrder());
        }

        if (comparator != null) {
            businesses.sort(comparator);
        }
    }

    /**
     * 降级查询（使用数据库）
     */
    private List<BusinessSearchVO> fallbackSearch(String keyword, boolean isScore, boolean isSales) {
        List<BusinessSearchVO> businesses = businessMapper.searchBusinesses(keyword);
//        System.out.println(businesses);
        // 为每个店铺计算评分与销量
        for (BusinessSearchVO business : businesses) {
            Map<String, Object> interactionCounts = businessMapper.getInteractionCounts(business.getId());
            int salesCount = orderClient.orderCount(business.getId()).getData();
            Integer likeCount = interactionMapper.countLikesByMerchantId(business.getId());
            Integer collectCount = interactionMapper.countCollectionsByMerchantId(business.getId());
            // 计算评分 (点赞权重0.6，收藏权重0.4，归一化到1-5分)
            double normalizedRating = 1 + 4 * (0.6 * likeCount / (likeCount + 10.0) + 0.4 * collectCount / (collectCount + 10.0));
            BigDecimal rating = BigDecimal.valueOf(normalizedRating).setScale(2, RoundingMode.HALF_UP);
            business.setScore(rating);
//            System.out.println("Business ID: " + business.getId() +
//                    ", likeCount: " + likeCount +
//                    ", collectCount: " + collectCount +
//                    ", rawRating: " + normalizedRating);
            business.setSalesCount(salesCount);
        }

        // 使用 Comparator 进行排序
        Comparator<BusinessSearchVO> comparator = null;

        if (isScore && isSales) {
            // 先按评分降序，再按销量降序
            comparator = Comparator.comparing(BusinessSearchVO::getScore, Comparator.reverseOrder())
                    .thenComparing(BusinessSearchVO::getSalesCount, Comparator.reverseOrder());
        } else if (isScore) {
            // 按评分降序
            comparator = Comparator.comparing(BusinessSearchVO::getScore, Comparator.reverseOrder());
        } else if (isSales) {
            // 按销量降序
            comparator = Comparator.comparing(BusinessSearchVO::getSalesCount, Comparator.reverseOrder());
        }

        if (comparator != null) {
            businesses.sort(comparator);
        }
        return businesses;
    }
}