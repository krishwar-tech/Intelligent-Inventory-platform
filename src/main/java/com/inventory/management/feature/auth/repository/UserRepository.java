package com.inventory.management.feature.auth.repository;

import java.util.Optional;

import com.inventory.management.feature.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
}