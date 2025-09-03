package com.tenantcollective.rentnegotiation.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tenantcollective.rentnegotiation.model.AnonymousReport;
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
public class AnonymousReportRepositoryFile implements AnonymousReportRepository {
    
    private final String filePath;
    private final ObjectMapper objectMapper;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public AnonymousReportRepositoryFile(@Value("${anonymous.storage.file:storage/anonymous_reports.csv}") String filePath) {
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
                objectMapper.writeValue(new File(filePath), new ArrayList<AnonymousReport>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize anonymous report storage file", e);
        }
    }
    
    @Override
    public AnonymousReport save(AnonymousReport report) {
        lock.writeLock().lock();
        try {
            List<AnonymousReport> reports = loadReports();
            
            if (report.getId() == null || report.getId().isEmpty()) {
                report.setId(UUID.randomUUID().toString());
            }
            
            // Remove existing report with same ID
            reports.removeIf(r -> r.getId().equals(report.getId()));
            reports.add(report);
            
            saveReports(reports);
            return report;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public Optional<AnonymousReport> findById(String id) {
        lock.readLock().lock();
        try {
            return loadReports().stream()
                    .filter(report -> report.getId().equals(id))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public List<AnonymousReport> findAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(loadReports());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public List<AnonymousReport> findByBuildingName(String buildingName) {
        lock.readLock().lock();
        try {
            return loadReports().stream()
                    .filter(report -> report.getBuildingName().equalsIgnoreCase(buildingName))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public List<AnonymousReport> findByNeighborhood(String neighborhood) {
        lock.readLock().lock();
        try {
            return loadReports().stream()
                    .filter(report -> report.getNeighborhood() != null && 
                            report.getNeighborhood().equalsIgnoreCase(neighborhood))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void deleteById(String id) {
        lock.writeLock().lock();
        try {
            List<AnonymousReport> reports = loadReports();
            reports.removeIf(report -> report.getId().equals(id));
            saveReports(reports);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    private List<AnonymousReport> loadReports() {
        try {
            File file = new File(filePath);
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }
            
            AnonymousReport[] reports = objectMapper.readValue(file, AnonymousReport[].class);
            return new ArrayList<>(Arrays.asList(reports));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load anonymous reports from file", e);
        }
    }
    
    private void saveReports(List<AnonymousReport> reports) {
        try {
            objectMapper.writeValue(new File(filePath), reports);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save anonymous reports to file", e);
        }
    }
}
