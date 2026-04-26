package com.cheobs.math_engine.adapter.input.http.api;

import com.cheobs.math_engine.adapter.input.http.dto.layout.request.LayoutRequestDto;
import com.cheobs.math_engine.adapter.input.http.dto.layout.response.LayoutDetailsResponseDto;
import com.cheobs.math_engine.adapter.input.http.dto.layout.response.LayoutResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Layout", description = "Endpoints for Layout Management")
@RequestMapping("/api/layouts")
public interface LayoutApi {

    @PostMapping
    @Operation(summary = "Create a new layout")
    ResponseEntity<LayoutDetailsResponseDto> createLayout(@RequestBody @Valid LayoutRequestDto layoutRequestDto);

    @PutMapping("/{id}")
    @Operation(summary = "Update layout information")
    ResponseEntity<LayoutDetailsResponseDto> updateLayout(@PathVariable("id") UUID id, @RequestBody @Valid LayoutRequestDto layoutRequestDto);

    @GetMapping("/{id}")
    @Operation(summary = "Get layout by id")
    ResponseEntity<LayoutDetailsResponseDto> getLayout(@PathVariable("id") UUID id);

    @GetMapping
    @Operation(summary = "Get all layouts")
    ResponseEntity<List<LayoutResponseDto>> getAllLayouts(@RequestParam(value = "search", required = false) String search);

    @PatchMapping("/{id}/activate")
    @Operation(summary = "Activate a layout")
    ResponseEntity<Void> activateLayout(@PathVariable("id") UUID id);

    @PatchMapping("/{id}/deactivate")
    @Operation(summary = "Deactivate a layout")
    ResponseEntity<Void> deactivateLayout(@PathVariable("id") UUID id);

}
