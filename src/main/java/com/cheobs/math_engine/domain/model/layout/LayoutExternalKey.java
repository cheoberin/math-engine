package com.cheobs.math_engine.domain.model.layout;

public record LayoutExternalKey(String value) {

    public LayoutExternalKey {
        if (value == null) {
            throw new LayoutValidationExcepiton("Layout external key cannot be null", "validation.layout.external-key.null");
        }

        value = value.trim().toUpperCase();

        if (value.isBlank()) {
            throw new LayoutValidationExcepiton("Layout external key cannot be blank", "validation.layout.external-key.blank");
        }

        if (value.length() > 4) {
            throw new LayoutValidationExcepiton("Layout external key must have at most 4 characters", "validation.layout.external-key.short");
        }

        if (!value.matches("[A-Z0-9]+")) {
            throw new LayoutValidationExcepiton("Layout external key must contain only letters and numbers", "validation.layout.external-key.characters");
        }
    }
}