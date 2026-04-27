package com.cheobs.math_engine.adapter.output.jpa.entity;

import com.cheobs.math_engine.domain.model.field.FieldSource;
import com.cheobs.math_engine.domain.model.field.FieldType;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "field", uniqueConstraints = {@UniqueConstraint(name = "uk_field_layout_external_key", columnNames = {"layout_id", "externalKey"})})
public class FieldEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "external_key", nullable = false)
    private String externalKey;

    @ManyToOne
    @JoinColumn(name = "layout_id", nullable = false, updatable = false)
    private LayoutEntity layout;

    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false)
    private FieldSource source;

    @Column(name = "formula")
    private String formula;

    @Enumerated(EnumType.STRING)
    @Column(name = "field_type", nullable = false)
    private FieldType fieldType;

    @Column(name = "calculation_order")
    private Integer calculationOrder;

    public FieldEntity() {
    }

    public FieldEntity(UUID id, String externalKey, LayoutEntity layout, FieldSource source, String formula, FieldType fieldType, Integer calculationOrder) {
        this.id = id;
        this.externalKey = externalKey;
        this.layout = layout;
        this.source = source;
        this.formula = formula;
        this.fieldType = fieldType;
        this.calculationOrder = calculationOrder;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getExternalKey() {
        return externalKey;
    }

    public void setExternalKey(String externalKey) {
        this.externalKey = externalKey;
    }

    public LayoutEntity getLayout() {
        return layout;
    }

    public void setLayout(LayoutEntity layout) {
        this.layout = layout;
    }

    public FieldSource getSource() {
        return source;
    }

    public void setSource(FieldSource source) {
        this.source = source;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public Integer getCalculationOrder() {
        return calculationOrder;
    }

    public void setCalculationOrder(Integer calculationOrder) {
        this.calculationOrder = calculationOrder;
    }
}
