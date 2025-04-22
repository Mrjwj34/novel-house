package org.jwj.novelai.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.jwj.novelai.enums.AiProcessType;

@Data
@Schema(description = "AI 处理请求 DTO")
public class AiProcessReqDto {

    @Schema(description = "需要 AI 处理的原文内容", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "原文内容不能为空")
    private String originalContent;

    @Schema(description = "要执行的操作类型 (EXPAND: 扩写, POLISH: 润色)", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "操作类型不能为空")
    private AiProcessType processType;

    // 可以添加其他需要的字段，例如：
    // @Schema(description = "期望的风格或语气")
    // private String desiredStyle;
    //
    // @Schema(description = "上下文信息")
    // private String context;
}