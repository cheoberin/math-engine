package com.cheobs.math_engine.adapter.output.jpa.repository;

import com.cheobs.math_engine.adapter.output.jpa.entity.SubmissionFieldEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SubmissionFieldRepository extends JpaRepository<SubmissionFieldEntity, UUID> {

    @Query("""
                SELECT sf
                FROM SubmissionFieldEntity sf
                JOIN FETCH sf.field
                WHERE sf.submission.id = :id
            """)
    List<SubmissionFieldEntity> findBySubmissionIdWithField(UUID id);

}

