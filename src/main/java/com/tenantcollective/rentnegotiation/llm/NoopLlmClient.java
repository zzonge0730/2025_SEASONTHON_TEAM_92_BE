package com.tenantcollective.rentnegotiation.llm;

import com.tenantcollective.rentnegotiation.model.Group;
import com.tenantcollective.rentnegotiation.model.LetterRequest;
import org.springframework.stereotype.Component;

@Component
public class NoopLlmClient implements LlmClient {
    
    @Override
    public String generateLetter(Group group, LetterRequest request, String samplePainPoints) {
        return formatTemplate(group, request, samplePainPoints);
    }
    
    private String formatTemplate(Group group, LetterRequest request, String samplePainPoints) {
        String landlordName = "관리자님"; // Placeholder
        
        return String.format(
            "To: %s\n" +
            "From: %s (%s), %d households\n\n" +
            "Background:\n" +
            "- Avg rent %.0f KRW / Median %.0f KRW\n" +
            "- Avg recent increase notice %.1f %%\n\n" +
            "Proposal:\n" +
            "1) Increase cap: %d %%\n" +
            "2) Term: %d months\n" +
            "3) Notice: %d days\n\n" +
            "Rationale:\n" +
            "Reducing vacancy risk and turnover costs benefits both parties.\n" +
            "Pain points noted: %s.\n\n" +
            "Please reply to %s / %s.\n" +
            "Sincerely,\n" +
            "Tenants of %s",
            landlordName,
            group.getLabel(),
            group.getScope().equals("building") ? "건물" : "동네",
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