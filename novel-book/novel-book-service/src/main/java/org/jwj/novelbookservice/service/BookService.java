package org.jwj.novelbookservice.service;

import org.jwj.novelbookapi.dto.req.*;
import org.jwj.novelbookapi.dto.resp.*;
import org.jwj.novelcommon.resp.PageRespDto;
import org.jwj.novelcommon.resp.RestResp;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface BookService {
    RestResp<List<BookCategoryRespDto>> listCategory(Integer workDirection);

    RestResp<BookInfoRespDto> getBookById(Long bookId);

    RestResp<Void> addVisitCount(Long bookId);

    RestResp<BookChapterAboutRespDto> getLastChapterAbout(Long bookId);

    RestResp<List<BookInfoRespDto>> listRecBooks(Long bookId) throws NoSuchAlgorithmException;

    RestResp<List<BookChapterRespDto>> listChapters(Long bookId);

    RestResp<BookContentAboutRespDto> getBookContentAbout(Long chapterId);

    RestResp<Long> getPreChapterId(Long chapterId);

    RestResp<Long> getNextChapterId(Long chapterId);

    RestResp<List<BookRankRespDto>> listVisitRankBooks();

    RestResp<List<BookRankRespDto>> listNewestRankBooks();

    RestResp<List<BookRankRespDto>> listUpdateRankBooks();

    RestResp<BookCommentRespDto> listNewestComments(Long bookId);

    RestResp<List<BookEsRespDto>> listNextEsBooks(Long maxBookId);

    RestResp<List<BookInfoRespDto>> listBookInfoByIds(List<Long> bookIds);

    RestResp<Void> saveComment(BookCommentReqDto dto);

    RestResp<Void> updateComment(BookCommentReqDto dto);

    RestResp<Void> deleteComment(BookCommentReqDto dto);

    RestResp<Void> saveBook(BookAddReqDto dto);

    RestResp<PageRespDto<BookInfoRespDto>> listAuthorBooks(BookPageReqDto dto);

    RestResp<Void> saveBookChapter(ChapterAddReqDto dto);

    RestResp<PageRespDto<BookChapterRespDto>> listBookChapters(ChapterPageReqDto dto);
}
