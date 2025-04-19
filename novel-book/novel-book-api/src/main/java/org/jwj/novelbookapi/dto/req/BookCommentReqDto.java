package org.jwj.novelbookapi.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 小说评论 请求DTO
 * @author xiongxiaoyang
 * @date 2022/5/17
 */
@Data
public class BookCommentReqDto {

    private Long commentId;

    private Long userId;

    @Schema(description = "小说ID")
    private Long bookId;

    @Schema(description = "评论内容")
    private String commentContent;

}
