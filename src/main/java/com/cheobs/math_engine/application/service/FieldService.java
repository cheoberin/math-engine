package com.cheobs.math_engine.application.service;

import com.cheobs.math_engine.domain.model.common.ConflictException;
import com.cheobs.math_engine.domain.model.common.NotFoundException;
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

    private static final String FIELD_NOT_FOUND_IDENTIFIER = "not-found.field";
    private static final String FIELD_NOT_FOUND_BY_ID_MESSAGE = "Field not found with id: ";
    private static final String LAYOUT_NOT_FOUND_IDENTIFIER = "not-found.layout";
    private static final String LAYOUT_NOT_FOUND_BY_ID_MESSAGE = "Layout not found with id: ";
    private static final String FIELD_EXTERNAL_KEY_CONFLICT_IDENTIFIER = "conflict.field.external-key.in-use";
    private static final String FIELD_EXTERNAL_KEY_CONFLICT_MESSAGE = "Field with external key already exists in layout: ";

    private final LayoutPort layoutPort;
    private final FieldPort fieldPort;

    public FieldService(LayoutPort layoutPort, FieldPort fieldPort) {
        this.layoutPort = layoutPort;
        this.fieldPort = fieldPort;
    }

    @Override
    @Transactional
    public Field createField(FieldCommand command, UUID layoutId) {
        var layout = layoutPort.getById(layoutId).orElseThrow(() -> new NotFoundException(LAYOUT_NOT_FOUND_BY_ID_MESSAGE + layoutId, LAYOUT_NOT_FOUND_IDENTIFIER));

        fieldPort.getByLayoutIdAndExternalKey(layoutId, command.externalKey()).ifPresent(existing -> {
            throw new ConflictException(FIELD_EXTERNAL_KEY_CONFLICT_MESSAGE + command.externalKey(), FIELD_EXTERNAL_KEY_CONFLICT_IDENTIFIER);
        });

        Field field = new Field(command, layout);
        return fieldPort.save(field);
    }

    @Override
    @Transactional
    public Field updateField(UUID id, FieldCommand command) {
        var field = getFieldOrThrow(id);
        var layoutId = field.getLayout().getId();

        fieldPort.getByLayoutIdAndExternalKey(layoutId, command.externalKey()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new ConflictException(FIELD_EXTERNAL_KEY_CONFLICT_MESSAGE + command.externalKey(), FIELD_EXTERNAL_KEY_CONFLICT_IDENTIFIER);
            }
        });

        field.updateDetails(command);
        return fieldPort.save(field);
    }

    @Override
    @Transactional(readOnly = true)
    public Field getField(UUID id) {
        return getFieldOrThrow(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Field> getFieldsByLayoutId(UUID layoutId, String search) {
        layoutPort.getById(layoutId).orElseThrow(() -> new NotFoundException(LAYOUT_NOT_FOUND_BY_ID_MESSAGE + layoutId, LAYOUT_NOT_FOUND_IDENTIFIER));

        search = (search == null || search.isBlank()) ? null : search;
        return fieldPort.getByLayoutIdAndSearch(layoutId, search);
    }

    @Override
    @Transactional
    public void deleteField(UUID id) {
        getFieldOrThrow(id);
        fieldPort.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteFields(List<UUID> ids) {
        ids.forEach(this::getFieldOrThrow);
        fieldPort.deleteByIds(ids);
    }

    private Field getFieldOrThrow(UUID id) {
        return fieldPort.getById(id).orElseThrow(() -> new NotFoundException(FIELD_NOT_FOUND_BY_ID_MESSAGE + id, FIELD_NOT_FOUND_IDENTIFIER));
    }
}
