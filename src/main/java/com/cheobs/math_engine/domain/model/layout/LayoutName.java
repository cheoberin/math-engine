package com.cheobs.math_engine.domain.model.layout;

import com.cheobs.math_engine.domain.model.common.ValidationException;

public record LayoutName(String value) {

    public LayoutName {

        if (value == null) {
            throw new ValidationException("Layout name cannot be null", "validation.layout.name.null");
        }

        value = value.trim();

        if (value.isBlank()) {
            throw new ValidationException("Layout name cannot be blank", "validation.layout.name.blank");
        }

        if (value.length() > 255) {
            throw new ValidationException("Layout name is too long", "validation.layout.name.short");
        }

    }

}
