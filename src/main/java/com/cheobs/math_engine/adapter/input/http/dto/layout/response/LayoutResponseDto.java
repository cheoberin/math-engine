package com.cheobs.math_engine.adapter.input.http.dto.layout.response;

import java.util.UUID;

public record LayoutResponseDto(
        UUID id,
        String externalKey,
        String name
) {
}
