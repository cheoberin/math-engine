package com.cheobs.math_engine.adapter.output.jpa.repository;


import com.cheobs.math_engine.adapter.output.jpa.entity.LayoutEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LayoutRepository extends JpaRepository<LayoutEntity, UUID> {

    Optional<LayoutEntity> findByExternalKey(String externalKey);

    List<LayoutEntity> findByExternalKeyContainingIgnoreCaseOrNameContainingIgnoreCase(
            String externalKey, String name
    );

}
