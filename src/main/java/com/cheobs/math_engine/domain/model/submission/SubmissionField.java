package com.cheobs.math_engine.domain.model.submission;

import com.cheobs.math_engine.domain.model.field.Field;

import java.math.BigDecimal;
import java.util.UUID;

public class SubmissionField {

    private UUID id;
    private final Submission submission;
    private final Field field;
    private final BigDecimal value;

    public SubmissionField(Submission submission, Field field, BigDecimal value) {
        this.submission = submission;
        this.field = field;
        this.value = value;
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
