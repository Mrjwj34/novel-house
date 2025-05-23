package org.jwj.novel.home.service.impl;

import org.jwj.novel.home.dto.resp.HomeBookRespDto;
import org.jwj.novel.home.dto.resp.HomeFriendLinkRespDto;
import org.jwj.novel.home.manager.cache.FriendLinkCacheManager;
import org.jwj.novel.home.manager.cache.HomeBookCacheManager;
import org.jwj.novel.home.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.jwj.novelcommon.resp.RestResp;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Optional;

/**
 * 首页模块 服务实现类
 *
 * @author xiongxiaoyang
 * @date 2022/5/13
 */
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private final HomeBookCacheManager homeBookCacheManager;

    private final FriendLinkCacheManager friendLinkCacheManager;

    @Override
    public RestResp<List<HomeBookRespDto>> listHomeBooks() {
        List<HomeBookRespDto> list = homeBookCacheManager.listHomeBooks();
        if(CollectionUtils.isEmpty(list)){
            homeBookCacheManager.evictCache();
        }
        return RestResp.ok(list);
    }

    @Override
    public RestResp<List<HomeFriendLinkRespDto>> listHomeFriendLinks() {
        return RestResp.ok(friendLinkCacheManager.listFriendLinks());
    }
}
