package com.cheobs.math_engine.domain.model.field;

import com.cheobs.math_engine.domain.model.common.exceptions.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FieldExternalKeyTest {

    @Test
    void shouldNormalizeExternalKey() {
        FieldExternalKey externalKey = new FieldExternalKey(" abc1234 ");

        assertEquals("ABC1234", externalKey.value());
    }

    @Test
    void shouldThrowWhenExternalKeyIsNull() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> new FieldExternalKey(null)
        );

        assertEquals("validation.field.external-key.null", exception.getIdentifier());
    }

    @Test
    void shouldThrowWhenExternalKeyIsBlank() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> new FieldExternalKey("   ")
        );

        assertEquals("validation.field.external-key.blank", exception.getIdentifier());
    }

    @Test
    void shouldAcceptExternalKeyWith7Chars() {
        FieldExternalKey externalKey = new FieldExternalKey("ABC1234");

        assertEquals("ABC1234", externalKey.value());
    }

    @Test
    void shouldThrowWhenExternalKeyHasMoreThan7Chars() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> new FieldExternalKey("ABC12345")
        );

        assertEquals("validation.field.external-key.short", exception.getIdentifier());
    }

    @Test
    void shouldThrowWhenExternalKeyHasInvalidCharacters() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> new FieldExternalKey("A-B")
        );

        assertEquals("validation.field.external-key.characters", exception.getIdentifier());
    }
}

