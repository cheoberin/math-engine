package com.cheobs.math_engine.domain.port.output;

import com.cheobs.math_engine.domain.model.field.Field;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FieldPort {

	Field save(Field field);

	Optional<Field> getById(UUID id);

	Optional<Field> getByLayoutIdAndExternalKey(UUID layoutId, String externalKey);

	List<Field> getByLayoutIdAndSearch(UUID layoutId, String search);

	void deleteById(UUID id);

	void deleteByIds(List<UUID> ids);

}
