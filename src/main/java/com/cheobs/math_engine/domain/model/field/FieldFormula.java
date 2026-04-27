package com.cheobs.math_engine.domain.model.field;

public record FieldFormula(String value) {

    public FieldFormula(String value) {
        this.value = normalize(value);
    }

    private static String normalize(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim();

        if (normalized.isEmpty()) {
            return null;
        }

        return normalized
                .toUpperCase()
                .replace(" ", "");
    }

}
