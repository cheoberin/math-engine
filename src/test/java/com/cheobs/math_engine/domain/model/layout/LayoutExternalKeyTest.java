package com.cheobs.math_engine.domain.model.layout;

import com.cheobs.math_engine.domain.model.common.ValidationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LayoutExternalKeyTest {

    @Test
    void shouldNormalizeExternalKey() {
        LayoutExternalKey externalKey = new LayoutExternalKey(" abc4 ");

        assertEquals("ABC4", externalKey.value());
    }

    @Test
    void shouldThrowWhenExternalKeyIsNull() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> new LayoutExternalKey(null)
        );

        assertEquals("validation.layout.external-key.null", exception.getIdentifier());
    }

    @Test
    void shouldThrowWhenExternalKeyIsBlank() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> new LayoutExternalKey("   ")
        );

        assertEquals("validation.layout.external-key.blank", exception.getIdentifier());
    }

    @Test
    void shouldAcceptExternalKeyWith4Chars() {
        LayoutExternalKey externalKey = new LayoutExternalKey("ABC4");

        assertEquals("ABC4", externalKey.value());
    }

    @Test
    void shouldThrowWhenExternalKeyHasMoreThan4Chars() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> new LayoutExternalKey("ABC12345")
        );

        assertEquals("validation.layout.external-key.short", exception.getIdentifier());
    }

    @Test
    void shouldThrowWhenExternalKeyHasInvalidCharacters() {
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> new LayoutExternalKey("A-B")
        );

        assertEquals("validation.layout.external-key.characters", exception.getIdentifier());
    }
}

