package com.tenantcollective.rentnegotiation.config;

import com.tenantcollective.rentnegotiation.llm.LlmClient;
import com.tenantcollective.rentnegotiation.llm.NoopLlmClient;
import com.tenantcollective.rentnegotiation.llm.OpenAiLlmClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class LlmConfig {
    
    @Value("${openai.api.key:}")
    private String openaiApiKey;
    
    @Value("${llm.enabled:false}")
    private boolean llmEnabled;
    
    @Bean
    @Primary
    public LlmClient llmClient(OpenAiLlmClient openAiLlmClient, NoopLlmClient noopLlmClient) {
        // API 키가 있고 LLM이 활성화된 경우 OpenAI 클라이언트 사용
        if (llmEnabled && openaiApiKey != null && !openaiApiKey.trim().isEmpty()) {
            return openAiLlmClient;
        }
        
        // 그렇지 않으면 템플릿 기반 클라이언트 사용
        return noopLlmClient;
    }
}