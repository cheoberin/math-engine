package com.cheobs.math_engine.adapter.input.http.api;

import com.cheobs.math_engine.adapter.input.http.dto.field.request.FieldRequestDto;
import com.cheobs.math_engine.adapter.input.http.dto.field.response.FieldDetailsResponseDto;
import com.cheobs.math_engine.adapter.input.http.dto.field.response.FieldResponseDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Tag(name = "Field", description = "Endpoints for Field Management")
@RequestMapping("/api/fields")
public interface FieldApi {

    @PostMapping
    ResponseEntity<FieldDetailsResponseDto> createField(@RequestBody @Valid FieldRequestDto fieldRequest);

    @PutMapping("/{id}")
    ResponseEntity<FieldDetailsResponseDto> updateField(@PathVariable("id") UUID id, @RequestBody @Valid FieldRequestDto fieldRequest);

    @GetMapping("/{id}")
    ResponseEntity<FieldDetailsResponseDto> getFieldById(@PathVariable("id") UUID id);

    @GetMapping("/layout/{layoutId}")
    ResponseEntity<List<FieldResponseDto>> getFieldsByLayout(@PathVariable("layoutId") UUID layoutId, @RequestParam(value = "search", required = false) String search);

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteFieldById(@PathVariable("id") UUID id);

    @DeleteMapping("/batch")
    ResponseEntity<Void> deleteFields(@RequestBody @NotEmpty List<UUID> ids);

}
