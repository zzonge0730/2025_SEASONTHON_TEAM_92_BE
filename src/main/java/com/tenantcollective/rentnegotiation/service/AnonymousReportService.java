package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.model.AnonymousReport;
import com.tenantcollective.rentnegotiation.repo.AnonymousReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnonymousReportService {
    
    private final AnonymousReportRepository anonymousReportRepository;
    
    @Autowired
    public AnonymousReportService(AnonymousReportRepository anonymousReportRepository) {
        this.anonymousReportRepository = anonymousReportRepository;
    }
    
    public AnonymousReport saveReport(AnonymousReport report) {
        return anonymousReportRepository.save(report);
    }
    
    public Optional<AnonymousReport> findReportById(String id) {
        return anonymousReportRepository.findById(id);
    }
    
    public List<AnonymousReport> getAllReports() {
        return anonymousReportRepository.findAll();
    }
    
    public List<AnonymousReport> getReportsByBuilding(String buildingName) {
        return anonymousReportRepository.findByBuildingName(buildingName);
    }
    
    public List<AnonymousReport> getReportsByNeighborhood(String neighborhood) {
        return anonymousReportRepository.findByNeighborhood(neighborhood);
    }
    
    public void deleteReport(String id) {
        anonymousReportRepository.deleteById(id);
    }
    
    public boolean updateReportVerification(String id, boolean verified) {
        Optional<AnonymousReport> reportOpt = anonymousReportRepository.findById(id);
        if (reportOpt.isPresent()) {
            AnonymousReport report = reportOpt.get();
            report.setVerified(verified);
            anonymousReportRepository.save(report);
            return true;
        }
        return false;
    }
}
