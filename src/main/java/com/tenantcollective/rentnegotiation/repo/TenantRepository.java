package com.tenantcollective.rentnegotiation.repo;

import com.tenantcollective.rentnegotiation.model.Tenant;
import java.util.List;
import java.util.Optional;

public interface TenantRepository {
    Tenant save(Tenant tenant);
    Optional<Tenant> findById(String id);
    List<Tenant> findAll();
    List<Tenant> findByBuildingNameAndStreetAddress(String buildingName, String streetAddress);
    List<Tenant> findByNeighborhood(String neighborhood);
    void deleteById(String id);
}