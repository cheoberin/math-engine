package com.cheobs.math_engine.adapter.output.jpa.repository;

import com.cheobs.math_engine.adapter.output.jpa.entity.SubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity, UUID> {

    @Query("""
                SELECT s
                FROM SubmissionEntity s
                JOIN FETCH s.layout
                WHERE s.id = :id
            """)
    java.util.Optional<SubmissionEntity> findByIdWithLayout(@Param("id") UUID id);

    @Query(value = """
                SELECT *
                FROM submission s
                WHERE s.status = 'PENDING'
                ORDER BY s.received_at ASC
                LIMIT :quantity
            """, nativeQuery = true)
    List<SubmissionEntity> findPendingSubmissions(@Param("quantity") Integer quantity);

}

