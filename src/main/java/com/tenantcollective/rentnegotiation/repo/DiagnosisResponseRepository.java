package com.tenantcollective.rentnegotiation.repo;

import com.tenantcollective.rentnegotiation.model.DiagnosisResponse;

import java.util.List;

public interface DiagnosisResponseRepository {
    DiagnosisResponse save(DiagnosisResponse diagnosisResponse);
    List<DiagnosisResponse> findAll();
    List<DiagnosisResponse> findByUserId(String userId);
    List<DiagnosisResponse> findByBuildingName(String buildingName);
    List<DiagnosisResponse> findByNeighborhood(String neighborhood);
}
