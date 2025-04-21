package org.jwj.novel.home.service;

import org.jwj.novel.home.dto.resp.HomeBookRespDto;
import org.jwj.novel.home.dto.resp.HomeFriendLinkRespDto;
import org.jwj.novelcommon.resp.RestResp;

import java.util.List;

/**
 * 首页模块 服务类
 *
 * @author xiongxiaoyang
 * @date 2022/5/13
 */
public interface HomeService {

    /**
     * 查询首页小说推荐列表
     *
     * @return 首页小说推荐列表的 rest 响应结果
     */
    RestResp<List<HomeBookRespDto>> listHomeBooks();

    /**
     * 首页友情链接列表查询
     *
     * @return 友情链接列表
     */
    RestResp<List<HomeFriendLinkRespDto>> listHomeFriendLinks();
}
