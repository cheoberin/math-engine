package com.cheobs.math_engine.adapter.input.http.dto.layout.request;

import com.cheobs.math_engine.domain.model.layout.LayoutCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record LayoutRequestDto(

        @NotBlank
        @Length(max = 4)
        @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Layout external key must contain only letters and numbers")
        String externalKey,

        @NotBlank
        @Length(max = 255)
        String name
) {

    public LayoutCommand toDomain() {
        return new LayoutCommand(
                externalKey,
                name
        );
    }

}
