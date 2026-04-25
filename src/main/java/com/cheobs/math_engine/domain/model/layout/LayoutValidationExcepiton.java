package com.cheobs.math_engine.domain.model.layout;

public class LayoutValidationExcepiton extends RuntimeException {

    private final String identifier;

    public LayoutValidationExcepiton(String message, String identifier) {
        super(message);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

}
