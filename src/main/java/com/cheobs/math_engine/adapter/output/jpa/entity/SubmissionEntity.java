package com.cheobs.math_engine.adapter.output.jpa.entity;

import com.cheobs.math_engine.domain.model.submission.SubmissionStatus;
import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "submission")
public class SubmissionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "layout_id", nullable = false, updatable = false)
    private LayoutEntity layout;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubmissionStatus status;

    @Column(name = "received_at", nullable = false, updatable = false)
    private OffsetDateTime receivedAt;

    public SubmissionEntity() {
    }

    public SubmissionEntity(UUID id, LayoutEntity layout, SubmissionStatus status, OffsetDateTime receivedAt) {
        this.id = id;
        this.layout = layout;
        this.status = status;
        this.receivedAt = receivedAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LayoutEntity getLayout() {
        return layout;
    }

    public void setLayout(LayoutEntity layout) {
        this.layout = layout;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public OffsetDateTime getReceivedAt() {
        return receivedAt;
    }

    public void setReceivedAt(OffsetDateTime receivedAt) {
        this.receivedAt = receivedAt;
    }
}

