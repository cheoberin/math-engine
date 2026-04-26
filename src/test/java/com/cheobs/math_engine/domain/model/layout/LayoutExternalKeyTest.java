package com.cheobs.math_engine.domain.model.layout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LayoutExternalKeyTest {

    @Test
    void shouldNormalizeExternalKey() {
        LayoutExternalKey externalKey = new LayoutExternalKey(" a1b2 ");

        assertEquals("A1B2", externalKey.value());
    }

    @Test
    void shouldThrowWhenExternalKeyIsNull() {
        LayoutValidationExcepiton exception = assertThrows(
                LayoutValidationExcepiton.class,
                () -> new LayoutExternalKey(null)
        );

        assertEquals("validation.layout.external-key.null", exception.getIdentifier());
    }

    @Test
    void shouldThrowWhenExternalKeyIsBlank() {
        LayoutValidationExcepiton exception = assertThrows(
                LayoutValidationExcepiton.class,
                () -> new LayoutExternalKey("   ")
        );

        assertEquals("validation.layout.external-key.blank", exception.getIdentifier());
    }

    @Test
    void shouldThrowWhenExternalKeyHasMoreThan4Chars() {
        LayoutValidationExcepiton exception = assertThrows(
                LayoutValidationExcepiton.class,
                () -> new LayoutExternalKey("ABCDE")
        );

        assertEquals("validation.layout.external-key.short", exception.getIdentifier());
    }

    @Test
    void shouldThrowWhenExternalKeyHasInvalidCharacters() {
        LayoutValidationExcepiton exception = assertThrows(
                LayoutValidationExcepiton.class,
                () -> new LayoutExternalKey("A-B")
        );

        assertEquals("validation.layout.external-key.characters", exception.getIdentifier());
    }
}

