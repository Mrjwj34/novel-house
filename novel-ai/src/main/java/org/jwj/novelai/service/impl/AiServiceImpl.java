package org.jwj.novelai.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jwj.novelai.dto.req.AiProcessReqDto;
import org.jwj.novelai.dto.resp.AiProcessRespDto;
import org.jwj.novelai.enums.AiProcessType;
import org.jwj.novelai.service.AiService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
// 建议重命名类名
public class AiServiceImpl implements AiService {

    private final ChatClient chatClient; // 注入保持不变，Spring AI 负责提供 OpenAI 的实现

    @Override
    public AiProcessRespDto processText(AiProcessReqDto dto) {
        log.info("接收到 AI 处理请求，类型: {}, 原文长度: {}", dto.getProcessType(), dto.getOriginalContent().length());

        String promptString = buildPrompt(dto.getProcessType(), dto.getOriginalContent());
        PromptTemplate promptTemplate = new PromptTemplate(promptString);
        Prompt prompt = promptTemplate.create(Map.of("originalContent", dto.getOriginalContent()));

        log.debug("发送给 OpenAI 的 Prompt: {}", prompt.getContents());

        ChatResponse chatResponse = chatClient.prompt(prompt)
                .call()
                .chatResponse();

        String result = chatResponse.getResult().getOutput().getText();
        log.info("OpenAI 处理完成，返回内容长度: {}", result.length());

        return AiProcessRespDto.builder()
                .processedContent(result)
                .build();
    }

    private String buildPrompt(AiProcessType type, String originalContent) {
        // ... 构建 Prompt 的逻辑通常也无需改变，但可以根据 OpenAI 的特性进行微调 ...
        String template =
                """
                {promptPrefix}
                ---
                原文内容：
                {originalContent}
                ---
                请只输出处理后的文本内容，不要包含任何额外的解释或标记。
                """;

        PromptTemplate promptTemplate = new PromptTemplate(template);
        return promptTemplate.render(Map.of(
                "promptPrefix", type.getPromptPrefix(),
                "originalContent", originalContent
        ));
    }
}