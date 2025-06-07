package com.example.pasir_kowalski_radoslaw.repository;

import com.example.pasir_kowalski_radoslaw.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
