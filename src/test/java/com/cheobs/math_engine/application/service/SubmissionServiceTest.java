package com.cheobs.math_engine.application.service;

import com.cheobs.math_engine.domain.model.common.ConflictException;
import com.cheobs.math_engine.domain.model.common.NotFoundException;
import com.cheobs.math_engine.domain.model.field.Field;
import com.cheobs.math_engine.domain.model.field.FieldSource;
import com.cheobs.math_engine.domain.model.field.FieldType;
import com.cheobs.math_engine.domain.model.layout.Layout;
import com.cheobs.math_engine.domain.model.layout.LayoutStatus;
import com.cheobs.math_engine.domain.model.submission.*;
import com.cheobs.math_engine.domain.port.output.FieldPort;
import com.cheobs.math_engine.domain.port.output.LayoutPort;
import com.cheobs.math_engine.domain.port.output.SubmissionPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmissionServiceTest {

    @Mock
    private LayoutPort layoutPort;

    @Mock
    private FieldPort fieldPort;

    @Mock
    private SubmissionPort submissionPort;

    @InjectMocks
    private SubmissionService submissionService;

    @Test
    void shouldCreateSubmissionWhenLayoutIsActiveAndFieldsMatch() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = new Layout(layoutId, "LAY1", "Main Layout", LayoutStatus.ACTIVE);

        Field fieldA1 = anyInputField( layout, "A1");
        Field fieldB1 = anyInputField( layout, "B1");

        SubmissionCommand command = new SubmissionCommand("LAY1", List.of(
                new SubmissionFieldCommand("A1", BigDecimal.valueOf(10)),
                new SubmissionFieldCommand("B1", BigDecimal.valueOf(20))
        ));

        Submission savedSubmission = new Submission(UUID.randomUUID(), layout, SubmissionStatus.PENDING, null);

        when(layoutPort.getByExternalKey("LAY1")).thenReturn(Optional.of(layout));
        when(fieldPort.getByLayoutIdAndFieldType(layoutId, FieldSource.INPUT)).thenReturn(List.of(fieldA1, fieldB1));
        when(submissionPort.save(any(Submission.class))).thenReturn(savedSubmission);
        when(submissionPort.save(anyList())).thenReturn(List.of());

        Submission result = submissionService.createSubmission(command);

        assertSame(savedSubmission, result);
        verify(submissionPort).save(any(Submission.class));

        ArgumentCaptor<List<SubmissionField>> captor = ArgumentCaptor.captor();
        verify(submissionPort).save(captor.capture());
        List<SubmissionField> submissionFields = captor.getValue();
        assertEquals(2, submissionFields.size());
    }

    @Test
    void shouldThrowNotFoundWhenLayoutDoesNotExist() {
        SubmissionCommand command = new SubmissionCommand("MISS", List.of());

        when(layoutPort.getByExternalKey("MISS")).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> submissionService.createSubmission(command)
        );

        assertEquals("not-found.layout", exception.getIdentifier());
        verify(submissionPort, never()).save(any(Submission.class));
    }

    @Test
    void shouldThrowConflictWhenLayoutIsInactive() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = new Layout(layoutId, "LAY1", "Main Layout", LayoutStatus.INACTIVE);

        SubmissionCommand command = new SubmissionCommand("LAY1", List.of());

        when(layoutPort.getByExternalKey("LAY1")).thenReturn(Optional.of(layout));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> submissionService.createSubmission(command)
        );

        assertEquals("conflict.layout.inactive", exception.getIdentifier());
        verify(submissionPort, never()).save(any(Submission.class));
    }

    @Test
    void shouldThrowValidationExceptionWhenFieldIsMissingFromSubmission() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = new Layout(layoutId, "LAY1", "Main Layout", LayoutStatus.ACTIVE);

        Field fieldA1 = anyInputField( layout, "A1");
        Field fieldB1 = anyInputField( layout, "B1");

        SubmissionCommand command = new SubmissionCommand("LAY1", List.of(
                new SubmissionFieldCommand("A1", BigDecimal.valueOf(10))
                // B1 is missing
        ));

        when(layoutPort.getByExternalKey("LAY1")).thenReturn(Optional.of(layout));
        when(fieldPort.getByLayoutIdAndFieldType(layoutId, FieldSource.INPUT)).thenReturn(List.of(fieldA1, fieldB1));

        FieldSubmissionValidationException exception = assertThrows(
                FieldSubmissionValidationException.class,
                () -> submissionService.createSubmission(command)
        );

        assertEquals("validation.submission.fields", exception.getIdentifier());
        assertTrue(exception.getMissingCodes().contains("B1"));
        verify(submissionPort, never()).save(any(Submission.class));
    }

    @Test
    void shouldThrowValidationExceptionWhenUnexpectedFieldIsSubmitted() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = new Layout(layoutId, "LAY1", "Main Layout", LayoutStatus.ACTIVE);

        Field fieldA1 = anyInputField( layout, "A1");

        SubmissionCommand command = new SubmissionCommand("LAY1", List.of(
                new SubmissionFieldCommand("A1", BigDecimal.valueOf(10)),
                new SubmissionFieldCommand("Z9", BigDecimal.valueOf(99)) // not expected
        ));

        when(layoutPort.getByExternalKey("LAY1")).thenReturn(Optional.of(layout));
        when(fieldPort.getByLayoutIdAndFieldType(layoutId, FieldSource.INPUT)).thenReturn(List.of(fieldA1));

        FieldSubmissionValidationException exception = assertThrows(
                FieldSubmissionValidationException.class,
                () -> submissionService.createSubmission(command)
        );

        assertEquals("validation.submission.fields", exception.getIdentifier());
        assertTrue(exception.getNotExpectedCodes().contains("Z9"));
        verify(submissionPort, never()).save(any(Submission.class));
    }

    @Test
    void shouldThrowValidationExceptionWhenDuplicateFieldsAreSubmitted() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = new Layout(layoutId, "LAY1", "Main Layout", LayoutStatus.ACTIVE);

        Field fieldA1 = anyInputField( layout, "A1");

        SubmissionCommand command = new SubmissionCommand("LAY1", List.of(
                new SubmissionFieldCommand("A1", BigDecimal.valueOf(10)),
                new SubmissionFieldCommand("A1", BigDecimal.valueOf(20)) // duplicate
        ));

        when(layoutPort.getByExternalKey("LAY1")).thenReturn(Optional.of(layout));
        when(fieldPort.getByLayoutIdAndFieldType(layoutId, FieldSource.INPUT)).thenReturn(List.of(fieldA1));

        FieldSubmissionValidationException exception = assertThrows(
                FieldSubmissionValidationException.class,
                () -> submissionService.createSubmission(command)
        );

        assertEquals("validation.submission.fields", exception.getIdentifier());
        assertTrue(exception.getDuplicateCodes().contains("A1"));
        verify(submissionPort, never()).save(any(Submission.class));
    }

    @Test
    void shouldThrowConflictWhenSubmissionContainsNullFieldExternalKey() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = new Layout(layoutId, "LAY1", "Main Layout", LayoutStatus.ACTIVE);

        Field fieldA1 = anyInputField(layout, "A1");

        SubmissionCommand command = new SubmissionCommand("LAY1", List.of(
                new SubmissionFieldCommand(null, BigDecimal.valueOf(10))
        ));

        when(layoutPort.getByExternalKey("LAY1")).thenReturn(Optional.of(layout));
        when(fieldPort.getByLayoutIdAndFieldType(layoutId, FieldSource.INPUT)).thenReturn(List.of(fieldA1));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> submissionService.createSubmission(command)
        );

        assertEquals("conflict.field.external-key.null", exception.getIdentifier());
        verify(submissionPort, never()).save(any(Submission.class));
    }

    @Test
    void shouldMapSubmissionFieldValuesCorrectlyFromCommand() {
        UUID layoutId = UUID.randomUUID();
        Layout layout = new Layout(layoutId, "LAY1", "Main Layout", LayoutStatus.ACTIVE);

        Field fieldA1 = anyInputField(layout, "A1");

        BigDecimal expectedValue = BigDecimal.valueOf(42);
        SubmissionCommand command = new SubmissionCommand("LAY1", List.of(
                new SubmissionFieldCommand("A1", expectedValue)
        ));

        Submission savedSubmission = new Submission(UUID.randomUUID(), layout, SubmissionStatus.PENDING, null);

        when(layoutPort.getByExternalKey("LAY1")).thenReturn(Optional.of(layout));
        when(fieldPort.getByLayoutIdAndFieldType(layoutId, FieldSource.INPUT)).thenReturn(List.of(fieldA1));
        when(submissionPort.save(any(Submission.class))).thenReturn(savedSubmission);
        when(submissionPort.save(anyList())).thenReturn(List.of());

        submissionService.createSubmission(command);

        ArgumentCaptor<List<SubmissionField>> captor = ArgumentCaptor.captor();
        verify(submissionPort).save(captor.capture());

        List<SubmissionField> saved = captor.getValue();
        assertEquals(1, saved.size());
        assertEquals(expectedValue, saved.getFirst().getValue());
        assertSame(fieldA1, saved.getFirst().getField());
        assertSame(savedSubmission, saved.getFirst().getSubmission());
    }

    private static Field anyInputField(Layout layout, String externalKey) {
        return new Field(UUID.randomUUID(), externalKey, "Field " + externalKey, layout, FieldSource.INPUT, null, FieldType.NUMBER, null);
    }
}

