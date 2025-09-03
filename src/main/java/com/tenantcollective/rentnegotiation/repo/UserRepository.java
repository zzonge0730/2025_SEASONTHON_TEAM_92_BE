package com.tenantcollective.rentnegotiation.repo;

import com.tenantcollective.rentnegotiation.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    List<User> findByNickname(String nickname);
    List<User> findAll();
    List<User> findByRole(String role);
    void deleteById(String id);
}
