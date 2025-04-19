package org.jwj.novelbookapi.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.jwj.novelcommon.req.PageReqDto;

/**
 * 章节发布页 请求DTO
 *
 * @author xiongxiaoyang
 * @date 2022/5/23
 */
@Data
public class BookPageReqDto extends PageReqDto {

    /**
     * 作家ID
     */
    @Schema(description = "作家ID", required = true)
    private Long authorId;


}
