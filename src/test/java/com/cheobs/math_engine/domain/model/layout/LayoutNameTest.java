package com.cheobs.math_engine.domain.model.layout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LayoutNameTest {

    @Test
    void shouldTrimNameValue() {
        LayoutName layoutName = new LayoutName("  Main Layout  ");

        assertEquals("Main Layout", layoutName.value());
    }

    @Test
    void shouldThrowWhenNameIsNull() {
        LayoutValidationExcepiton exception = assertThrows(
                LayoutValidationExcepiton.class,
                () -> new LayoutName(null)
        );

        assertEquals("validation.layout.name.null", exception.getIdentifier());
    }

    @Test
    void shouldThrowWhenNameIsBlank() {
        LayoutValidationExcepiton exception = assertThrows(
                LayoutValidationExcepiton.class,
                () -> new LayoutName("   ")
        );

        assertEquals("validation.layout.name.blank", exception.getIdentifier());
    }

    @Test
    void shouldAcceptNameWith255Characters() {
        String name = "A".repeat(255);

        LayoutName layoutName = new LayoutName(name);

        assertEquals(name, layoutName.value());
    }

    @Test
    void shouldThrowWhenNameHasMoreThan255Characters() {
        String name = "A".repeat(256);

        LayoutValidationExcepiton exception = assertThrows(
                LayoutValidationExcepiton.class,
                () -> new LayoutName(name)
        );

        assertEquals("validation.layout.name.short", exception.getIdentifier());
    }
}

