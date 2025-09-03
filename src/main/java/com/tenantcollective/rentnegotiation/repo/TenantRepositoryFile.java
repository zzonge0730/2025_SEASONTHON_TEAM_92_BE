package com.tenantcollective.rentnegotiation.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tenantcollective.rentnegotiation.model.Tenant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

@Repository
public class TenantRepositoryFile implements TenantRepository {
    
    private final String filePath;
    private final ObjectMapper objectMapper;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public TenantRepositoryFile(@Value("${tenant.storage.file}") String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        initializeFile();
    }
    
    private void initializeFile() {
        try {
            Path path = Paths.get(filePath);
            Path parentDir = path.getParent();
            if (parentDir != null && !Files.exists(parentDir)) {
                Files.createDirectories(parentDir);
            }
            if (!Files.exists(path)) {
                Files.createFile(path);
                // Write empty array to start
                objectMapper.writeValue(new File(filePath), new ArrayList<Tenant>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize tenant storage file", e);
        }
    }
    
    @Override
    public Tenant save(Tenant tenant) {
        lock.writeLock().lock();
        try {
            List<Tenant> tenants = loadAllTenants();
            
            if (tenant.getId() == null) {
                tenant.setId(UUID.randomUUID().toString());
            }
            
            // Remove existing tenant with same ID if it exists
            tenants.removeIf(t -> t.getId().equals(tenant.getId()));
            tenants.add(tenant);
            
            saveAllTenants(tenants);
            return tenant;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public Optional<Tenant> findById(String id) {
        lock.readLock().lock();
        try {
            return loadAllTenants().stream()
                    .filter(tenant -> tenant.getId().equals(id))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public List<Tenant> findAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(loadAllTenants());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public List<Tenant> findByBuildingNameAndStreetAddress(String buildingName, String streetAddress) {
        lock.readLock().lock();
        try {
            return loadAllTenants().stream()
                    .filter(tenant -> tenant.getBuildingName().equals(buildingName) && 
                                    tenant.getStreetAddress().equals(streetAddress))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public List<Tenant> findByNeighborhood(String neighborhood) {
        lock.readLock().lock();
        try {
            return loadAllTenants().stream()
                    .filter(tenant -> tenant.getNeighborhood().equals(neighborhood))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void deleteById(String id) {
        lock.writeLock().lock();
        try {
            List<Tenant> tenants = loadAllTenants();
            tenants.removeIf(tenant -> tenant.getId().equals(id));
            saveAllTenants(tenants);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    private List<Tenant> loadAllTenants() {
        try {
            File file = new File(filePath);
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }
            
            Tenant[] tenants = objectMapper.readValue(file, Tenant[].class);
            return new ArrayList<>(Arrays.asList(tenants));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load tenants from file", e);
        }
    }
    
    private void saveAllTenants(List<Tenant> tenants) {
        try {
            objectMapper.writeValue(new File(filePath), tenants);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save tenants to file", e);
        }
    }
}