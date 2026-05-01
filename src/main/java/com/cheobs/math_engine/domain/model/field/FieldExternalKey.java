package com.cheobs.math_engine.domain.model.field;

import com.cheobs.math_engine.domain.model.common.exceptions.ValidationException;

public record FieldExternalKey(
        String value
) {

    public FieldExternalKey {
        if (value == null) {
            throw new ValidationException("Field external key cannot be null", "validation.field.external-key.null");
        }

        value = value.trim().toUpperCase();

        if (value.isBlank()) {
            throw new ValidationException("Field external key cannot be blank", "validation.field.external-key.blank");
        }

        if (value.length() > 7) {
            throw new ValidationException("Field external key must have at most 7 characters", "validation.field.external-key.short");
        }

        if (!value.matches("[A-Z0-9]+")) {
            throw new ValidationException("Field external key must contain only letters and numbers", "validation.field.external-key.characters");
        }
    }

}
