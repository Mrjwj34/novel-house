package org.jwj.novelai.dto.resp;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "AI 处理响应 DTO")
public class AiProcessRespDto {

    @Schema(description = "经过 AI 处理后的内容")
    private String processedContent;
}