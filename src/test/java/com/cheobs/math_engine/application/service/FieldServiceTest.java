package com.cheobs.math_engine.application.service;

import com.cheobs.math_engine.domain.model.common.ConflictException;
import com.cheobs.math_engine.domain.model.common.NotFoundException;
import com.cheobs.math_engine.domain.model.common.ValidationException;
import com.cheobs.math_engine.domain.model.field.Field;
import com.cheobs.math_engine.domain.model.field.FieldCommand;
import com.cheobs.math_engine.domain.model.field.FieldSource;
import com.cheobs.math_engine.domain.model.field.FieldType;
import com.cheobs.math_engine.domain.model.layout.Layout;
import com.cheobs.math_engine.domain.model.layout.LayoutStatus;
import com.cheobs.math_engine.domain.port.output.FieldPort;
import com.cheobs.math_engine.domain.port.output.LayoutPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FieldServiceTest {

    @Mock
    private LayoutPort layoutPort;

    @Mock
    private FieldPort fieldPort;

    @InjectMocks
    private FieldService fieldService;

    @Test
    void shouldCreateFieldWhenLayoutExistsAndExternalKeyIsAvailable() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = anyLayout(layoutId);
        FieldCommand command = new FieldCommand("ab1", FieldSource.INPUT, null, FieldType.NUMBER);

        Field persisted = new Field(
                UUID.randomUUID(),
                "AB1",
                layout,
                FieldSource.INPUT,
                null,
                FieldType.NUMBER,
                null
        );

        when(layoutPort.getById(layoutId)).thenReturn(Optional.of(layout));
        when(fieldPort.getByLayoutIdAndExternalKey(layoutId, "ab1")).thenReturn(Optional.empty());
        when(fieldPort.getByLayoutIdAndSearch(layoutId, null)).thenReturn(List.of());
        when(fieldPort.save(any(Field.class))).thenReturn(persisted);

        Field result = fieldService.createField(command, layoutId);

        assertSame(persisted, result);

        ArgumentCaptor<Field> captor = ArgumentCaptor.forClass(Field.class);
        verify(fieldPort).save(captor.capture());

        Field savedField = captor.getValue();
        assertEquals("AB1", savedField.getExternalKey());
        assertEquals(layoutId, savedField.getLayout().getId());
        assertEquals(FieldSource.INPUT, savedField.getSource());
    }

    @Test
    void shouldThrowNotFoundWhenCreatingFieldForMissingLayout() {
        UUID layoutId = UUID.randomUUID();
        FieldCommand command = new FieldCommand("A1", FieldSource.INPUT, null, FieldType.NUMBER);

        when(layoutPort.getById(layoutId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> fieldService.createField(command, layoutId)
        );

        assertEquals("not-found.layout", exception.getIdentifier());
        verify(fieldPort, never()).save(any(Field.class));
    }

    @Test
    void shouldThrowConflictWhenCreatingFieldWithExternalKeyAlreadyInUseInLayout() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = anyLayout(layoutId);
        FieldCommand command = new FieldCommand("A1", FieldSource.INPUT, null, FieldType.NUMBER);

        Field existing = new Field(
                UUID.randomUUID(),
                "A1",
                layout,
                FieldSource.INPUT,
                null,
                FieldType.NUMBER,
                null
        );

        when(layoutPort.getById(layoutId)).thenReturn(Optional.of(layout));
        when(fieldPort.getByLayoutIdAndExternalKey(layoutId, "A1")).thenReturn(Optional.of(existing));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> fieldService.createField(command, layoutId)
        );

        assertEquals("conflict.field.external-key.in-use", exception.getIdentifier());
        verify(fieldPort, never()).save(any(Field.class));
    }

    @Test
    void shouldThrowValidationWhenCreatingCalculationFieldWithMissingDependency() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = anyLayout(layoutId);
        FieldCommand command = new FieldCommand("B1", FieldSource.CALCULATION, "[X9]+1", FieldType.NUMBER);

        when(layoutPort.getById(layoutId)).thenReturn(Optional.of(layout));
        when(fieldPort.getByLayoutIdAndExternalKey(layoutId, "B1")).thenReturn(Optional.empty());
        when(fieldPort.getByLayoutIdAndSearch(layoutId, null)).thenReturn(List.of());

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> fieldService.createField(command, layoutId)
        );

        assertEquals("validation.field.formula.dependency.not-found", exception.getIdentifier());
        verify(fieldPort, never()).save(any(Field.class));
    }

    @Test
    void shouldThrowValidationWhenCreatingCalculationFieldWithMissingNumericDependency() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = anyLayout(layoutId);
        FieldCommand command = new FieldCommand("B1", FieldSource.CALCULATION, "[11]/[7]", FieldType.NUMBER);

        when(layoutPort.getById(layoutId)).thenReturn(Optional.of(layout));
        when(fieldPort.getByLayoutIdAndExternalKey(layoutId, "B1")).thenReturn(Optional.empty());
        when(fieldPort.getByLayoutIdAndSearch(layoutId, null)).thenReturn(List.of());

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> fieldService.createField(command, layoutId)
        );

        assertEquals("validation.field.formula.dependency.not-found", exception.getIdentifier());
        verify(fieldPort, never()).save(any(Field.class));
    }

    @Test
    void shouldThrowConflictWhenCreatingCalculationFieldThatIntroducesCycle() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = anyLayout(layoutId);
        Field existing = new Field(UUID.randomUUID(), "A1", layout, FieldSource.CALCULATION, "[B1]+1", FieldType.NUMBER, 1);
        FieldCommand command = new FieldCommand("B1", FieldSource.CALCULATION, "[A1]+1", FieldType.NUMBER);

        when(layoutPort.getById(layoutId)).thenReturn(Optional.of(layout));
        when(fieldPort.getByLayoutIdAndExternalKey(layoutId, "B1")).thenReturn(Optional.empty());
        when(fieldPort.getByLayoutIdAndSearch(layoutId, null)).thenReturn(List.of(existing));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> fieldService.createField(command, layoutId)
        );

        assertEquals("conflict.field.circular-dependency", exception.getIdentifier());
        verify(fieldPort, never()).save(any(Field.class));
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingMissingField() {
        UUID fieldId = UUID.randomUUID();
        FieldCommand command = new FieldCommand("A1", FieldSource.INPUT, null, FieldType.NUMBER);

        when(fieldPort.getById(fieldId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> fieldService.updateField(fieldId, command)
        );

        assertEquals("not-found.field", exception.getIdentifier());
        verify(fieldPort, never()).save(any(Field.class));
    }

    @Test
    void shouldThrowConflictWhenUpdatingFieldToExternalKeyUsedByAnotherField() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = anyLayout(layoutId);
        UUID fieldId = UUID.randomUUID();

        Field current = new Field(fieldId, "A1", layout, FieldSource.INPUT, null, FieldType.NUMBER, null);
        Field other = new Field(UUID.randomUUID(), "B1", layout, FieldSource.INPUT, null, FieldType.NUMBER, null);

        FieldCommand command = new FieldCommand("B1", FieldSource.INPUT, null, FieldType.NUMBER);

        when(fieldPort.getById(fieldId)).thenReturn(Optional.of(current));
        when(fieldPort.getByLayoutIdAndExternalKey(layoutId, "B1")).thenReturn(Optional.of(other));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> fieldService.updateField(fieldId, command)
        );

        assertEquals("conflict.field.external-key.in-use", exception.getIdentifier());
        verify(fieldPort, never()).save(any(Field.class));
    }

    @Test
    void shouldUpdateFieldWhenExternalKeyBelongsToSameField() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = anyLayout(layoutId);
        UUID fieldId = UUID.randomUUID();

        Field current = new Field(fieldId, "A1", layout, FieldSource.INPUT, null, FieldType.NUMBER, null);
        Field sameByExternalKey = new Field(fieldId, "B1", layout, FieldSource.INPUT, null, FieldType.NUMBER, null);
        Field dependency = new Field(UUID.randomUUID(), "C1", layout, FieldSource.INPUT, null, FieldType.NUMBER, null);

        FieldCommand command = new FieldCommand("b1", FieldSource.CALCULATION, "[C1]+1", FieldType.NUMBER);

        when(fieldPort.getById(fieldId)).thenReturn(Optional.of(current));
        when(fieldPort.getByLayoutIdAndExternalKey(layoutId, "b1")).thenReturn(Optional.of(sameByExternalKey));
        when(fieldPort.getByLayoutIdAndSearch(layoutId, null)).thenReturn(List.of(current, dependency));
        when(fieldPort.save(current)).thenReturn(current);

        Field result = fieldService.updateField(fieldId, command);

        assertSame(current, result);
        assertEquals("B1", result.getExternalKey());
        assertEquals(FieldSource.CALCULATION, result.getSource());
        assertEquals("[C1]+1", result.getFormula());
        assertEquals(1, result.getCalculationOrder());
        verify(fieldPort).save(current);
    }

    @Test
    void shouldCalculateOrderWhenCreatingCalculationField() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = anyLayout(layoutId);

        Field a1 = new Field(UUID.randomUUID(), "A1", layout, FieldSource.INPUT, null, FieldType.NUMBER, null);
        Field b1 = new Field(UUID.randomUUID(), "B1", layout, FieldSource.CALCULATION, "[A1]+1", FieldType.NUMBER, 1);

        FieldCommand command = new FieldCommand("C1", FieldSource.CALCULATION, "[B1]+1", FieldType.NUMBER);

        when(layoutPort.getById(layoutId)).thenReturn(Optional.of(layout));
        when(fieldPort.getByLayoutIdAndExternalKey(layoutId, "C1")).thenReturn(Optional.empty());
        when(fieldPort.getByLayoutIdAndSearch(layoutId, null)).thenReturn(List.of(a1, b1));
        when(fieldPort.save(any(Field.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Field result = fieldService.createField(command, layoutId);

        assertEquals("C1", result.getExternalKey());
        assertEquals(2, result.getCalculationOrder());
    }

    @Test
    void shouldKeepCalculationOrderNullForInputOnUpdate() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = anyLayout(layoutId);
        UUID fieldId = UUID.randomUUID();

        Field current = new Field(fieldId, "A1", layout, FieldSource.CALCULATION, "[B1]+1", FieldType.NUMBER, 2);
        Field sameByExternalKey = new Field(fieldId, "A1", layout, FieldSource.CALCULATION, "[B1]+1", FieldType.NUMBER, 2);
        Field b1 = new Field(UUID.randomUUID(), "B1", layout, FieldSource.INPUT, null, FieldType.NUMBER, null);
        FieldCommand command = new FieldCommand("A1", FieldSource.INPUT, null, FieldType.NUMBER);

        when(fieldPort.getById(fieldId)).thenReturn(Optional.of(current));
        when(fieldPort.getByLayoutIdAndExternalKey(layoutId, "A1")).thenReturn(Optional.of(sameByExternalKey));
        when(fieldPort.getByLayoutIdAndSearch(layoutId, null)).thenReturn(List.of(current, b1));
        when(fieldPort.save(current)).thenReturn(current);

        Field result = fieldService.updateField(fieldId, command);

        assertEquals(FieldSource.INPUT, result.getSource());
        assertNull(result.getCalculationOrder());
    }

    @Test
    void shouldGetFieldById() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = anyLayout(layoutId);

        UUID fieldId = UUID.randomUUID();
        Field field = new Field(fieldId, "A1", layout, FieldSource.INPUT, null, FieldType.NUMBER, null);

        when(fieldPort.getById(fieldId)).thenReturn(Optional.of(field));

        Field result = fieldService.getField(fieldId);

        assertSame(field, result);
    }

    @Test
    void shouldThrowNotFoundWhenGettingFieldById() {
        UUID fieldId = UUID.randomUUID();

        when(fieldPort.getById(fieldId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> fieldService.getField(fieldId)
        );

        assertEquals("not-found.field", exception.getIdentifier());
    }

    @Test
    void shouldThrowNotFoundWhenListingFieldsByMissingLayout() {
        UUID layoutId = UUID.randomUUID();

        when(layoutPort.getById(layoutId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> fieldService.getFieldsByLayoutId(layoutId, null)
        );

        assertEquals("not-found.layout", exception.getIdentifier());
    }

    @Test
    void shouldListFieldsUsingNullWhenSearchIsBlank() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = anyLayout(layoutId);

        List<Field> fields = List.of(
                new Field(UUID.randomUUID(), "A1", layout, FieldSource.INPUT, null, FieldType.NUMBER, null)
        );

        when(layoutPort.getById(layoutId)).thenReturn(Optional.of(layout));
        when(fieldPort.getByLayoutIdAndSearch(layoutId, null)).thenReturn(fields);

        assertSame(fields, fieldService.getFieldsByLayoutId(layoutId, null));
        assertSame(fields, fieldService.getFieldsByLayoutId(layoutId, ""));
        assertSame(fields, fieldService.getFieldsByLayoutId(layoutId, "   "));

        verify(fieldPort, times(3)).getByLayoutIdAndSearch(layoutId, null);
    }

    @Test
    void shouldListFieldsUsingProvidedSearch() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = anyLayout(layoutId);

        List<Field> fields = List.of(
                new Field(UUID.randomUUID(), "A1", layout, FieldSource.INPUT, null, FieldType.NUMBER, null)
        );

        when(layoutPort.getById(layoutId)).thenReturn(Optional.of(layout));
        when(fieldPort.getByLayoutIdAndSearch(layoutId, "abc")).thenReturn(fields);

        List<Field> result = fieldService.getFieldsByLayoutId(layoutId, "abc");

        assertSame(fields, result);
        verify(fieldPort).getByLayoutIdAndSearch(layoutId, "abc");
    }

    @Test
    void shouldDeleteField() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = anyLayout(layoutId);

        UUID fieldId = UUID.randomUUID();
        Field field = new Field(fieldId, "A1", layout, FieldSource.INPUT, null, FieldType.NUMBER, null);

        when(fieldPort.getById(fieldId)).thenReturn(Optional.of(field));

        fieldService.deleteField(fieldId);

        verify(fieldPort).deleteById(fieldId);
    }

    @Test
    void shouldThrowNotFoundWhenDeletingMissingField() {
        UUID fieldId = UUID.randomUUID();

        when(fieldPort.getById(fieldId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> fieldService.deleteField(fieldId)
        );

        assertEquals("not-found.field", exception.getIdentifier());
        verify(fieldPort, never()).deleteById(fieldId);
    }

    @Test
    void shouldDeleteFieldsInBatchWhenAllExist() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = anyLayout(layoutId);

        UUID firstId = UUID.randomUUID();
        UUID secondId = UUID.randomUUID();

        Field first = new Field(firstId, "A1", layout, FieldSource.INPUT, null, FieldType.NUMBER, null);
        Field second = new Field(secondId, "B1", layout, FieldSource.INPUT, null, FieldType.NUMBER, null);

        when(fieldPort.getById(firstId)).thenReturn(Optional.of(first));
        when(fieldPort.getById(secondId)).thenReturn(Optional.of(second));

        fieldService.deleteFields(List.of(firstId, secondId));

        verify(fieldPort).deleteByIds(List.of(firstId, secondId));
    }

    @Test
    void shouldThrowNotFoundWhenDeletingFieldsBatchAndAnyFieldIsMissing() {
        UUID existingId = UUID.randomUUID();
        UUID missingId = UUID.randomUUID();

        UUID layoutId = UUID.randomUUID();
        Layout layout = anyLayout(layoutId);
        Field existing = new Field(existingId, "A1", layout, FieldSource.INPUT, null, FieldType.NUMBER, null);

        when(fieldPort.getById(existingId)).thenReturn(Optional.of(existing));
        when(fieldPort.getById(missingId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> fieldService.deleteFields(List.of(existingId, missingId))
        );

        assertEquals("not-found.field", exception.getIdentifier());
        verify(fieldPort, never()).deleteByIds(any());
    }

    private static Layout anyLayout(UUID layoutId) {
        return new Layout(layoutId, "LAY1", "Main Layout", LayoutStatus.ACTIVE);
    }
}

