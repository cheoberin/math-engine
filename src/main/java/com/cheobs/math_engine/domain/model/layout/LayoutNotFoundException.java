package com.cheobs.math_engine.domain.model.layout;

public class LayoutNotFoundException extends RuntimeException {

    private final String identifier;

    public LayoutNotFoundException(String message, String identifier) {
        super(message);
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return identifier;
    }

}
