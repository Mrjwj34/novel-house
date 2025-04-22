package org.jwj.novelai.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

/**
 * AI 处理操作类型枚举
 */
@Getter
@Schema(description = "AI 处理操作类型")
public enum AiProcessType {
    EXPAND("扩写", "请帮我扩写以下段落，增加更多细节和描述，保持原有风格和主旨："),
    POLISH("润色", "请帮我润色以下段落，使其语言更流畅、表达更精准、更具文采：");

    private final String description;
    private final String promptPrefix; // 用于构建提示语的前缀

    AiProcessType(String description, String promptPrefix) {
        this.description = description;
        this.promptPrefix = promptPrefix;
    }
}
