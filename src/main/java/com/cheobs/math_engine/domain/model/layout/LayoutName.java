package com.cheobs.math_engine.domain.model.layout;

public record LayoutName(String value) {

    public LayoutName {

        if (value == null) {
            throw new LayoutValidationExcepiton("Layout name cannot be null", "validation.layout.name.null");
        }

        value = value.trim();

        if (value.isBlank()) {
            throw new LayoutValidationExcepiton("Layout name cannot be blank", "validation.layout.name.blank");
        }

        if (value.length() > 255) {
            throw new LayoutValidationExcepiton("Layout name is too long", "validation.layout.name.short");
        }

    }

}
