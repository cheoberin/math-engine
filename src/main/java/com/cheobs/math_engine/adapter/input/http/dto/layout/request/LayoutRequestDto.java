package com.cheobs.math_engine.adapter.input.http.dto.layout.request;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record LayoutRequestDto(

        @NotBlank
        @Length(min = 1, max = 4)
        String externalKey,

        @NotBlank
        @Length(max = 255)
        String name
) {
}
