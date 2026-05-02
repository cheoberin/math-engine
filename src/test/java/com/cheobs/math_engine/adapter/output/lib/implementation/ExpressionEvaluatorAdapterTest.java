package com.cheobs.math_engine.adapter.output.lib.implementation;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpressionEvaluatorAdapterTest {

    private final ExpressionEvaluatorAdapter adapter = new ExpressionEvaluatorAdapter();

    @Test
    void shouldEvaluateExpressionWithBracketVariables() {
        BigDecimal result = adapter.evaluate("[A1]+[B2]*2", Map.of(
                "A1", BigDecimal.TEN,
                "B2", BigDecimal.valueOf(3)
        ));

        assertDecimalEquals("16", result);
    }

    @Test
    void shouldTreatNullAndMissingVariablesAsZero() {
        BigDecimal resultWithNullMap = adapter.evaluate("[A1]+10", null);
        BigDecimal resultWithMissingMapField = adapter.evaluate("[A1]+[B1]", Map.of("A1", BigDecimal.ONE));

        assertDecimalEquals("10", resultWithNullMap);
        assertDecimalEquals("1", resultWithMissingMapField);
    }

    @Test
    void shouldIgnoreExtraVariablesInMap() {
        BigDecimal result = adapter.evaluate("[A1]+1", Map.of(
                "A1", BigDecimal.valueOf(2),
                "UNUSED", BigDecimal.valueOf(999)
        ));

        assertDecimalEquals("3", result);
    }

    @Test
    void shouldReturnZeroWhenDivisionByZero() {
        BigDecimal result = adapter.evaluate("[A1]/[B1]", Map.of(
                "A1", BigDecimal.valueOf(50),
                "B1", BigDecimal.ZERO
        ));

        assertDecimalEquals("0", result);
    }

    @Test
    void shouldEvaluateVariablesStartingWithNumbersUsingAliases() {
        BigDecimal result = adapter.evaluate("[11]+[2A]", Map.of(
                "11", BigDecimal.valueOf(9),
                "2A", BigDecimal.ONE
        ));

        assertDecimalEquals("10", result);
    }

    @Test
    void shouldSupportAbsoluteValuePipeSyntax() {
        BigDecimal result = adapter.evaluate("0.3*([11]+|[11]|)/2", Map.of(
                "11", BigDecimal.valueOf(-10)
        ));

        assertDecimalEquals("0", result);
    }

    private void assertDecimalEquals(String expected, BigDecimal actual) {
        assertEquals(0, new BigDecimal(expected).compareTo(actual));
    }
}

