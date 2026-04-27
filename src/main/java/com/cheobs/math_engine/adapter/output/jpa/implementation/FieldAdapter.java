package com.cheobs.math_engine.adapter.output.jpa.implementation;

import com.cheobs.math_engine.adapter.output.jpa.entity.FieldEntity;
import com.cheobs.math_engine.adapter.output.jpa.entity.LayoutEntity;
import com.cheobs.math_engine.adapter.output.jpa.repository.FieldRepository;
import com.cheobs.math_engine.domain.model.field.Field;
import com.cheobs.math_engine.domain.model.layout.Layout;
import com.cheobs.math_engine.domain.port.output.FieldPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class FieldAdapter implements FieldPort {

    private final FieldRepository fieldRepository;

    public FieldAdapter(FieldRepository fieldRepository) {
        this.fieldRepository = fieldRepository;
    }

    @Override
    public Field save(Field field) {
        FieldEntity entity = toEntity(field);
        entity = fieldRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public Optional<Field> getById(UUID id) {
        return fieldRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Field> getByLayoutIdAndExternalKey(UUID layoutId, String externalKey) {
        return fieldRepository.findByLayoutIdAndExternalKey(layoutId, externalKey)
                .map(this::toDomain);
    }

    @Override
    public List<Field> getByLayoutIdAndSearch(UUID layoutId, String search) {
        List<FieldEntity> entities;
        if (search == null || search.isBlank()) {
            entities = fieldRepository.findByLayoutId(layoutId);
        } else {
            entities = fieldRepository.findByLayoutIdAndSearch(layoutId, search);
        }

        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID id) {
        fieldRepository.deleteById(id);
    }

    @Override
    public void deleteByIds(List<UUID> ids) {
        fieldRepository.deleteAllByIdInBatch(ids);
    }

    private FieldEntity toEntity(Field field) {
        LayoutEntity layoutEntity = new LayoutEntity(
                field.getLayout().getId(),
                field.getLayout().getExternalKey(),
                field.getLayout().getName(),
                field.getLayout().getStatus()
        );

        return new FieldEntity(
                field.getId(),
                field.getExternalKey(),
                layoutEntity,
                field.getSource(),
                field.getFormula(),
                field.getFieldType(),
                field.getCalculationOrder()
        );
    }

    private Field toDomain(FieldEntity entity) {

        Layout layout = new Layout(
                entity.getLayout().getId(),
                entity.getLayout().getExternalKey(),
                entity.getLayout().getName(),
                entity.getLayout().getStatus()
        );

        return new Field(
                entity.getId(),
                entity.getExternalKey(),
                layout,
                entity.getSource(),
                entity.getFormula(),
                entity.getFieldType(),
                entity.getCalculationOrder()
        );
    }
}
