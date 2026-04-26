package com.cheobs.math_engine.adapter.input.http.controller;

import com.cheobs.math_engine.adapter.input.http.api.LayoutApi;
import com.cheobs.math_engine.adapter.input.http.dto.layout.request.LayoutRequestDto;
import com.cheobs.math_engine.adapter.input.http.dto.layout.response.LayoutDetailsResponseDto;
import com.cheobs.math_engine.adapter.input.http.dto.layout.response.LayoutResponseDto;
import com.cheobs.math_engine.domain.model.layout.LayoutCommand;
import com.cheobs.math_engine.domain.port.input.LayoutUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
public class LayoutController implements LayoutApi {

    private final LayoutUseCase layoutUseCase;

    public LayoutController(LayoutUseCase layoutUseCase) {
        this.layoutUseCase = layoutUseCase;
    }

    @Override
    public ResponseEntity<LayoutDetailsResponseDto> createLayout(LayoutRequestDto layoutRequestDto) {
        LayoutCommand command = new LayoutCommand(layoutRequestDto.externalKey(), layoutRequestDto.name());
        var domain = layoutUseCase.createLayout(command);
        var response = new LayoutDetailsResponseDto(domain);
        URI location = URI.create("/api/layouts/" + domain.getId());
        return ResponseEntity.created(location).body(response);
    }

    @Override
    public ResponseEntity<LayoutDetailsResponseDto> updateLayout(UUID id, LayoutRequestDto layoutRequestDto) {
        LayoutCommand command = new LayoutCommand(layoutRequestDto.externalKey(), layoutRequestDto.name());
        var domain = layoutUseCase.updateLayout(id, command);
        var response = new LayoutDetailsResponseDto(domain);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<LayoutDetailsResponseDto> getLayout(UUID id) {
        var domain = layoutUseCase.findLayout(id);
        var response = new LayoutDetailsResponseDto(domain);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<LayoutResponseDto>> getAllLayouts(String search) {
        var domain = layoutUseCase.findLayouts(search);
        var response = domain.stream().map(LayoutResponseDto::new).toList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> activateLayout(UUID id) {
        layoutUseCase.activateLayout(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deactivateLayout(UUID id) {
        layoutUseCase.deactivateLayout(id);
        return ResponseEntity.noContent().build();
    }
}
