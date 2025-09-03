package com.tenantcollective.rentnegotiation.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tenantcollective.rentnegotiation.model.Vote;
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
public class VoteRepositoryFile implements VoteRepository {
    
    private final String filePath;
    private final ObjectMapper objectMapper;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public VoteRepositoryFile(@Value("${vote.storage.file:storage/votes.csv}") String filePath) {
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
                objectMapper.writeValue(new File(filePath), new ArrayList<Vote>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize vote storage file", e);
        }
    }
    
    @Override
    public Vote save(Vote vote) {
        lock.writeLock().lock();
        try {
            List<Vote> votes = loadVotes();
            
            if (vote.getId() == null || vote.getId().isEmpty()) {
                vote.setId(UUID.randomUUID().toString());
            }
            
            // Remove existing vote with same ID
            votes.removeIf(v -> v.getId().equals(vote.getId()));
            votes.add(vote);
            
            saveVotes(votes);
            return vote;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public Optional<Vote> findById(String id) {
        lock.readLock().lock();
        try {
            return loadVotes().stream()
                    .filter(vote -> vote.getId().equals(id))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public List<Vote> findAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(loadVotes());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public List<Vote> findByProposalId(String proposalId) {
        lock.readLock().lock();
        try {
            return loadVotes().stream()
                    .filter(vote -> vote.getProposalId().equals(proposalId))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public List<Vote> findByUserId(String userId) {
        lock.readLock().lock();
        try {
            return loadVotes().stream()
                    .filter(vote -> vote.getUserId().equals(userId))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public Optional<Vote> findByProposalIdAndUserId(String proposalId, String userId) {
        lock.readLock().lock();
        try {
            return loadVotes().stream()
                    .filter(vote -> vote.getProposalId().equals(proposalId) && 
                            vote.getUserId().equals(userId))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void deleteById(String id) {
        lock.writeLock().lock();
        try {
            List<Vote> votes = loadVotes();
            votes.removeIf(vote -> vote.getId().equals(id));
            saveVotes(votes);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    private List<Vote> loadVotes() {
        try {
            File file = new File(filePath);
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }
            
            Vote[] votes = objectMapper.readValue(file, Vote[].class);
            return new ArrayList<>(Arrays.asList(votes));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load votes from file", e);
        }
    }
    
    private void saveVotes(List<Vote> votes) {
        try {
            objectMapper.writeValue(new File(filePath), votes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save votes to file", e);
        }
    }
}
