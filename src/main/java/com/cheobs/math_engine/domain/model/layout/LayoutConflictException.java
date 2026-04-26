package com.cheobs.math_engine.domain.model.layout;

public class LayoutConflictException extends RuntimeException {

    private final String identifier;

    public LayoutConflictException(String message, String identifier) {
        super(message);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }
}
