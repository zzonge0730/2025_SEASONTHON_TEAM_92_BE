package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.model.AnonymousComplaint;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AnonymousComplaintService {
    
    private final Map<String, AnonymousComplaint> complaintsById = new ConcurrentHashMap<>();
    private final Map<String, List<AnonymousComplaint>> complaintsByBuilding = new ConcurrentHashMap<>();
    private final Map<String, List<AnonymousComplaint>> complaintsByNeighborhood = new ConcurrentHashMap<>();
    
    public AnonymousComplaint saveComplaint(AnonymousComplaint complaint) {
        if (complaint.getId() == null) {
            complaint.setId("complaint_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8));
        }
        
        complaintsById.put(complaint.getId(), complaint);
        
        // Building별 인덱스 업데이트
        complaintsByBuilding.computeIfAbsent(complaint.getBuildingName(), k -> new ArrayList<>()).add(complaint);
        
        // Neighborhood별 인덱스 업데이트
        complaintsByNeighborhood.computeIfAbsent(complaint.getNeighborhood(), k -> new ArrayList<>()).add(complaint);
        
        System.out.println("📝 익명 신고 저장: " + complaint.getBuildingName() + " (ID: " + complaint.getId() + ")");
        return complaint;
    }
    
    public Optional<AnonymousComplaint> findComplaintById(String id) {
        return Optional.ofNullable(complaintsById.get(id));
    }
    
    public List<AnonymousComplaint> getAllComplaints() {
        return complaintsById.values()
                .stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }
    
    public List<AnonymousComplaint> getComplaintsByBuilding(String buildingName) {
        return complaintsByBuilding.getOrDefault(buildingName, new ArrayList<>())
                .stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }
    
    public List<AnonymousComplaint> getComplaintsByNeighborhood(String neighborhood) {
        return complaintsByNeighborhood.getOrDefault(neighborhood, new ArrayList<>())
                .stream()
                .sorted((a, b) -> b.getTimestamp().compareTo(a.getTimestamp()))
                .collect(Collectors.toList());
    }
    
    public boolean updateComplaintVerification(String id, boolean verified, String verifiedBy) {
        AnonymousComplaint complaint = complaintsById.get(id);
        if (complaint != null) {
            complaint.setVerified(verified);
            complaint.setVerifiedBy(verifiedBy);
            complaint.setVerifiedAt(LocalDateTime.now());
            
            System.out.println("✅ 신고 검증 상태 업데이트: " + id + " -> " + (verified ? "검증됨" : "거부됨"));
            return true;
        }
        return false;
    }
    
    public void deleteComplaint(String id) {
        AnonymousComplaint complaint = complaintsById.remove(id);
        if (complaint != null) {
            // Building 인덱스에서 제거
            List<AnonymousComplaint> buildingComplaints = complaintsByBuilding.get(complaint.getBuildingName());
            if (buildingComplaints != null) {
                buildingComplaints.removeIf(c -> c.getId().equals(id));
            }
            
            // Neighborhood 인덱스에서 제거
            List<AnonymousComplaint> neighborhoodComplaints = complaintsByNeighborhood.get(complaint.getNeighborhood());
            if (neighborhoodComplaints != null) {
                neighborhoodComplaints.removeIf(c -> c.getId().equals(id));
            }
            
            System.out.println("🗑️ 익명 신고 삭제: " + id);
        }
    }
    
    public long getVerifiedComplaintsCount() {
        return complaintsById.values()
                .stream()
                .filter(AnonymousComplaint::isVerified)
                .count();
    }
    
    public long getUnverifiedComplaintsCount() {
        return complaintsById.values()
                .stream()
                .filter(complaint -> !complaint.isVerified())
                .count();
    }
}