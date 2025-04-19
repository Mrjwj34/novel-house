package org.jwj.novelbookservice.manager.cache;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.jwj.novelbookapi.dto.resp.BookCategoryRespDto;
import org.jwj.novelbookservice.dao.entity.BookCategory;
import org.jwj.novelbookservice.dao.mapper.BookCategoryMapper;
import org.jwj.novelcommon.constants.CacheConsts;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class BookCategoryCacheManager {
    private final BookCategoryMapper bookCategoryMapper;
    /**
     * 根据作品方向查询小说分类列表，并放入缓存中
     */
    @Cacheable(value = CacheConsts.BOOK_CATEGORY_LIST_CACHE_NAME)
    public List<BookCategoryRespDto> listCategory(Integer workDirection) {
        LambdaQueryWrapper<BookCategory> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(BookCategory::getWorkDirection, workDirection);
        return bookCategoryMapper.selectList(queryWrapper).stream().map(v ->
                BookCategoryRespDto.builder()
                        .id(v.getId())
                        .name(v.getName())
                        .build()).toList();
    }
}
