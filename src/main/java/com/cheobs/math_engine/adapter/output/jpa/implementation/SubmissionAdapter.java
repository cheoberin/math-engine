package com.cheobs.math_engine.adapter.output.jpa.implementation;

import com.cheobs.math_engine.adapter.output.jpa.entity.*;
import com.cheobs.math_engine.adapter.output.jpa.repository.ProcessedFieldRepository;
import com.cheobs.math_engine.adapter.output.jpa.repository.SubmissionFieldRepository;
import com.cheobs.math_engine.adapter.output.jpa.repository.SubmissionRepository;
import com.cheobs.math_engine.domain.model.field.Field;
import com.cheobs.math_engine.domain.model.layout.Layout;
import com.cheobs.math_engine.domain.model.submission.ProcessedField;
import com.cheobs.math_engine.domain.model.submission.Submission;
import com.cheobs.math_engine.domain.model.submission.SubmissionField;
import com.cheobs.math_engine.domain.port.output.SubmissionPort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SubmissionAdapter implements SubmissionPort {

    private final SubmissionRepository submissionRepository;
    private final SubmissionFieldRepository submissionFieldRepository;
    private final ProcessedFieldRepository processedFieldRepository;

    public SubmissionAdapter(
            SubmissionRepository submissionRepository,
            SubmissionFieldRepository submissionFieldRepository,
            ProcessedFieldRepository processedFieldRepository
    ) {
        this.submissionRepository = submissionRepository;
        this.submissionFieldRepository = submissionFieldRepository;
        this.processedFieldRepository = processedFieldRepository;
    }

    @Override
    public Submission save(Submission submission) {
        SubmissionEntity entity = toEntity(submission);
        entity = submissionRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public List<SubmissionField> saveSubmissionFields(List<SubmissionField> submissionFields) {
        List<SubmissionFieldEntity> entities = submissionFields.stream()
                .map(this::toEntity)
                .toList();

        entities = submissionFieldRepository.saveAll(entities);

        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<ProcessedField> saveProcessedFields(List<ProcessedField> processedFields) {

        List<ProcessedFieldEntity> entities = processedFields.stream()
                .map(this::toEntity)
                .toList();

        entities = processedFieldRepository.saveAll(entities);

        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<SubmissionField> getSubmissionFieldsBySubmission(UUID id) {

        var entities = submissionFieldRepository.findBySubmissionIdWithField(id);

        return entities.stream()
                .map(this::toDomain)
                .toList();

    }

    @Override
    public List<Submission> pullPendingSubmisions(Integer quantity) {
        var entities = submissionRepository.findPendingSubmissions(quantity);
        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    private LayoutEntity toEntity(Layout layout) {
        return new LayoutEntity(
                layout.getId(),
                layout.getExternalKey(),
                layout.getName(),
                layout.getStatus()
        );
    }

    private SubmissionEntity toEntity(Submission submission) {
        return new SubmissionEntity(
                submission.getId(),
                toEntity(submission.getLayout()),
                submission.getStatus(),
                submission.getReceivedAt()
        );
    }

    private Layout toDomain(LayoutEntity entity) {
        return new Layout(
                entity.getId(),
                entity.getExternalKey(),
                entity.getName(),
                entity.getStatus()
        );
    }

    private Submission toDomain(SubmissionEntity entity) {
        Layout layout = toDomain(entity.getLayout());

        return new Submission(
                entity.getId(),
                layout,
                entity.getStatus(),
                entity.getReceivedAt()
        );
    }

    private SubmissionFieldEntity toEntity(SubmissionField submissionField) {
        SubmissionEntity submissionEntity = new SubmissionEntity(
                submissionField.getSubmission().getId(),
                toEntity(submissionField.getSubmission().getLayout()),
                submissionField.getSubmission().getStatus(),
                submissionField.getSubmission().getReceivedAt()
        );

        FieldEntity fieldEntity = new FieldEntity(
                submissionField.getField().getId(),
                submissionField.getField().getExternalKey(),
                submissionField.getField().getName(),
                toEntity(submissionField.getField().getLayout()),
                submissionField.getField().getSource(),
                submissionField.getField().getFormula(),
                submissionField.getField().getFieldType(),
                submissionField.getField().getCalculationOrder()
        );

        return new SubmissionFieldEntity(
                submissionField.getId(),
                submissionEntity,
                fieldEntity,
                submissionField.getValue()
        );
    }

    private SubmissionField toDomain(SubmissionFieldEntity entity) {
        LayoutEntity submissionLayoutEntity = entity.getSubmission().getLayout();
        LayoutEntity fieldLayoutEntity = entity.getField().getLayout();
        Layout layout = toDomain(submissionLayoutEntity != null ? submissionLayoutEntity : fieldLayoutEntity);

        Submission submission = new Submission(
                entity.getSubmission().getId(),
                layout,
                entity.getSubmission().getStatus(),
                entity.getSubmission().getReceivedAt()
        );

        Layout fieldLayout = fieldLayoutEntity != null ? toDomain(fieldLayoutEntity) : layout;

        Field field = new Field(
                entity.getField().getId(),
                entity.getField().getExternalKey(),
                entity.getField().getName(),
                fieldLayout,
                entity.getField().getSource(),
                entity.getField().getFormula(),
                entity.getField().getFieldType(),
                entity.getField().getCalculationOrder()
        );

        return new SubmissionField(submission, field, entity.getValue());
    }

    private ProcessedFieldEntity toEntity(ProcessedField processedField) {

        SubmissionEntity submissionEntity = new SubmissionEntity(
                processedField.getSubmission().getId(),
                toEntity(processedField.getSubmission().getLayout()),
                processedField.getSubmission().getStatus(),
                processedField.getSubmission().getReceivedAt()
        );

        FieldEntity fieldEntity = new FieldEntity(
                processedField.getField().getId(),
                processedField.getField().getExternalKey(),
                processedField.getField().getName(),
                toEntity(processedField.getField().getLayout()),
                processedField.getField().getSource(),
                processedField.getField().getFormula(),
                processedField.getField().getFieldType(),
                processedField.getField().getCalculationOrder()
        );

        return new ProcessedFieldEntity(
                processedField.getId(),
                submissionEntity,
                fieldEntity,
                processedField.getValue()
        );
    }

    private ProcessedField toDomain(ProcessedFieldEntity entity) {

        LayoutEntity submissionLayoutEntity = entity.getSubmission().getLayout();
        LayoutEntity fieldLayoutEntity = entity.getField().getLayout();
        Layout layout = toDomain(submissionLayoutEntity != null ? submissionLayoutEntity : fieldLayoutEntity);

        Submission submission = new Submission(
                entity.getSubmission().getId(),
                layout,
                entity.getSubmission().getStatus(),
                entity.getSubmission().getReceivedAt()
        );

        Layout fieldLayout = fieldLayoutEntity != null ? toDomain(fieldLayoutEntity) : layout;

        Field field = new Field(
                entity.getField().getId(),
                entity.getField().getExternalKey(),
                entity.getField().getName(),
                fieldLayout,
                entity.getField().getSource(),
                entity.getField().getFormula(),
                entity.getField().getFieldType(),
                entity.getField().getCalculationOrder()
        );

        return new ProcessedField(submission, field, entity.getValue());
    }

}

