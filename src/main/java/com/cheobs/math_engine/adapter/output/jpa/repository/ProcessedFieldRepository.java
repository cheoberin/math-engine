package com.cheobs.math_engine.adapter.output.jpa.repository;

import com.cheobs.math_engine.adapter.output.jpa.entity.ProcessedFieldEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProcessedFieldRepository extends JpaRepository<ProcessedFieldEntity, UUID> {

	@Query("""
				SELECT pf
				FROM ProcessedFieldEntity pf
				JOIN FETCH pf.field
				JOIN FETCH pf.submission s
				JOIN FETCH s.layout
				WHERE pf.submission.id = :id
			""")
	List<ProcessedFieldEntity> findBySubmissionIdWithField(@Param("id") UUID id);
}

