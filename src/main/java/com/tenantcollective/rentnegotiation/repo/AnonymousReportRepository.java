package com.tenantcollective.rentnegotiation.repo;

import com.tenantcollective.rentnegotiation.model.AnonymousReport;
import java.util.List;
import java.util.Optional;

public interface AnonymousReportRepository {
    AnonymousReport save(AnonymousReport report);
    Optional<AnonymousReport> findById(String id);
    List<AnonymousReport> findAll();
    List<AnonymousReport> findByBuildingName(String buildingName);
    List<AnonymousReport> findByNeighborhood(String neighborhood);
    void deleteById(String id);
}
