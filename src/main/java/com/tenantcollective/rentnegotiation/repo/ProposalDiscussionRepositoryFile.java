package com.tenantcollective.rentnegotiation.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tenantcollective.rentnegotiation.model.ProposalDiscussion;
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
public class ProposalDiscussionRepositoryFile implements ProposalDiscussionRepository {
    
    private final String filePath;
    private final ObjectMapper objectMapper;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public ProposalDiscussionRepositoryFile(@Value("${discussion.storage.file:storage/discussions.csv}") String filePath) {
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
                objectMapper.writeValue(new File(filePath), new ArrayList<ProposalDiscussion>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize discussion storage file", e);
        }
    }
    
    @Override
    public ProposalDiscussion save(ProposalDiscussion discussion) {
        lock.writeLock().lock();
        try {
            List<ProposalDiscussion> discussions = loadDiscussions();
            
            if (discussion.getId() == null || discussion.getId().isEmpty()) {
                discussion.setId(UUID.randomUUID().toString());
            }
            
            // Remove existing discussion with same ID
            discussions.removeIf(d -> d.getId().equals(discussion.getId()));
            discussions.add(discussion);
            
            saveDiscussions(discussions);
            return discussion;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public Optional<ProposalDiscussion> findById(String id) {
        lock.readLock().lock();
        try {
            return loadDiscussions().stream()
                    .filter(discussion -> discussion.getId().equals(id))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public List<ProposalDiscussion> findAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(loadDiscussions());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public List<ProposalDiscussion> findByProposalId(String proposalId) {
        lock.readLock().lock();
        try {
            return loadDiscussions().stream()
                    .filter(discussion -> discussion.getProposalId().equals(proposalId))
                    .sorted(Comparator.comparing(ProposalDiscussion::getTimestamp))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public List<ProposalDiscussion> findByAuthorId(String authorId) {
        lock.readLock().lock();
        try {
            return loadDiscussions().stream()
                    .filter(discussion -> discussion.getAuthorId().equals(authorId))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public List<ProposalDiscussion> findByParentId(String parentId) {
        lock.readLock().lock();
        try {
            return loadDiscussions().stream()
                    .filter(discussion -> parentId.equals(discussion.getParentId()))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void deleteById(String id) {
        lock.writeLock().lock();
        try {
            List<ProposalDiscussion> discussions = loadDiscussions();
            discussions.removeIf(discussion -> discussion.getId().equals(id));
            saveDiscussions(discussions);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    private List<ProposalDiscussion> loadDiscussions() {
        try {
            File file = new File(filePath);
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }
            
            ProposalDiscussion[] discussions = objectMapper.readValue(file, ProposalDiscussion[].class);
            return new ArrayList<>(Arrays.asList(discussions));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load discussions from file", e);
        }
    }
    
    private void saveDiscussions(List<ProposalDiscussion> discussions) {
        try {
            objectMapper.writeValue(new File(filePath), discussions);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save discussions to file", e);
        }
    }
}
