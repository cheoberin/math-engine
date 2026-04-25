package com.cheobs.math_engine.adapter.input.http.api;

import com.cheobs.math_engine.adapter.input.http.dto.layout.request.LayoutRequestDto;
import com.cheobs.math_engine.adapter.input.http.dto.layout.response.LayoutDetailsResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Layout", description = "Endpoints for Layout Management")
@RequestMapping("/api/layout")
public interface LayoutApi {

    @PostMapping
    @Operation(summary = "Create a new layout")
    ResponseEntity<LayoutDetailsResponseDto> createLayout(@Valid LayoutRequestDto layoutRequestDto);

}
