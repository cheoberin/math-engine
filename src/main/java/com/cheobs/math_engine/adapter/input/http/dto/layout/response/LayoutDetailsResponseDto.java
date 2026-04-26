package com.cheobs.math_engine.adapter.input.http.dto.layout.response;

import com.cheobs.math_engine.domain.model.layout.Layout;
import com.cheobs.math_engine.domain.model.layout.LayoutStatus;

import java.util.UUID;

public record LayoutDetailsResponseDto(UUID id, String externalKey, String name, LayoutStatus status) {

    public LayoutDetailsResponseDto(Layout layout) {
        this(layout.getId(), layout.getExternalKey(), layout.getName(), layout.getStatus());
    }

}
