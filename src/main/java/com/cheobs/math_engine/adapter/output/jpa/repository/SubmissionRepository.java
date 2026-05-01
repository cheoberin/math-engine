package com.cheobs.math_engine.adapter.output.jpa.repository;

import com.cheobs.math_engine.adapter.output.jpa.entity.SubmissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionEntity, UUID> {
}

