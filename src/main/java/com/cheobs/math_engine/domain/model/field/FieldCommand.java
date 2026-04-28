package com.cheobs.math_engine.domain.model.field;

public record FieldCommand(
        String externalKey,
        FieldSource source,
        String formula,
        FieldType fieldType
) {
}
