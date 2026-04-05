package com.tju.elm.business.es;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EsIndexInitializer {

    private final ElasticsearchClient esClient;
    private final BusinessDataSyncService businessDataSyncService;

    @PostConstruct
    public void initIndex() throws Exception {
        String indexName = "business_index";

        // 检查索引是否存在
        boolean exists = esClient.indices().exists(e -> e.index(indexName)).value();

        if (!exists) {
            // 创建索引和映射
            CreateIndexResponse response = esClient.indices().create(c -> c
                    .index(indexName)
                    .mappings(m -> m
                            .properties("id", p -> p.long_(lp -> lp))
                            .properties("businessName", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                            .properties("businessExplain", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                            .properties("businessAddress", p -> p.text(t -> t.analyzer("ik_max_word").searchAnalyzer("ik_smart")))
                            .properties("businessImg", p -> p.keyword(k -> k))
                            .properties("startPrice", p -> p.double_(d -> d))
                            .properties("deliveryPrice", p -> p.double_(d -> d))
                            .properties("status", p -> p.integer(i -> i))
                            .properties("isDeleted", p -> p.boolean_(b -> b))
                    )
            );
            log.info("创建 ES 索引: {}", response.index());

            // 同步现有数据到 ES
            businessDataSyncService.syncAllData();
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void syncData() {
        log.info("应用启动完成，同步数据到 ES");
        businessDataSyncService.syncAllData();
    }
}