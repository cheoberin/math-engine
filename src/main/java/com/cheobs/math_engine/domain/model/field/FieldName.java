package com.cheobs.math_engine.domain.model.field;

import com.cheobs.math_engine.domain.model.common.exceptions.ValidationException;

public record FieldName(String value) {

    public FieldName {
        if (value == null) {
            throw new ValidationException("Field external key cannot be null", "validation.field.external-key.null");
        }

        value = value.trim();

        if (value.isBlank()) {
            throw new ValidationException("Field external key cannot be blank", "validation.field.external-key.blank");
        }

        if (value.length() > 255) {
            throw new ValidationException("Field external key must have at most 7 characters", "validation.field.external-key.short");
        }

    }

}
