package com.cheobs.math_engine.domain.model.field;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FieldFormulaTest {

    @Test
    void shouldReturnNullWhenValueIsNull() {
        FieldFormula formula = new FieldFormula(null);

        assertNull(formula.value());
    }

    @Test
    void shouldReturnNullWhenValueIsBlank() {
        FieldFormula formula = new FieldFormula("   ");

        assertNull(formula.value());
    }

    @Test
    void shouldTrimAndUppercaseFormula() {
        FieldFormula formula = new FieldFormula("  a1 + b2  ");

        assertEquals("A1+B2", formula.value());
    }

    @Test
    void shouldRemoveInternalSpacesFromFormula() {
        FieldFormula formula = new FieldFormula("[A1] + [B1] * 2");

        assertEquals("[A1]+[B1]*2", formula.value());
    }

    @Test
    void shouldUppercaseFormula() {
        FieldFormula formula = new FieldFormula("[abc]+[xyz]");

        assertEquals("[ABC]+[XYZ]", formula.value());
    }

    @Test
    void shouldPreserveAlreadyNormalizedFormula() {
        FieldFormula formula = new FieldFormula("[A1]+1");

        assertEquals("[A1]+1", formula.value());
    }
}

