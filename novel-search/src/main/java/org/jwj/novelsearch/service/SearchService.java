package org.jwj.novelsearch.service;

import org.jwj.novelbookapi.dto.req.BookSearchReqDto;
import org.jwj.novelbookapi.dto.resp.BookInfoRespDto;
import org.jwj.novelcommon.resp.PageRespDto;
import org.jwj.novelcommon.resp.RestResp;

import java.io.IOException;

public interface SearchService {
    RestResp<PageRespDto<BookInfoRespDto>> searchBooks(BookSearchReqDto condition) throws IOException;
}
