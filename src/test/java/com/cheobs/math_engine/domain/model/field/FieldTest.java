package com.cheobs.math_engine.domain.model.field;

import com.cheobs.math_engine.domain.model.common.exceptions.ValidationException;
import com.cheobs.math_engine.domain.model.layout.Layout;
import com.cheobs.math_engine.domain.model.layout.LayoutStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FieldTest {

    @Test
    void shouldCreateInputFieldWhenFormulaIsNull() {
        Layout layout = anyLayout();

        Field field = new Field(
                UUID.randomUUID(),
                "A1",
                "Field A1",
                layout,
                FieldSource.INPUT,
                null,
                FieldType.NUMBER,
                null
        );

        assertEquals(FieldSource.INPUT, field.getSource());
        assertNull(field.getFormula());
        assertNull(field.getCalculationOrder());
    }

    @Test
    void shouldCreateInputFieldWhenFormulaIsBlankBecauseItNormalizesToNull() {
        Layout layout = anyLayout();

        Field field = new Field(
                UUID.randomUUID(),
                "A1",
                "Field A1",
                layout,
                FieldSource.INPUT,
                "   ",
                FieldType.NUMBER,
                null
        );

        assertEquals(FieldSource.INPUT, field.getSource());
        assertNull(field.getFormula());
        assertNull(field.getCalculationOrder());
    }

    @Test
    void shouldThrowWhenInputFieldHasFormula() {
        Layout layout = anyLayout();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> new Field(
                        UUID.randomUUID(),
                        "A1",
                        "Field A1",
                        layout,
                        FieldSource.INPUT,
                        "A + B",
                        FieldType.NUMBER,
                        null
                )
        );

        assertEquals("validation.field.formula.must-be-null", exception.getIdentifier());
    }

    @Test
    void shouldThrowWhenCreatingInputFieldFromCommandWithFormula() {
        Layout layout = anyLayout();
        FieldCommand command = new FieldCommand("A1", "Field A1", FieldSource.INPUT, "A+B", FieldType.NUMBER);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> new Field(command, layout, null)
        );

        assertEquals("validation.field.formula.must-be-null", exception.getIdentifier());
    }

    @Test
    void shouldThrowWhenInputFieldHasCalculationOrder() {
        Layout layout = anyLayout();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> new Field(
                        UUID.randomUUID(),
                        "A1",
                        "Field A1",
                        layout,
                        FieldSource.INPUT,
                        null,
                        FieldType.NUMBER,
                        1
                )
        );

        assertEquals("validation.field.calculation-order.must-be-null", exception.getIdentifier());
    }

    @Test
    void shouldCreateCalculationFieldWhenFormulaIsPresent() {
        Layout layout = anyLayout();

        Field field = new Field(
                UUID.randomUUID(),
                "A1",
                "Field A1",
                layout,
                FieldSource.CALCULATION,
                " A + B ",
                FieldType.NUMBER,
                1
        );

        assertEquals(FieldSource.CALCULATION, field.getSource());
        assertEquals("A+B", field.getFormula());
        assertEquals(1, field.getCalculationOrder());
    }

    @Test
    void shouldThrowWhenCalculationFieldFormulaIsMissing() {
        Layout layout = anyLayout();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> new Field(
                        UUID.randomUUID(),
                        "A1",
                        "Field A1",
                        layout,
                        FieldSource.CALCULATION,
                        "   ",
                        FieldType.NUMBER,
                        1
                )
        );

        assertEquals("validation.field.formula.required", exception.getIdentifier());
    }

    @Test
    void shouldThrowWhenCalculationFieldHasNoCalculationOrder() {
        Layout layout = anyLayout();

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> new Field(
                        UUID.randomUUID(),
                        "A1",
                        "Field A1",
                        layout,
                        FieldSource.CALCULATION,
                        "A+B",
                        FieldType.NUMBER,
                        null
                )
        );

        assertEquals("validation.field.calculation-order.required", exception.getIdentifier());
    }

    @Test
    void shouldThrowWhenUpdatingInputFieldToCalculationWithoutFormula() {
        Layout layout = anyLayout();

        Field field = new Field(
                UUID.randomUUID(),
                "A1",
                "Field A1",
                layout,
                FieldSource.INPUT,
                null,
                FieldType.NUMBER,
                null
        );

        FieldCommand command = new FieldCommand("A1", "Field A1", FieldSource.CALCULATION, null, FieldType.NUMBER);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> field.updateDetails(command, 1)
        );

        assertEquals("validation.field.formula.required", exception.getIdentifier());
    }

    @Test
    void shouldThrowWhenUpdatingCalculationFieldToInputWithFormula() {
        Layout layout = anyLayout();

        Field field = new Field(
                UUID.randomUUID(),
                "A1",
                "Field A1",
                layout,
                FieldSource.CALCULATION,
                "A+B",
                FieldType.NUMBER,
                1
        );

        FieldCommand command = new FieldCommand("A1", "Field A1", FieldSource.INPUT, "A+B", FieldType.NUMBER);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> field.updateDetails(command, null)
        );

        assertEquals("validation.field.formula.must-be-null", exception.getIdentifier());
    }

    @Test
    void shouldThrowWhenUpdatingInputFieldWithCalculationOrder() {
        Layout layout = anyLayout();

        Field field = new Field(
                UUID.randomUUID(),
                "A1",
                "Field A1",
                layout,
                FieldSource.INPUT,
                null,
                FieldType.NUMBER,
                null
        );

        FieldCommand command = new FieldCommand("A1", "Field A1", FieldSource.INPUT, null, FieldType.NUMBER);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> field.updateDetails(command, 2)
        );

        assertEquals("validation.field.calculation-order.must-be-null", exception.getIdentifier());
    }

    @Test
    void shouldThrowWhenUpdatingCalculationFieldWithoutCalculationOrder() {
        Layout layout = anyLayout();

        Field field = new Field(
                UUID.randomUUID(),
                "A1",
                "Field A1",
                layout,
                FieldSource.CALCULATION,
                "A+B",
                FieldType.NUMBER,
                1
        );

        FieldCommand command = new FieldCommand("A1", "Field A1", FieldSource.CALCULATION, "A+B", FieldType.NUMBER);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> field.updateDetails(command, null)
        );

        assertEquals("validation.field.calculation-order.required", exception.getIdentifier());
    }

    private static Layout anyLayout() {
        return new Layout(UUID.randomUUID(), "LAY1", "Main Layout", LayoutStatus.ACTIVE);
    }
}


