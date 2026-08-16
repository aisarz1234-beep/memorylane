package com.memorylane.memorylane.repository;

import com.memorylane.memorylane.model.Memory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MemoryRepository extends JpaRepository<Memory, Long> {
    List<Memory> findAllByOrderByCreatedAtDesc();
}