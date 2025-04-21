package org.jwj.novelauthor.manager.feign;

import org.jwj.novelauthor.dto.AuthorInfoDto;
import org.jwj.novelauthor.manager.cache.AuthorInfoCacheManager;
import lombok.AllArgsConstructor;
import org.jwj.novelbookapi.dto.req.BookAddReqDto;
import org.jwj.novelbookapi.dto.req.BookPageReqDto;
import org.jwj.novelbookapi.dto.req.ChapterAddReqDto;
import org.jwj.novelbookapi.dto.req.ChapterPageReqDto;
import org.jwj.novelbookapi.dto.resp.BookChapterRespDto;
import org.jwj.novelbookapi.dto.resp.BookInfoRespDto;
import org.jwj.novelbookapi.feign.BookFeign;
import org.jwj.novelcommon.auth.UserHolder;
import org.jwj.novelcommon.resp.PageRespDto;
import org.jwj.novelcommon.resp.RestResp;
import org.springframework.stereotype.Component;

/**
 * 小说微服务调用 Feign 客户端管理
 *
 * @author xiongxiaoyang
 * @date 2023/3/29
 */
@Component
@AllArgsConstructor
public class BookFeignManager {

    private final BookFeign bookFeign;

    private final AuthorInfoCacheManager authorInfoCacheManager;

    public RestResp<Void> publishBook(BookAddReqDto dto) {
        AuthorInfoDto author = authorInfoCacheManager.getAuthor(UserHolder.getUserId());
        dto.setAuthorId(author.getId());
        dto.setPenName(author.getPenName());
        return bookFeign.publishBook(dto);
    }

    public RestResp<PageRespDto<BookInfoRespDto>> listPublishBooks(BookPageReqDto dto) {
        authorInfoCacheManager.getAuthor(UserHolder.getUserId());
        return bookFeign.listPublishBooks(dto);
    }

    public RestResp<Void> publishBookChapter(ChapterAddReqDto dto) {
        return bookFeign.publishBookChapter(dto);
    }

    public RestResp<PageRespDto<BookChapterRespDto>> listPublishBookChapters(ChapterPageReqDto dto) {
        return bookFeign.listPublishBookChapters(dto);
    }


}
