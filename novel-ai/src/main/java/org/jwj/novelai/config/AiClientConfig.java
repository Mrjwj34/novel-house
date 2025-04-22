package org.jwj.novelai.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AiClientConfig {
    private final OpenAiChatModel openAiChatModel;
    @Bean
    public ChatClient chatClient(){
        return ChatClient.builder(openAiChatModel).build();
    }
}
