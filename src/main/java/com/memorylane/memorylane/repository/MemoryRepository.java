package com.memorylane.memorylane.repository;

import com.memorylane.memorylane.model.Memory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MemoryRepository extends JpaRepository<Memory, Long> {
    List<Memory> findAllByOrderByCreatedAtDesc();

    List<Memory> findByTitleContainingIgnoreCaseOrTextContainingIgnoreCaseOrderByCreatedAtDesc(
            String title, String text);

    List<Memory> findByTagsContainingIgnoreCaseOrderByCreatedAtDesc(String tag);

    @org.springframework.data.jpa.repository.Query(
            "SELECT m FROM Memory m WHERE FUNCTION('MONTH', m.createdAt) = :month " +
                    "AND FUNCTION('DAY', m.createdAt) = :day AND FUNCTION('YEAR', m.createdAt) <> :currentYear " +
                    "ORDER BY m.createdAt DESC"
    )
    List<Memory> findOnThisDay(int month, int day, int currentYear);
}