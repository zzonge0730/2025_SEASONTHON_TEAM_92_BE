package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.model.Group;
import com.tenantcollective.rentnegotiation.model.MarketData;
import com.tenantcollective.rentnegotiation.model.Tenant;
import com.tenantcollective.rentnegotiation.util.GroupIdGenerator;
import com.tenantcollective.rentnegotiation.util.StatisticsUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GroupingService {
    
    private final TenantService tenantService;
    private final RealEstateApiService realEstateApiService;
    
    @Autowired
    public GroupingService(TenantService tenantService, RealEstateApiService realEstateApiService) {
        this.tenantService = tenantService;
        this.realEstateApiService = realEstateApiService;
    }
    
    public List<Group> getGroups(String scope) {
        List<Tenant> allTenants = tenantService.getAllTenants();
        
        if (scope.equals("building")) {
            return groupByBuilding(allTenants);
        } else if (scope.equals("neighborhood")) {
            return groupByNeighborhood(allTenants);
        } else {
            throw new IllegalArgumentException("Invalid scope. Must be 'building' or 'neighborhood'");
        }
    }
    
    private List<Group> groupByBuilding(List<Tenant> tenants) {
        Map<String, List<Tenant>> buildingGroups = tenants.stream()
                .collect(Collectors.groupingBy(tenant -> 
                    tenant.getBuildingName() + "|" + tenant.getStreetAddress()));
        
        return buildingGroups.entrySet().stream()
                .map(entry -> {
                    String[] parts = entry.getKey().split("\\|");
                    String buildingName = parts[0];
                    String streetAddress = parts[1];
                    List<Tenant> groupTenants = entry.getValue();
                    
                    String groupId = GroupIdGenerator.generateGroupId(streetAddress);
                    String label = buildingName + " (" + groupTenants.get(0).getNeighborhood() + ")";
                    
                    return createGroup(groupId, label, "building", groupTenants);
                })
                .filter(group -> group.getGroupSize() > 0)
                .collect(Collectors.toList());
    }
    
    private List<Group> groupByNeighborhood(List<Tenant> tenants) {
        Map<String, List<Tenant>> neighborhoodGroups = tenants.stream()
                .collect(Collectors.groupingBy(Tenant::getNeighborhood));
        
        return neighborhoodGroups.entrySet().stream()
                .map(entry -> {
                    String neighborhood = entry.getKey();
                    List<Tenant> groupTenants = entry.getValue();
                    
                    String groupId = GroupIdGenerator.generateGroupId(neighborhood);
                    String label = neighborhood + " (neighborhood)";
                    
                    return createGroup(groupId, label, "neighborhood", groupTenants);
                })
                .filter(group -> group.getGroupSize() > 0)
                .collect(Collectors.toList());
    }
    
    private Group createGroup(String groupId, String label, String scope, List<Tenant> tenants) {
        List<Integer> rents = tenants.stream()
                .map(Tenant::getCurrentRentKrw)
                .collect(Collectors.toList());
        
        List<Integer> noticePcts = tenants.stream()
                .map(Tenant::getIncreaseNoticePctOptional)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        
        double avgRent = StatisticsUtils.calculateAverage(rents);
        double medianRent = StatisticsUtils.calculateMedian(rents);
        double avgNoticePct = StatisticsUtils.calculateAverageNoticePct(noticePcts);
        
        // 시장 데이터 조회
        String neighborhood = tenants.get(0).getNeighborhood();
        String buildingName = scope.equals("building") ? tenants.get(0).getBuildingName() : null;
        MarketData marketData = realEstateApiService.analyzeMarketData(neighborhood, buildingName);
        
        return new Group(groupId, label, scope, tenants.size(), avgRent, medianRent, avgNoticePct, marketData);
    }
}