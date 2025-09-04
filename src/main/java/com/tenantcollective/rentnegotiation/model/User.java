package com.tenantcollective.rentnegotiation.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import jakarta.validation.constraints.Email;
import java.time.LocalDateTime;

public class User {
    private String id;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    @NotBlank(message = "Nickname is required")
    @Size(min = 2, max = 50, message = "Nickname must be between 2 and 50 characters")
    private String nickname;
    
    @NotBlank(message = "Role is required")
    @Pattern(regexp = "^(tenant|landlord|anonymous|admin)$", message = "Role must be tenant, landlord, anonymous, or admin")
    private String role;
    
    // GPS location for verification
    private Double latitude;
    private Double longitude;
    private String address;
    private String neighborhood;
    private String buildingName;
    private Boolean profileCompleted = false;
    
    private Boolean active = true;

    // Constructors
    public User() {
        this.timestamp = LocalDateTime.now();
    }

    public User(String email, String password, String nickname, String role) {
        this();
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }

    public Boolean getProfileCompleted() {
        return profileCompleted;
    }

    public void setProfileCompleted(Boolean profileCompleted) {
        this.profileCompleted = profileCompleted;
    }
}
