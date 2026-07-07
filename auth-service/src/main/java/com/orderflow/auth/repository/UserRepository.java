package com.orderflow.auth.repository;

import com.orderflow.auth.entitiy.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    // property is "id" on the User entity (not "userId")
    User findByIdAndActive(Long id, Boolean active);
}

