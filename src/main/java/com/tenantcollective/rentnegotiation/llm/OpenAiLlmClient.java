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
public class OpenAiLlmClient implements LlmClient {
    
    @Value("${openai.api.key:}")
    private String apiKey;
    
    @Value("${openai.api.url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    @Override
    public String generateLetter(Group group, LetterRequest request, String samplePainPoints) {
        // API 키가 없으면 템플릿 기반으로 폴백
        if (apiKey == null || apiKey.trim().isEmpty()) {
            return generateTemplateLetter(group, request, samplePainPoints);
        }
        
        try {
            return callOpenAiApi(group, request, samplePainPoints);
        } catch (Exception e) {
            // LLM 호출 실패 시 템플릿 기반으로 폴백
            System.err.println("OpenAI API 호출 실패, 템플릿 기반으로 폴백: " + e.getMessage());
            return generateTemplateLetter(group, request, samplePainPoints);
        }
    }
    
    private String callOpenAiApi(Group group, LetterRequest request, String samplePainPoints) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        
        String prompt = buildPrompt(group, request, samplePainPoints);
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", "gpt-4o-mini"); // 더 저렴하고 성능 좋은 모델
        requestBody.put("messages", new Object[]{
            Map.of("role", "system", "content", "당신은 임차인들의 월세 협상을 도와주는 전문가입니다. 정중하고 전문적이며 설득력 있는 편지를 작성해주세요."),
            Map.of("role", "user", "content", prompt)
        });
        requestBody.put("max_tokens", 1000);
        requestBody.put("temperature", 0.7);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
        
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            if (responseBody.containsKey("choices")) {
                Object[] choices = (Object[]) responseBody.get("choices");
                if (choices.length > 0) {
                    Map<String, Object> choice = (Map<String, Object>) choices[0];
                    Map<String, Object> message = (Map<String, Object>) choice.get("message");
                    return (String) message.get("content");
                }
            }
        }
        
        throw new RuntimeException("OpenAI API 응답 파싱 실패");
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
            "편지는 한국어로 작성하고, 정중하면서도 설득력 있게 작성해주세요. " +
            "상호 이익이 되는 방향으로 접근하며, 구체적인 데이터와 근거를 제시해주세요.",
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
        String landlordName = "관리자님";
        
        return String.format(
            "안녕하세요 %s님,\n\n" +
            "저희는 %s의 %d세대 임차인들입니다. 평균 월세 %.0f원, 중간값 %.0f원으로 " +
            "거주하고 있으며, 최근 평균 %.1f%%의 인상 통지를 받았습니다.\n\n" +
            "저희가 제안하는 협상 조건은 다음과 같습니다:\n" +
            "1. 월세 인상 상한: %d%%\n" +
            "2. 계약 기간: %d개월\n" +
            "3. 인상 통지 기간: %d일\n\n" +
            "현재 겪고 있는 문제점들:\n%s\n\n" +
            "이러한 조건은 공실 위험을 줄이고 이전 비용을 절약하여 양측 모두에게 이익이 됩니다.\n\n" +
            "연락처: %s, %s\n" +
            "답변 부탁드립니다.\n\n" +
            "감사합니다.\n" +
            "%s 임차인들 드림",
            landlordName,
            group.getLabel(),
            group.getGroupSize(),
            group.getAvgRentKrw(),
            group.getMedianRentKrw(),
            group.getAvgNoticePct(),
            request.getCapPct(),
            request.getTermMonths(),
            request.getNoticeDays(),
            samplePainPoints != null ? samplePainPoints : "일반적인 문제점들",
            request.getContactEmail(),
            request.getContactPhone(),
            group.getLabel()
        );
    }
}