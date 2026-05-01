package com.cheobs.math_engine.adapter.output.jpa.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "processed_field", uniqueConstraints = {@UniqueConstraint(name = "uk_processed_field", columnNames = {"submission_id", "field_id"})})
public class ProcessedFieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "submission_id", nullable = false, updatable = false)
    private SubmissionEntity submission;

    @ManyToOne
    @JoinColumn(name = "field_id", nullable = false, updatable = false)
    private FieldEntity field;

    @Column(name = "value", nullable = false, precision = 19, scale = 6)
    private BigDecimal value;

    public ProcessedFieldEntity() {
    }

    public ProcessedFieldEntity(UUID id, SubmissionEntity submission, FieldEntity field, BigDecimal value) {
        this.id = id;
        this.submission = submission;
        this.field = field;
        this.value = value;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public SubmissionEntity getSubmission() {
        return submission;
    }

    public void setSubmission(SubmissionEntity submission) {
        this.submission = submission;
    }

    public FieldEntity getField() {
        return field;
    }

    public void setField(FieldEntity field) {
        this.field = field;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }
}

