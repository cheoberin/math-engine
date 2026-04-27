package com.cheobs.math_engine.adapter.input.http.controller;

import com.cheobs.math_engine.adapter.input.http.api.FieldApi;
import com.cheobs.math_engine.adapter.input.http.dto.field.request.FieldRequestDto;
import com.cheobs.math_engine.adapter.input.http.dto.field.response.FieldDetailsResponseDto;
import com.cheobs.math_engine.adapter.input.http.dto.field.response.FieldResponseDto;
import com.cheobs.math_engine.domain.model.field.FieldCommand;
import com.cheobs.math_engine.domain.port.input.FieldUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
public class FieldController implements FieldApi {

    private final FieldUseCase fieldUseCase;

    public FieldController(FieldUseCase fieldUseCase) {
        this.fieldUseCase = fieldUseCase;
    }

    @Override
    public ResponseEntity<FieldDetailsResponseDto> createField(FieldRequestDto fieldRequest) {
        FieldCommand command = fieldRequest.toDomain();
        var domain = fieldUseCase.createField(command, fieldRequest.layout());
        var response = new FieldDetailsResponseDto(domain);
        URI location = URI.create("/api/fields/" + domain.getId());
        return ResponseEntity.created(location).body(response);
    }

    @Override
    public ResponseEntity<FieldDetailsResponseDto> updateField(UUID id, FieldRequestDto fieldRequest) {
        FieldCommand command = fieldRequest.toDomain();
        var domain = fieldUseCase.updateField(id, command);
        var response = new FieldDetailsResponseDto(domain);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<FieldDetailsResponseDto> getFieldById(UUID id) {
        var domain = fieldUseCase.getField(id);
        var response = new FieldDetailsResponseDto(domain);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<FieldResponseDto>> getFieldsByLayout(UUID layoutId, String search) {
        var domain = fieldUseCase.getFieldsByLayoutId(layoutId, search);
        var response = domain.stream().map(FieldResponseDto::new).toList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteFieldById(UUID id) {
        fieldUseCase.deleteField(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<Void> deleteFields(List<UUID> ids) {
        fieldUseCase.deleteFields(ids);
        return ResponseEntity.noContent().build();
    }
}
