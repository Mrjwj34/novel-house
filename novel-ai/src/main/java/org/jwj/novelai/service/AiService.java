package org.jwj.novelai.service;


import org.jwj.novelai.dto.req.AiProcessReqDto;
import org.jwj.novelai.dto.resp.AiProcessRespDto;

/**
 * AI 写作服务接口
 */
public interface AiService {

    /**
     * 处理文本（扩写或润色）
     * @param dto 请求参数，包含原文和操作类型
     * @return 处理后的结果
     */
    AiProcessRespDto processText(AiProcessReqDto dto);
}