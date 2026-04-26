package com.cheobs.math_engine.adapter.input.http.dto.layout.response;

import com.cheobs.math_engine.domain.model.layout.Layout;

import java.util.UUID;

public record LayoutResponseDto(UUID id, String externalKey, String name) {

    public LayoutResponseDto(Layout layout) {
        this(layout.getId(), layout.getExternalKey(), layout.getName());
    }

}
