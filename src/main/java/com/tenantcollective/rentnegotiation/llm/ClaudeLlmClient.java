package com.tenantcollective.rentnegotiation.llm;

import com.tenantcollective.rentnegotiation.model.Group;
import com.tenantcollective.rentnegotiation.model.LetterRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class ClaudeLlmClient implements LlmClient {
    
    @Value("${claude.api.key:}")
    private String apiKey;
    
    @Value("${claude.api.url:https://api.anthropic.com/v1/messages}")
    private String apiUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Override
    public String generateLetter(Group group, LetterRequest request, String samplePainPoints) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return generateTemplateLetter(group, request, samplePainPoints);
        }
        
        try {
            return callClaudeApi(group, request, samplePainPoints);
        } catch (Exception e) {
            System.err.println("Claude API 호출 실패, 템플릿 기반으로 폴백: " + e.getMessage());
            return generateTemplateLetter(group, request, samplePainPoints);
        }
    }
    
    private String callClaudeApi(Group group, LetterRequest request, String samplePainPoints) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");
        
        String prompt = buildPrompt(group, request, samplePainPoints);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "claude-3-sonnet-20240229");
        requestBody.put("max_tokens", 1000);
        requestBody.put("messages", new Object[]{
            Map.of("role", "user", "content", prompt)
        });
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody.containsKey("content")) {
                Object[] content = (Object[]) responseBody.get("content");
                if (content.length > 0) {
                    Map<String, Object> textContent = (Map<String, Object>) content[0];
                    return (String) textContent.get("text");
                }
            }
        }
        
        throw new RuntimeException("Claude API 응답 파싱 실패");
    }
    
    private String buildPrompt(Group group, LetterRequest request, String samplePainPoints) {
        return String.format(
            "다음 정보를 바탕으로 임대인에게 보낼 월세 협상 편지를 작성해주세요:\n\n" +
            "그룹 정보:\n" +
            "- 그룹명: %s (%s)\n" +
            "- 가구 수: %d세대\n" +
            "- 평균 월세: %.0f원\n" +
            "- 중간값 월세: %.0f원\n" +
            "- 평균 인상 통지율: %.1f%%\n\n" +
            "협상 제안:\n" +
            "- 인상 상한: %d%%\n" +
            "- 계약 기간: %d개월\n" +
            "- 통지 기간: %d일\n" +
            "- 연락처: %s, %s\n\n" +
            "문제점:\n%s\n\n" +
            "편지는 한국어로 작성하고, 정중하면서도 설득력 있게 작성해주세요.",
            group.getLabel(),
            group.getScope().equals("building") ? "건물" : "동네",
            group.getGroupSize(),
            group.getAvgRentKrw(),
            group.getMedianRentKrw(),
            group.getAvgNoticePct(),
            request.getCapPct(),
            request.getTermMonths(),
            request.getNoticeDays(),
            request.getContactEmail(),
            request.getContactPhone(),
            samplePainPoints != null ? samplePainPoints : "일반적인 문제점들"
        );
    }
    
    private String generateTemplateLetter(Group group, LetterRequest request, String samplePainPoints) {
        // 템플릿 기반 편지 생성 로직
        return String.format(
            "안녕하세요 관리자님,\n\n" +
            "저희는 %s의 %d세대 임차인들입니다.\n\n" +
            "협상 제안: 인상 상한 %d%%, 계약 기간 %d개월\n" +
            "문제점: %s\n\n" +
            "연락처: %s\n" +
            "감사합니다.",
            group.getLabel(),
            group.getGroupSize(),
            request.getCapPct(),
            request.getTermMonths(),
            samplePainPoints != null ? samplePainPoints : "일반적인 문제점들",
            request.getContactEmail()
        );
    }
}