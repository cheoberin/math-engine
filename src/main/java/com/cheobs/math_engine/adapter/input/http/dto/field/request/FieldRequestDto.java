package com.cheobs.math_engine.adapter.input.http.dto.field.request;

import com.cheobs.math_engine.domain.model.field.FieldCommand;
import com.cheobs.math_engine.domain.model.field.FieldSource;
import com.cheobs.math_engine.domain.model.field.FieldType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

import java.util.UUID;

public record FieldRequestDto(
        @NotBlank
        @Length(max = 7)
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Field external key must contain only letters and numbers")
        String externalKey,
        @NotBlank
        @Length(max = 255)
        String name,
        @NotNull
        UUID layout,
        @NotNull
        FieldSource source,
        String formula,
        @NotNull
        FieldType fieldType
) {

    public FieldCommand toDomain() {
        return new FieldCommand(
                externalKey,
                name,
                source,
                formula,
                fieldType
        );
    }

}
