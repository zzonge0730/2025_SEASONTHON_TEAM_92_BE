package com.tenantcollective.rentnegotiation.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tenantcollective.rentnegotiation.model.User;
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
public class UserRepositoryFile implements UserRepository {
    
    private final String filePath;
    private final ObjectMapper objectMapper;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    
    public UserRepositoryFile(@Value("${user.storage.file:storage/users.json}") String filePath) {
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
                objectMapper.writeValue(new File(filePath), new ArrayList<User>());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize user storage file", e);
        }
    }
    
    @Override
    public User save(User user) {
        lock.writeLock().lock();
        try {
            List<User> users = loadUsers();
            
            if (user.getId() == null || user.getId().isEmpty()) {
                user.setId(UUID.randomUUID().toString());
            }
            
            // Remove existing user with same ID
            users.removeIf(u -> u.getId().equals(user.getId()));
            users.add(user);
            
            saveUsers(users);
            return user;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    @Override
    public Optional<User> findById(String id) {
        lock.readLock().lock();
        try {
            return loadUsers().stream()
                    .filter(user -> user.getId().equals(id))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        lock.readLock().lock();
        try {
            return loadUsers().stream()
                    .filter(user -> user.getEmail() != null && user.getEmail().equalsIgnoreCase(email))
                    .findFirst();
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public List<User> findAll() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(loadUsers());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public List<User> findByNickname(String nickname) {
        lock.readLock().lock();
        try {
            return loadUsers().stream()
                    .filter(user -> user.getNickname().equals(nickname))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public List<User> findByRole(String role) {
        lock.readLock().lock();
        try {
            return loadUsers().stream()
                    .filter(user -> user.getRole().equals(role))
                    .collect(Collectors.toList());
        } finally {
            lock.readLock().unlock();
        }
    }
    
    @Override
    public void deleteById(String id) {
        lock.writeLock().lock();
        try {
            List<User> users = loadUsers();
            users.removeIf(user -> user.getId().equals(id));
            saveUsers(users);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    private List<User> loadUsers() {
        try {
            File file = new File(filePath);
            if (!file.exists() || file.length() == 0) {
                return new ArrayList<>();
            }
            
            User[] users = objectMapper.readValue(file, User[].class);
            return new ArrayList<>(Arrays.asList(users));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load users from file", e);
        }
    }
    
    private void saveUsers(List<User> users) {
        try {
            objectMapper.writeValue(new File(filePath), users);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save users to file", e);
        }
    }
}
