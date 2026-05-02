package com.cheobs.math_engine.domain.model.submission;

import com.cheobs.math_engine.domain.model.field.FieldType;

import java.math.BigDecimal;
import java.util.UUID;

public record SubmissionDetailsField(
        UUID id,
        String fieldExternalKey,
        String description,
        BigDecimal value,
        FieldType fieldType
) {
}

