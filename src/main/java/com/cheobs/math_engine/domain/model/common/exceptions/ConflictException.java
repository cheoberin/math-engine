package com.cheobs.math_engine.domain.model.common.exceptions;

public class ConflictException extends RuntimeException {

    private final String identifier;

    public ConflictException(String message, String identifier) {
        super(message);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

}
