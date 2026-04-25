package com.cheobs.math_engine.adapter.output.jpa.entity;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "layout")
public class LayoutEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false,  length = 4)
    private String externalKey;

    @Column(nullable = false)
    private String name;

    public LayoutEntity() {
    }

    public LayoutEntity(UUID id, String externalKey, String name) {
        this.id = id;
        this.externalKey = externalKey;
        this.name = name;
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
}
