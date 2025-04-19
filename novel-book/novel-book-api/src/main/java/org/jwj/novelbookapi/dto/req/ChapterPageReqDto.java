package org.jwj.novelbookapi.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.jwj.novelcommon.req.PageReqDto;

/**
 * 章节发布页 请求DTO
 *
 * @author xiongxiaoyang
 * @date 2022/5/23
 */
@Data
public class ChapterPageReqDto extends PageReqDto {

    /**
     * 小说ID
     */
    @NotBlank
    @Schema(description = "小说ID", required = true)
    private Long bookId;


}
