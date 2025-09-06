package com.tenantcollective.rentnegotiation.service;

import com.tenantcollective.rentnegotiation.model.User;
import com.tenantcollective.rentnegotiation.model.UserUpdateRequest;
import com.tenantcollective.rentnegotiation.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    public User saveUser(User user) {
        // GPS 기반 간단 인증에서는 이메일 중복 체크 생략
        // 닉네임과 위치만으로 사용자 구분
        return userRepository.save(user);
    }
    
    public User registerUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already in use: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(String id) {
        return userRepository.findById(id);
    }

    public User updateUserAddress(String userId, String address, String neighborhood, String buildingName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        user.setAddress(address);
        user.setNeighborhood(neighborhood);
        user.setBuildingName(buildingName);
        // In a real app, you might set a flag like isLocationVerified = true
        return userRepository.save(user);
    }
    
    public User updateUser(User user) {
        User existingUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + user.getId()));
        
        // Update fields that are allowed to be updated
        if (user.getNickname() != null) {
            existingUser.setNickname(user.getNickname());
        }
        if (user.getAddress() != null) {
            existingUser.setAddress(user.getAddress());
        }
        if (user.getNeighborhood() != null) {
            existingUser.setNeighborhood(user.getNeighborhood());
        }
        if (user.getBuildingName() != null) {
            existingUser.setBuildingName(user.getBuildingName());
        }
        if (user.getLatitude() != null) {
            existingUser.setLatitude(user.getLatitude());
        }
        if (user.getLongitude() != null) {
            existingUser.setLongitude(user.getLongitude());
        }
        if (user.getProfileCompleted() != null) {
            existingUser.setProfileCompleted(user.getProfileCompleted());
        }
        if (user.getDiagnosisCompleted() != null) {
            existingUser.setDiagnosisCompleted(user.getDiagnosisCompleted());
        }
        if (user.getOnboardingCompleted() != null) {
            existingUser.setOnboardingCompleted(user.getOnboardingCompleted());
        }
        if (user.getActive() != null) {
            existingUser.setActive(user.getActive());
        }
        
        return userRepository.save(existingUser);
    }
    
    public User updateUser(UserUpdateRequest userUpdateRequest) {
        User existingUser = userRepository.findById(userUpdateRequest.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userUpdateRequest.getId()));
        
        // Update fields that are allowed to be updated
        if (userUpdateRequest.getNickname() != null) {
            existingUser.setNickname(userUpdateRequest.getNickname());
        }
        if (userUpdateRequest.getAddress() != null) {
            existingUser.setAddress(userUpdateRequest.getAddress());
        }
        if (userUpdateRequest.getNeighborhood() != null) {
            existingUser.setNeighborhood(userUpdateRequest.getNeighborhood());
        }
        if (userUpdateRequest.getBuildingName() != null) {
            existingUser.setBuildingName(userUpdateRequest.getBuildingName());
        }
        if (userUpdateRequest.getLatitude() != null) {
            existingUser.setLatitude(userUpdateRequest.getLatitude());
        }
        if (userUpdateRequest.getLongitude() != null) {
            existingUser.setLongitude(userUpdateRequest.getLongitude());
        }
        if (userUpdateRequest.getProfileCompleted() != null) {
            existingUser.setProfileCompleted(userUpdateRequest.getProfileCompleted());
        }
        if (userUpdateRequest.getDiagnosisCompleted() != null) {
            existingUser.setDiagnosisCompleted(userUpdateRequest.getDiagnosisCompleted());
        }
        if (userUpdateRequest.getOnboardingCompleted() != null) {
            existingUser.setOnboardingCompleted(userUpdateRequest.getOnboardingCompleted());
        }
        if (userUpdateRequest.getActive() != null) {
            existingUser.setActive(userUpdateRequest.getActive());
        }
        
        // Only update password if provided
        if (userUpdateRequest.getPassword() != null && !userUpdateRequest.getPassword().trim().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(userUpdateRequest.getPassword()));
        }
        
        return userRepository.save(existingUser);
    }
    
    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }
    
    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }
    
    public boolean authenticateUser(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.isPresent() && user.get().getPassword().equals(password) && user.get().getActive();
    }
    
    public User findUserByNicknameAndLocation(String nickname, Double latitude, Double longitude) {
        List<User> users = userRepository.findByNickname(nickname);
        
        // GPS 좌표가 비슷한 사용자 찾기 (약 100m 반경)
        for (User user : users) {
            if (user.getLatitude() != null && user.getLongitude() != null) {
                double distance = calculateDistance(latitude, longitude, user.getLatitude(), user.getLongitude());
                if (distance <= 0.1) { // 100m 이내
                    return user;
                }
            }
        }
        return null;
    }
    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // 지구 반지름 (km)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // km 단위
    }
}