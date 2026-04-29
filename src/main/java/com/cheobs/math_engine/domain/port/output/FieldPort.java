package com.cheobs.math_engine.domain.port.output;

import com.cheobs.math_engine.domain.model.field.Field;
import com.cheobs.math_engine.domain.model.field.FieldSource;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FieldPort {

    Field save(Field field);

    Optional<Field> getById(UUID id);

    Optional<Field> getByLayoutIdAndExternalKey(UUID layoutId, String externalKey);

    List<Field> getByLayoutIdAndSearch(UUID layoutId, String search);

    List<Field> getByLayoutIdAndFieldType(UUID layoutId, FieldSource fieldSource);

    void deleteById(UUID id);

    void deleteByIds(List<UUID> ids);

}
