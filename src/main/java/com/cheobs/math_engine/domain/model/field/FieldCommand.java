package com.cheobs.math_engine.domain.model.field;

public record FieldCommand(
        String externalKey,
        String name,
        FieldSource source,
        String formula,
        FieldType fieldType
) {
}
