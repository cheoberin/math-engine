package com.cheobs.math_engine.domain.model.common.exceptions;

public class ValidationException extends RuntimeException {

    private final String identifier;

    public ValidationException(String message, String identifier) {
        super(message);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}

