package com.tju.elm.business.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import co.elastic.clients.elasticsearch.core.DeleteResponse;
import com.tju.elm.business.mapper.BusinessMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessDataSyncService {

    private final ElasticsearchClient esClient;
    private final BusinessMapper businessMapper;

    private static final String INDEX_NAME = "business_index";

    /**
     * 同步所有商铺数据到 ES
     */
    public void syncAllData() {
        try {
            // 从数据库获取所有上线的商铺
            List<BusinessDocument> businesses = businessMapper.getAllOnlineBusinessesForEs();

            if (businesses == null || businesses.isEmpty()) {
                log.warn("没有需要同步的商铺数据");
                return;
            }

            // 批量插入/更新
            var bulkRequest = new co.elastic.clients.elasticsearch.core.BulkRequest.Builder();
            for (BusinessDocument business : businesses) {
                bulkRequest.operations(op -> op
                        .index(idx -> idx
                                .index(INDEX_NAME)
                                .id(String.valueOf(business.getId()))
                                .document(business)
                        )
                );
            }

            BulkResponse response = esClient.bulk(bulkRequest.build());

            if (response.errors()) {
                log.error("ES 批量同步失败");
            } else {
                log.info("ES 批量同步成功，共 {} 条数据", businesses.size());
            }
        } catch (Exception e) {
            log.error("同步数据到 ES 失败", e);
        }
    }

    /**
     * 同步单条数据（新增或更新时调用）
     */
    public void syncSingleData(Long businessId) {
        try {
            BusinessDocument business = businessMapper.getBusinessByIdForEs(businessId);
            if (business != null) {
                esClient.index(i -> i
                        .index(INDEX_NAME)
                        .id(String.valueOf(businessId))
                        .document(business)
                );
                log.info("同步单条数据到 ES 成功: {}", businessId);
            }
        } catch (Exception e) {
            log.error("同步单条数据到 ES 失败: {}", businessId, e);
        }
    }

    /**
     * 从 ES 删除单条数据（删除时调用）
     */
    public void deleteFromEs(Long businessId) {
        try {
            DeleteResponse response = esClient.delete(d -> d
                    .index(INDEX_NAME)
                    .id(String.valueOf(businessId))
            );

            if (response.result().name().equals("Deleted")) {
                log.info("从 ES 删除成功: {}", businessId);
            } else if (response.result().name().equals("NotFound")) {
                log.debug("ES 中不存在该数据，无需删除: {}", businessId);
            } else {
                log.warn("从 ES 删除结果: {} - {}", businessId, response.result());
            }
        } catch (Exception e) {
            log.error("从 ES 删除失败: {}", businessId, e);
        }
    }

}