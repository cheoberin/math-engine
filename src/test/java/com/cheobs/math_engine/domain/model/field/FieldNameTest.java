package com.cheobs.math_engine.domain.model.field;

import com.cheobs.math_engine.domain.model.common.exceptions.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FieldNameTest {

    @Test
    void shouldTrimNameValue() {
        FieldName fieldName = new FieldName("  Field Name  ");

        assertEquals("Field Name", fieldName.value());
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> new FieldName(null)
        );

        assertEquals("validation.field.external-key.null", exception.getIdentifier());
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> new FieldName("   ")
        );

        assertEquals("validation.field.external-key.blank", exception.getIdentifier());
    }

    @Test
    void shouldAcceptNameWith255Characters() {
        String name = "A".repeat(255);

        FieldName fieldName = new FieldName(name);

        assertEquals(name, fieldName.value());
    }

    @Test
    void shouldThrowWhenNameHasMoreThan255Characters() {
        String name = "A".repeat(256);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> new FieldName(name)
        );

        assertEquals("validation.field.external-key.short", exception.getIdentifier());
    }
}

