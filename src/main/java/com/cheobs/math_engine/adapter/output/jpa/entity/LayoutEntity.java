package com.cheobs.math_engine.adapter.output.jpa.entity;

import com.cheobs.math_engine.domain.model.layout.LayoutStatus;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "layout")
public class LayoutEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 4)
    private String externalKey;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LayoutStatus status;

    public LayoutEntity() {
    }

    public LayoutEntity(UUID id, String externalKey, String name, LayoutStatus status) {
        this.id = id;
        this.externalKey = externalKey;
        this.name = name;
        this.status = status;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getExternalKey() {
        return externalKey;
    }

    public void setExternalKey(String externalKey) {
        this.externalKey = externalKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LayoutStatus getStatus() {
        return status;
    }

    public void setStatus(LayoutStatus status) {
        this.status = status;
    }
}
