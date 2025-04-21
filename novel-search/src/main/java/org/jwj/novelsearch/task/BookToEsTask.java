package org.jwj.novelsearch.task;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jwj.novelsearch.manager.feign.BookFeignManager;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookToEsTask {
    private final BookFeignManager bookFeignManager;

    private final ElasticsearchClient elasticsearchClient;
    /**
     * 每月凌晨做一次全量数据同步
     */
    @SneakyThrows
    @XxlJob("saveToEsJobHandler")
    public ReturnT<String> saveToEs() {
    }
}
