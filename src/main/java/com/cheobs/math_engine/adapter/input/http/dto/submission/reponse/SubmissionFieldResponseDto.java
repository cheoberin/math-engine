package com.cheobs.math_engine.adapter.input.http.dto.submission.reponse;

import com.cheobs.math_engine.domain.model.field.FieldType;
import com.cheobs.math_engine.domain.model.submission.SubmissionDetailsField;

import java.math.BigDecimal;
import java.util.UUID;

public record SubmissionFieldResponseDto(
        UUID id,
        String fieldExternalKey,
        String description,
        BigDecimal value,
        FieldType fieldType
) {

    public SubmissionFieldResponseDto(SubmissionDetailsField field) {
        this(field.id(), field.fieldExternalKey(), field.description(), field.value(), field.fieldType());
    }
}
