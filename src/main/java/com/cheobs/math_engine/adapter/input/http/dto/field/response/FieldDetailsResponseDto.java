package com.cheobs.math_engine.adapter.input.http.dto.field.response;

import com.cheobs.math_engine.domain.model.field.Field;
import com.cheobs.math_engine.domain.model.field.FieldSource;
import com.cheobs.math_engine.domain.model.field.FieldType;

import java.util.UUID;

public record FieldDetailsResponseDto(
        UUID id,
        String externalKey,
        UUID layoutId,
        String layoutExternalKey,
        String layoutName,
        FieldSource source,
        String formula,
        FieldType fieldType,
        Integer calculationOrder
) {

    public FieldDetailsResponseDto(Field field) {
        this(
                field.getId(),
                field.getExternalKey(),
                field.getLayout().getId(),
                field.getLayout().getExternalKey(),
                field.getLayout().getName(),
                field.getSource(),
                field.getFormula(),
                field.getFieldType(),
                field.getCalculationOrder()
        );
    }

}