package com.cheobs.math_engine.adapter.input.http.dto.field.response;

import com.cheobs.math_engine.domain.model.field.Field;

import java.util.UUID;

public record FieldResponseDto(
        UUID id,
        String externalKey,
        UUID layoutId,
        String layoutExternalKey,
        String layoutName
) {

    public FieldResponseDto(
            Field field
    ) {
        this(
                field.getId(),
                field.getExternalKey(),
                field.getLayout().getId(),
                field.getLayout().getExternalKey(),
                field.getLayout().getName());
    }

}
