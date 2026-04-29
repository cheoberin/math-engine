package com.cheobs.math_engine.domain.model.submission;

import com.cheobs.math_engine.domain.model.field.Field;

import java.math.BigDecimal;
import java.util.UUID;

public class SubmissionField {

    private UUID id;
    private Submission submission;
    private Field field;
    private BigDecimal value;

    public SubmissionField(Submission submission, Field field, BigDecimal value) {
        this.submission = submission;
        this.field = field;
        this.value = value;
    }
}
