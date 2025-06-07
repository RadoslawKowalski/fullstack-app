package com.example.pasir_kowalski_radoslaw.repository;

import com.example.pasir_kowalski_radoslaw.model.Debt;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DebtRepository extends JpaRepository<Debt, Long> {
    List<Debt> findByGroupId(Long groupId);
} 