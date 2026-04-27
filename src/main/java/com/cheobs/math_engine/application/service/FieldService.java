package com.cheobs.math_engine.application.service;

import com.cheobs.math_engine.domain.model.field.Field;
import com.cheobs.math_engine.domain.model.field.FieldCommand;
import com.cheobs.math_engine.domain.port.input.FieldUseCase;
import com.cheobs.math_engine.domain.port.output.FieldPort;
import com.cheobs.math_engine.domain.port.output.LayoutPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class FieldService implements FieldUseCase {

    //TODO IMPLEMENT SERVICE
    private final LayoutPort layoutPort;
    private final FieldPort fieldPort;

    public FieldService(LayoutPort layoutPort, FieldPort fieldPort) {
        this.layoutPort = layoutPort;
        this.fieldPort = fieldPort;
    }

    @Override
    @Transactional
    public Field createField(FieldCommand command, UUID layoutId) {
        return null;
    }

    @Override
    @Transactional
    public Field updateField(UUID id, FieldCommand command) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public Field getField(UUID id) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Field> getFieldsByLayoutId(UUID layoutId, String search) {
        return List.of();
    }

    @Override
    @Transactional
    public void deleteField(UUID id) {

    }

    @Override
    @Transactional
    public void deleteFields(List<UUID> ids) {

    }
}
