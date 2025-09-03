package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.model.Tenant;
import com.tenantcollective.rentnegotiation.repo.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TenantService {
    
    private final TenantRepository tenantRepository;
    
    @Autowired
    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }
    
    public Tenant saveTenant(Tenant tenant) {
        return tenantRepository.save(tenant);
    }
    
    public Optional<Tenant> findTenantById(String id) {
        return tenantRepository.findById(id);
    }
    
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }
    
    public List<Tenant> getTenantsByBuilding(String buildingName, String streetAddress) {
        return tenantRepository.findByBuildingNameAndStreetAddress(buildingName, streetAddress);
    }
    
    public List<Tenant> getTenantsByNeighborhood(String neighborhood) {
        return tenantRepository.findByNeighborhood(neighborhood);
    }
}