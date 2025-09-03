package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.llm.LlmClient;
import com.tenantcollective.rentnegotiation.model.Group;
import com.tenantcollective.rentnegotiation.model.LetterRequest;
import com.tenantcollective.rentnegotiation.model.LetterResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class LetterService {
    
    private final GroupingService groupingService;
    private final LlmClient llmClient;
    
    @Autowired
    public LetterService(GroupingService groupingService, LlmClient llmClient) {
        this.groupingService = groupingService;
        this.llmClient = llmClient;
    }
    
    public LetterResponse generateLetter(LetterRequest request) {
        // Find the group by ID
        List<Group> allGroups = groupingService.getGroups("building");
        allGroups.addAll(groupingService.getGroups("neighborhood"));
        
        Group targetGroup = allGroups.stream()
                .filter(group -> group.getGroupId().equals(request.getGroupId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Group not found: " + request.getGroupId()));
        
        // Generate sample pain points (in a real app, this would come from the actual tenant data)
        String samplePainPoints = generateSamplePainPoints();
        
        // Generate the letter
        String generatedText = llmClient.generateLetter(targetGroup, request, samplePainPoints);
        
        return new LetterResponse(generatedText, 0, false); // No LLM used in this implementation
    }
    
    private String generateSamplePainPoints() {
        List<String> commonPainPoints = List.of(
            "엘리베이터 고장",
            "단기 통지",
            "보안 문제",
            "난방비 인상",
            "관리비 투명성 부족",
            "수리 지연",
            "소음 문제"
        );
        
        Random random = new Random();
        int numPoints = random.nextInt(3) + 1; // 1-3 pain points
        List<String> selectedPoints = new ArrayList<>();
        
        for (int i = 0; i < numPoints; i++) {
            String point = commonPainPoints.get(random.nextInt(commonPainPoints.size()));
            if (!selectedPoints.contains(point)) {
                selectedPoints.add(point);
            }
        }
        
        return String.join(", ", selectedPoints);
    }
}