package org.jwj.novelsearch.manager.feign;

import lombok.AllArgsConstructor;
import org.jwj.novelbookapi.dto.resp.BookEsRespDto;
import org.jwj.novelbookapi.feign.BookFeign;
import org.jwj.novelcommon.constants.ErrorCodeEnum;
import org.jwj.novelcommon.resp.RestResp;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public List<BookEsRespDto> listEsBooks(Long maxBookId){
        RestResp<List<BookEsRespDto>> listRestResp = bookFeign.listNextEsBooks(maxBookId);
        if(Objects.equals(ErrorCodeEnum.OK.getCode(),listRestResp.getCode())){
            return listRestResp.getData();
        }
        return new ArrayList<>(0);
    }

}
