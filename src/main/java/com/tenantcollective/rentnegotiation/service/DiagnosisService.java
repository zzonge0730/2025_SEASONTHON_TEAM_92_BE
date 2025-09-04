package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.model.DiagnosisResponse;
import com.tenantcollective.rentnegotiation.model.DiagnosisStats;
import com.tenantcollective.rentnegotiation.model.User;
import com.tenantcollective.rentnegotiation.repo.DiagnosisResponseRepository;
import com.tenantcollective.rentnegotiation.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DiagnosisService {

    private final DiagnosisResponseRepository diagnosisResponseRepository;
    private final UserRepository userRepository;

    @Autowired
    public DiagnosisService(DiagnosisResponseRepository diagnosisResponseRepository, UserRepository userRepository) {
        this.diagnosisResponseRepository = diagnosisResponseRepository;
        this.userRepository = userRepository;
    }

    public DiagnosisResponse saveResponse(DiagnosisResponse response) {
        // In a real app, you'd validate the response
        return diagnosisResponseRepository.save(response);
    }

    public DiagnosisStats getComparisonStats(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        List<DiagnosisResponse> userResponses = diagnosisResponseRepository.findByUserId(userId);
        List<DiagnosisResponse> buildingResponses = diagnosisResponseRepository.findByBuildingName(user.getBuildingName());
        List<DiagnosisResponse> neighborhoodResponses = diagnosisResponseRepository.findByNeighborhood(user.getNeighborhood());

        Map<String, Double> userScores = calculateAverageScores(userResponses);
        Map<String, Double> buildingScores = calculateAverageScores(buildingResponses);
        Map<String, Double> neighborhoodScores = calculateAverageScores(neighborhoodResponses);

        DiagnosisStats stats = new DiagnosisStats(userScores, buildingScores, neighborhoodScores);
        stats.setUserParticipantCount((int) countUniqueUsers(userResponses));
        stats.setBuildingParticipantCount((int) countUniqueUsers(buildingResponses));
        stats.setNeighborhoodParticipantCount((int) countUniqueUsers(neighborhoodResponses));

        return stats;
    }

    private long countUniqueUsers(List<DiagnosisResponse> responses) {
        if (responses == null || responses.isEmpty()) {
            return 0;
        }
        return responses.stream().map(DiagnosisResponse::getUserId).distinct().count();
    }

    private Map<String, Double> calculateAverageScores(List<DiagnosisResponse> responses) {
        if (responses == null || responses.isEmpty()) {
            return Map.of();
        }

        // Group responses by questionId and calculate the average of answerValue for each question
        return responses.stream()
                .collect(Collectors.groupingBy(
                        DiagnosisResponse::getQuestionId,
                        Collectors.averagingDouble(DiagnosisResponse::getAnswerValue)
                ));
    }
}
