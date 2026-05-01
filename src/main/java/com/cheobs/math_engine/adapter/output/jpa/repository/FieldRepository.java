package com.cheobs.math_engine.adapter.output.jpa.repository;

import com.cheobs.math_engine.adapter.output.jpa.entity.FieldEntity;
import com.cheobs.math_engine.domain.model.field.FieldSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FieldRepository extends JpaRepository<FieldEntity, UUID> {

    @Query("SELECT f FROM FieldEntity f WHERE f.layout.id = :layoutId AND f.externalKey = :externalKey")
    Optional<FieldEntity> findByLayoutIdAndExternalKey(@Param("layoutId") UUID layoutId, @Param("externalKey") String externalKey);

    @Query("SELECT f FROM FieldEntity f WHERE f.layout.id = :layoutId")
    List<FieldEntity> findByLayoutId(@Param("layoutId") UUID layoutId);

    @Query("SELECT f FROM FieldEntity f WHERE f.layout.id = :layoutId AND (f.externalKey ILIKE %:search% OR f.name ILIKE %:search%)")
    List<FieldEntity> findByLayoutIdAndSearch(@Param("layoutId") UUID layoutId, @Param("search") String search);

    List<FieldEntity> findByLayoutIdAndSource(UUID layoutId, FieldSource fieldSource);

}
