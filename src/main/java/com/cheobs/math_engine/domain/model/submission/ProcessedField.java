package com.cheobs.math_engine.domain.model.submission;

import com.cheobs.math_engine.domain.model.field.Field;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

public class ProcessedField {

    private UUID id;
    private final Submission submission;
    private final Field field;
    private final BigDecimal value;

    public ProcessedField(Submission submission, Field field, BigDecimal value) {
        this.submission = Objects.requireNonNull(submission, "Submission cannot be null");
        this.field = Objects.requireNonNull(field, "Field cannot be null");
        this.value = Objects.requireNonNull(value, "Value cannot be null");
    }

    public UUID getId() {
        return id;
    }

    public Submission getSubmission() {
        return submission;
    }

    public Field getField() {
        return field;
    }

    public BigDecimal getValue() {
        return value;
    }

}
