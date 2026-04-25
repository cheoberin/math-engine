package com.cheobs.math_engine.adapter.input.http.dto.layout.request;

import jakarta.validation.constraints.NotBlank;

public record LayoutRequestDto(
        @NotBlank
        String externalKey,
        @NotBlank
        String name
) {
}
