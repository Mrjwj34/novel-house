package org.jwj.novelai.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jwj.novelai.dto.req.AiProcessReqDto;
import org.jwj.novelai.dto.resp.AiProcessRespDto;
import org.jwj.novelai.service.AiService;
import org.jwj.novelcommon.constants.ApiRouterConsts;
import org.jwj.novelcommon.resp.RestResp;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AiController", description = "AI 写作助手模块")
@RestController
// 定义一个基础路由，例如 /api/ai
@RequestMapping(ApiRouterConsts.API_URL_PREFIX + "/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @Operation(summary = "AI 文本处理接口 (扩写/润色)")
    @PostMapping("/process")
    public RestResp<AiProcessRespDto> processText(@Valid @RequestBody AiProcessReqDto dto) {
        AiProcessRespDto result = aiService.processText(dto);
        return RestResp.ok(result);
    }

}