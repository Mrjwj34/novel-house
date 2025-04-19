package org.jwj.novelbookservice.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jwj.novelbookapi.dto.req.*;
import org.jwj.novelbookapi.dto.resp.*;
import org.jwj.novelbookservice.dao.entity.BookChapter;
import org.jwj.novelbookservice.dao.entity.BookComment;
import org.jwj.novelbookservice.dao.entity.BookInfo;
import org.jwj.novelbookservice.dao.mapper.BookChapterMapper;
import org.jwj.novelbookservice.dao.mapper.BookCommentMapper;
import org.jwj.novelbookservice.dao.mapper.BookContentMapper;
import org.jwj.novelbookservice.dao.mapper.BookInfoMapper;
import org.jwj.novelbookservice.manager.amqp.AmqpMsgManager;
import org.jwj.novelbookservice.manager.cache.*;
import org.jwj.novelbookservice.manager.feign.UserFeignManager;
import org.jwj.novelbookservice.service.BookService;
import org.jwj.novelcommon.auth.UserHolder;
import org.jwj.novelcommon.constants.DatabaseConsts;
import org.jwj.novelcommon.resp.PageRespDto;
import org.jwj.novelcommon.resp.RestResp;
import org.jwj.novelconfig.annotation.Key;
import org.jwj.noveluserapi.dto.resp.UserInfoRespDto;
import org.springframework.stereotype.Service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.ZoneOffset;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookCategoryCacheManager bookCategoryCacheManager;

    private final BookRankCacheManager bookRankCacheManager;

    private final BookInfoCacheManager bookInfoCacheManager;

    private final BookChapterCacheManager bookChapterCacheManager;

    private final BookContentCacheManager bookContentCacheManager;

    private final BookInfoMapper bookInfoMapper;

    private final BookChapterMapper bookChapterMapper;

    private final BookContentMapper bookContentMapper;

    private final BookCommentMapper bookCommentMapper;

    private final AmqpMsgManager amqpMsgManager;

    private final UserFeignManager userFeignManager;

    private static final Integer REC_BOOK_COUNT = 4;
    @Override
    public RestResp<List<BookCategoryRespDto>> listCategory(Integer workDirection) {
        return RestResp.ok(bookCategoryCacheManager.listCategory(workDirection));
    }

    @Override
    public RestResp<BookInfoRespDto> getBookById(Long bookId) {
        return RestResp.ok(bookInfoCacheManager.getBookInfo(bookId));
    }

    @Override
    public RestResp<Void> addVisitCount(Long bookId) {
        bookInfoMapper.addVisitCount(bookId);
        return RestResp.ok();
    }

    @Override
    public RestResp<BookChapterAboutRespDto> getLastChapterAbout(Long bookId) {
        // 查询小说信息
        BookInfoRespDto bookInfo = bookInfoCacheManager.getBookInfo(bookId);
        // 查询最新章节信息
        BookChapterRespDto chapter = bookChapterCacheManager.getChapter(bookInfo.getLastChapterId());
        // 查询章节内容
        String bookContent = bookContentCacheManager.getBookContent(bookInfo.getLastChapterId());
        // 查询章节总数
        LambdaQueryWrapper<BookChapter> chapterQueryWrapper = new LambdaQueryWrapper<>();
        chapterQueryWrapper.eq(BookChapter::getBookId, bookId);
        Long chapterTotal = bookChapterMapper.selectCount(chapterQueryWrapper);
        // 组装数据并返回
        return RestResp.ok(BookChapterAboutRespDto.builder()
                .chapterInfo(chapter)
                .chapterTotal(chapterTotal)
                .contentSummary(bookContent.substring(0, 30))
                .build());
    }

    @Override
    public RestResp<List<BookInfoRespDto>> listRecBooks(Long bookId) throws NoSuchAlgorithmException {
        // 获取本书类别
        Long categoryId = bookInfoCacheManager.getBookInfo(bookId).getCategoryId();
        // 获取同类书籍
        List<Long> lastUpdateIdList = bookInfoCacheManager.getLastUpdateIdList(categoryId);
        // 随机生成四本推荐书籍
        List<BookInfoRespDto> respDtoList = new ArrayList<>();
        List<Integer> recIdIndexList = new ArrayList<>();
        // 返回推荐列表
        int count = 0;
        Random rand = SecureRandom.getInstanceStrong();
        while(count < REC_BOOK_COUNT) {
            int recIdIndex = rand.nextInt(lastUpdateIdList.size());
            if(!recIdIndexList.contains(recIdIndex)) {
                recIdIndexList.add(recIdIndex);
                BookInfoRespDto bookInfo = bookInfoCacheManager.getBookInfo(lastUpdateIdList.get(recIdIndex));
                respDtoList.add(bookInfo);
                count++;
            }
        }
        return RestResp.ok(respDtoList);
    }

    @Override
    public RestResp<List<BookChapterRespDto>> listChapters(Long bookId) {
        QueryWrapper<BookChapter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(DatabaseConsts.BookChapterTable.COLUMN_BOOK_ID, bookId)
                .orderByAsc(DatabaseConsts.BookChapterTable.COLUMN_CHAPTER_NUM);
        return RestResp.ok(bookChapterMapper.selectList(queryWrapper).stream()
                .map(v -> BookChapterRespDto.builder()
                        .id(v.getId())
                        .chapterName(v.getChapterName())
                        .isVip(v.getIsVip())
                        .build()).toList());
    }

    @Override
    public RestResp<BookContentAboutRespDto> getBookContentAbout(Long chapterId) {
        log.debug("userId : {}", UserHolder.getUserId());
        // 查询章节信息
        BookChapterRespDto chapter = bookChapterCacheManager.getChapter(chapterId);
        // 查询章节内容
        String bookContent = bookContentCacheManager.getBookContent(chapterId);
        // 查询小说信息
        BookInfoRespDto bookInfo = bookInfoCacheManager.getBookInfo(chapter.getBookId());

        // 组装数据并返回
        return RestResp.ok(BookContentAboutRespDto.builder()
                .bookInfo(bookInfo)
                .chapterInfo(chapter)
                .bookContent(bookContent)
                .build());
    }

    @Override
    public RestResp<Long> getPreChapterId(Long chapterId) {
        // 查询小说id和章节号
        BookChapterRespDto chapter = bookChapterCacheManager.getChapter(chapterId);
        Long bookId = chapter.getBookId();
        Integer chapterNum = chapter.getChapterNum();
        // 查询上一章节信息并返回章节id
        LambdaQueryWrapper<BookChapter> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookChapter::getBookId, bookId)
                .lt(BookChapter::getChapterNum, chapterNum)
                .orderByDesc(BookChapter::getChapterNum)
                .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        return RestResp.ok(
                Optional.ofNullable(bookChapterMapper.selectOne(wrapper))
                        .map(BookChapter::getId)
                        .orElse(null)
        );
    }

    @Override
    public RestResp<Long> getNextChapterId(Long chapterId) {
        // 查询小说ID 和 章节号
        BookChapterRespDto chapter = bookChapterCacheManager.getChapter(chapterId);
        Long bookId = chapter.getBookId();
        Integer chapterNum = chapter.getChapterNum();

        // 查询下一章信息并返回章节ID
        QueryWrapper<BookChapter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(DatabaseConsts.BookChapterTable.COLUMN_BOOK_ID, bookId)
                .gt(DatabaseConsts.BookChapterTable.COLUMN_CHAPTER_NUM, chapterNum)
                .orderByAsc(DatabaseConsts.BookChapterTable.COLUMN_CHAPTER_NUM)
                .last(DatabaseConsts.SqlEnum.LIMIT_1.getSql());
        return RestResp.ok(
                Optional.ofNullable(bookChapterMapper.selectOne(queryWrapper))
                        .map(BookChapter::getId)
                        .orElse(null)
        );
    }

    @Override
    public RestResp<List<BookRankRespDto>> listVisitRankBooks() {
        return RestResp.ok(bookRankCacheManager.listVisitRankBooks());
    }

    @Override
    public RestResp<List<BookRankRespDto>> listNewestRankBooks() {
        return RestResp.ok(bookRankCacheManager.listNewestRankBooks());
    }

    @Override
    public RestResp<List<BookRankRespDto>> listUpdateRankBooks() {
        return RestResp.ok(bookRankCacheManager.listUpdateRankBooks());
    }

    @Override
    public RestResp<BookCommentRespDto> listNewestComments(Long bookId) {
        // 查询评论总数
        LambdaQueryWrapper<BookComment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BookComment::getBookId, bookId);
        Long commentCount = bookCommentMapper.selectCount(wrapper);
        BookCommentRespDto bookCommentRespDto = BookCommentRespDto.builder()
                .commentTotal(commentCount)
                .build();
        // 没有评论则返回空列表
        if(commentCount <= 0) {
            bookCommentRespDto.setComments(Collections.emptyList();
            return RestResp.ok(bookCommentRespDto);
        }
        // 查询最新评论
        LambdaQueryWrapper<BookComment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookComment::getBookId, bookId)
                .orderByDesc(BookComment::getCreateTime)
                .last(DatabaseConsts.SqlEnum.LIMIT_5.getSql());
        List<BookComment> bookComments = bookCommentMapper.selectList(queryWrapper);
        // 查询评论用户信息 并设置需要返回的评论用户名
        List<Long> userIds = bookComments.stream().map(BookComment::getUserId).toList();
        List<UserInfoRespDto> userInfos = userFeignManager.listUserInfoByIds(userIds);
        Map<Long, UserInfoRespDto> userInfoMap = userInfos.stream()
                .collect(Collectors.toMap(UserInfoRespDto::getId, Function.identity()));
        List<BookCommentRespDto.CommentInfo> commentInfos = bookComments.stream()
                .map(v -> BookCommentRespDto.CommentInfo.builder()
                        .id(v.getId())
                        .commentUserId(v.getUserId())
                        .commentUser(userInfoMap.get(v.getUserId()).getUsername())
                        .commentUserPhoto(userInfoMap.get(v.getUserId()).getUserPhoto())
                        .commentContent(v.getCommentContent())
                        .commentTime(v.getCreateTime()).build()).toList();
        bookCommentRespDto.setComments(commentInfos);
        return RestResp.ok(bookCommentRespDto);
    }

    @Override
    public RestResp<List<BookEsRespDto>> listNextEsBooks(Long maxBookId) {
        QueryWrapper<BookInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.clear();
        queryWrapper
                .orderByAsc(DatabaseConsts.CommonColumnEnum.ID.getName())
                .gt(DatabaseConsts.CommonColumnEnum.ID.getName(), maxBookId)
                .gt(DatabaseConsts.BookTable.COLUMN_WORD_COUNT, 0)
                .last(DatabaseConsts.SqlEnum.LIMIT_30.getSql());
        return RestResp.ok(bookInfoMapper.selectList(queryWrapper).stream().map(bookInfo -> BookEsRespDto.builder()
                .id(bookInfo.getId())
                .categoryId(bookInfo.getCategoryId())
                .categoryName(bookInfo.getCategoryName())
                .bookDesc(bookInfo.getBookDesc())
                .bookName(bookInfo.getBookName())
                .authorId(bookInfo.getAuthorId())
                .authorName(bookInfo.getAuthorName())
                .bookStatus(bookInfo.getBookStatus())
                .commentCount(bookInfo.getCommentCount())
                .isVip(bookInfo.getIsVip())
                .score(bookInfo.getScore())
                .visitCount(bookInfo.getVisitCount())
                .wordCount(bookInfo.getWordCount())
                .workDirection(bookInfo.getWorkDirection())
                .lastChapterId(bookInfo.getLastChapterId())
                .lastChapterName(bookInfo.getLastChapterName())
                .lastChapterUpdateTime(bookInfo.getLastChapterUpdateTime()
                        .toInstant(ZoneOffset.ofHours(8)).toEpochMilli())
                .build()).collect(Collectors.toList()));
    }

    @Override
    public RestResp<List<BookInfoRespDto>> listBookInfoByIds(List<Long> bookIds) {
        QueryWrapper<BookInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(DatabaseConsts.CommonColumnEnum.ID.getName(), bookIds);
        return RestResp.ok(
                bookInfoMapper.selectList(queryWrapper).stream().map(v -> BookInfoRespDto.builder()
                        .id(v.getId())
                        .bookName(v.getBookName())
                        .authorName(v.getAuthorName())
                        .picUrl(v.getPicUrl())
                        .bookDesc(v.getBookDesc())
                        .build()).collect(Collectors.toList()));
    }

    @Override
    public RestResp<Void> saveComment(@Key(expr = "#{userId + '::' + bookId}") BookCommentReqDto dto) {
        return null;
    }

    @Override
    public RestResp<Void> updateComment(BookCommentReqDto dto) {
        return null;
    }

    @Override
    public RestResp<Void> deleteComment(BookCommentReqDto dto) {
        return null;
    }

    @Override
    public RestResp<Void> saveBook(BookAddReqDto dto) {
        return null;
    }

    @Override
    public RestResp<PageRespDto<BookInfoRespDto>> listAuthorBooks(BookPageReqDto dto) {
        return null;
    }

    @Override
    public RestResp<Void> saveBookChapter(ChapterAddReqDto dto) {
        return null;
    }

    @Override
    public RestResp<PageRespDto<BookChapterRespDto>> listBookChapters(ChapterPageReqDto dto) {
        return null;
    }
}
