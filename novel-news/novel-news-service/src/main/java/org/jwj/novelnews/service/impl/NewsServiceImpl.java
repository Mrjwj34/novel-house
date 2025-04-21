package org.jwj.novelnews.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jwj.novelcommon.constants.DatabaseConsts;
import org.jwj.novelcommon.resp.RestResp;
import org.jwj.novelnews.dao.entity.NewsContent;
import org.jwj.novelnews.dao.entity.NewsInfo;
import org.jwj.novelnews.dao.mapper.NewsContentMapper;
import org.jwj.novelnews.dao.mapper.NewsInfoMapper;
import org.jwj.novelnews.dto.resp.NewsInfoRespDto;
import org.jwj.novelnews.manager.cache.NewsCacheManager;
import org.jwj.novelnews.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 新闻模块 服务实现类
 *
 * @author xiongxiaoyang
 * @date 2022/5/14
 */
@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsCacheManager newsCacheManager;

    private final NewsInfoMapper newsInfoMapper;

    private final NewsContentMapper newsContentMapper;

    @Override
    public RestResp<List<NewsInfoRespDto>> listLatestNews() {
        return RestResp.ok(newsCacheManager.listLatestNews());
    }

    @Override
    public RestResp<NewsInfoRespDto> getNews(Long id) {
        NewsInfo newsInfo = newsInfoMapper.selectById(id);
        QueryWrapper<NewsContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(DatabaseConsts.NewsContentTable.COLUMN_NEWS_ID, id)
            .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        NewsContent newsContent = newsContentMapper.selectOne(queryWrapper);
        return RestResp.ok(NewsInfoRespDto.builder()
            .title(newsInfo.getTitle())
            .sourceName(newsInfo.getSourceName())
            .updateTime(newsInfo.getUpdateTime())
            .content(newsContent.getContent())
            .build());
    }
}
