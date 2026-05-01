package com.cheobs.math_engine.adapter.output.jpa.implementation;

import com.cheobs.math_engine.adapter.output.jpa.entity.SubmissionFieldEntity;
import com.cheobs.math_engine.adapter.output.jpa.repository.SubmissionFieldRepository;
import com.cheobs.math_engine.adapter.output.jpa.repository.SubmissionRepository;
import com.cheobs.math_engine.domain.model.field.Field;
import com.cheobs.math_engine.domain.model.field.FieldSource;
import com.cheobs.math_engine.domain.model.field.FieldType;
import com.cheobs.math_engine.domain.model.layout.Layout;
import com.cheobs.math_engine.domain.model.layout.LayoutStatus;
import com.cheobs.math_engine.domain.model.submission.Submission;
import com.cheobs.math_engine.domain.model.submission.SubmissionField;
import com.cheobs.math_engine.domain.model.submission.SubmissionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubmissionAdapterTest {

    @Mock
    private SubmissionRepository submissionRepository;

    @Mock
    private SubmissionFieldRepository submissionFieldRepository;

    private SubmissionAdapter submissionAdapter;

    @BeforeEach
    void setUp() {
        submissionAdapter = new SubmissionAdapter(submissionRepository, submissionFieldRepository);
    }

    @Test
    void shouldKeepLayoutWhenSavingSubmissionFields() {
        Layout layout = new Layout(UUID.randomUUID(), "LAY1", "Main Layout", LayoutStatus.ACTIVE);
        Submission submission = new Submission(UUID.randomUUID(), layout, SubmissionStatus.PENDING, OffsetDateTime.now());
        Field field = new Field(UUID.randomUUID(), "A1", "Field A1", layout, FieldSource.INPUT, null, FieldType.NUMBER, null);
        SubmissionField submissionField = new SubmissionField(submission, field, BigDecimal.TEN);

        when(submissionFieldRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        List<SubmissionField> savedSubmissionFields = submissionAdapter.save(List.of(submissionField));

        assertEquals(1, savedSubmissionFields.size());

        SubmissionField savedSubmissionField = savedSubmissionFields.getFirst();
        assertNotNull(savedSubmissionField.getSubmission().getLayout());
        assertEquals(layout.getId(), savedSubmissionField.getSubmission().getLayout().getId());
        assertEquals(layout.getExternalKey(), savedSubmissionField.getSubmission().getLayout().getExternalKey());
        assertNotNull(savedSubmissionField.getField().getLayout());
        assertEquals(layout.getId(), savedSubmissionField.getField().getLayout().getId());

        ArgumentCaptor<List<SubmissionFieldEntity>> entityCaptor = ArgumentCaptor.captor();
        verify(submissionFieldRepository).saveAll(entityCaptor.capture());

        SubmissionFieldEntity persistedEntity = entityCaptor.getValue().getFirst();
        assertNotNull(persistedEntity.getSubmission().getLayout());
        assertEquals(layout.getId(), persistedEntity.getSubmission().getLayout().getId());
        assertNotNull(persistedEntity.getField().getLayout());
        assertEquals(layout.getId(), persistedEntity.getField().getLayout().getId());
    }
}


