package com.cheobs.math_engine.domain.model.field;

import com.cheobs.math_engine.domain.model.common.ValidationException;
import com.cheobs.math_engine.domain.model.layout.Layout;

import java.util.Objects;
import java.util.UUID;

public class Field {

    private UUID id;
    private FieldExternalKey externalKey;
    private final Layout layout;
    private FieldSource source;
    private FieldFormula formula;
    private FieldType fieldType;
    private Integer calculationOrder;

    public Field(UUID id, String externalKey, Layout layout, FieldSource source, String formula, FieldType fieldType, Integer calculationOrder) {
        this.id = Objects.requireNonNull(id, "Field id cannot be null");
        this.externalKey = new FieldExternalKey(externalKey);
        this.layout = Objects.requireNonNull(layout, "Field layout cannot be null");
        this.source = Objects.requireNonNull(source, "Field source cannot be null");
        this.formula = new FieldFormula(formula);
        validateFormulaBySource(this.source, this.formula);
        this.fieldType = Objects.requireNonNull(fieldType, "Field type cannot be null");
        validateCalculationOrderBySource(this.source, calculationOrder);
        this.calculationOrder = calculationOrder;
    }

    public Field(FieldCommand command, Layout layout, Integer calculationOrder) {
        Objects.requireNonNull(command, "FieldCommand cannot be null");
        this.externalKey = new FieldExternalKey(command.externalKey());
        this.layout = Objects.requireNonNull(layout, "Field layout cannot be null");
        this.source = Objects.requireNonNull(command.source(), "Field source cannot be null");
        this.formula = new FieldFormula(command.formula());
        validateFormulaBySource(this.source, this.formula);
        this.fieldType = Objects.requireNonNull(command.fieldType(), "Field type cannot be null");
        validateCalculationOrderBySource(this.source, calculationOrder);
        this.calculationOrder = calculationOrder;
    }

    public void updateDetails(FieldCommand command, Integer calculationOrder) {
        Objects.requireNonNull(command, "FieldCommand cannot be null");
        this.externalKey = new FieldExternalKey(command.externalKey());
        this.source = Objects.requireNonNull(command.source(), "Field source cannot be null");
        this.formula = new FieldFormula(command.formula());
        validateFormulaBySource(this.source, this.formula);
        this.fieldType = Objects.requireNonNull(command.fieldType(), "Field type cannot be null");
        validateCalculationOrderBySource(this.source, calculationOrder);
        this.calculationOrder = calculationOrder;
    }

    private static void validateFormulaBySource(FieldSource source, FieldFormula formula) {
        if (source == FieldSource.INPUT && formula.value() != null) {
            throw new ValidationException(
                    "Field formula must be null when source is INPUT",
                    "validation.field.formula.must-be-null"
            );
        }

        if (source == FieldSource.CALCULATION && formula.value() == null) {
            throw new ValidationException(
                    "Field formula is required when source is CALCULATION",
                    "validation.field.formula.required"
            );
        }
    }

    private static void validateCalculationOrderBySource(FieldSource source, Integer calculationOrder) {
        if (source == FieldSource.INPUT && calculationOrder != null) {
            throw new ValidationException(
                    "Field calculation order must be null when source is INPUT",
                    "validation.field.calculation-order.must-be-null"
            );
        }

        if (source == FieldSource.CALCULATION && calculationOrder == null) {
            throw new ValidationException(
                    "Field calculation order is required when source is CALCULATION",
                    "validation.field.calculation-order.required"
            );
        }
    }

    public UUID getId() {
        return id;
    }

    public String getExternalKey() {
        return externalKey.value();
    }

    public Layout getLayout() {
        return layout;
    }

    public FieldSource getSource() {
        return source;
    }

    public String getFormula() {
        return formula.value();
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public Integer getCalculationOrder() {
        return calculationOrder;
    }
}
