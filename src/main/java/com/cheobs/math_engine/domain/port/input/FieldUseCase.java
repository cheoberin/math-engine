package com.cheobs.math_engine.domain.port.input;

import com.cheobs.math_engine.domain.model.field.Field;
import com.cheobs.math_engine.domain.model.field.FieldCommand;

import java.util.List;
import java.util.UUID;

public interface FieldUseCase {

    Field createField(FieldCommand command, UUID layoutId);

    Field updateField(UUID id, FieldCommand command);

    Field getField(UUID id);

    List<Field> getFieldsByLayoutId(UUID layoutId, String search);

    void deleteField(UUID id);

    void deleteFields(List<UUID> ids);

}
