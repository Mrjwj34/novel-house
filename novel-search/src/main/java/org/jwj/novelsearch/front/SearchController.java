package org.jwj.novelsearch.front;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jwj.novelbookapi.dto.req.BookSearchReqDto;
import org.jwj.novelbookapi.dto.resp.BookInfoRespDto;
import org.jwj.novelcommon.constants.ApiRouterConsts;
import org.jwj.novelcommon.resp.PageRespDto;
import org.jwj.novelcommon.resp.RestResp;
import org.jwj.novelsearch.service.SearchService;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * 前台门户-搜索模块 API 控制器
 *
 * @author xiongxiaoyang
 * @date 2022/5/27
 */
@Tag(name = "SearchController", description = "前台门户-搜索模块")
@RestController
@RequestMapping(ApiRouterConsts.API_FRONT_SEARCH_URL_PREFIX)
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * 小说搜索接口
     */
    @Operation(summary = "小说搜索接口")
    @GetMapping("books")
    public RestResp<PageRespDto<BookInfoRespDto>> searchBooks(
        @ParameterObject BookSearchReqDto condition) throws IOException {
        return searchService.searchBooks(condition);
    }

}
