package com.cheobs.math_engine.adapter.output.jpa.implementation;

import com.cheobs.math_engine.adapter.output.jpa.entity.FieldEntity;
import com.cheobs.math_engine.adapter.output.jpa.entity.LayoutEntity;
import com.cheobs.math_engine.adapter.output.jpa.entity.SubmissionEntity;
import com.cheobs.math_engine.adapter.output.jpa.entity.SubmissionFieldEntity;
import com.cheobs.math_engine.adapter.output.jpa.repository.SubmissionFieldRepository;
import com.cheobs.math_engine.adapter.output.jpa.repository.SubmissionRepository;
import com.cheobs.math_engine.domain.model.field.Field;
import com.cheobs.math_engine.domain.model.layout.Layout;
import com.cheobs.math_engine.domain.model.submission.Submission;
import com.cheobs.math_engine.domain.model.submission.SubmissionField;
import com.cheobs.math_engine.domain.port.output.SubmissionPort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubmissionAdapter implements SubmissionPort {

    private final SubmissionRepository submissionRepository;
    private final SubmissionFieldRepository submissionFieldRepository;

    public SubmissionAdapter(
            SubmissionRepository submissionRepository,
            SubmissionFieldRepository submissionFieldRepository
    ) {
        this.submissionRepository = submissionRepository;
        this.submissionFieldRepository = submissionFieldRepository;
    }

    @Override
    public Submission save(Submission submission) {
        SubmissionEntity entity = toEntity(submission);
        entity = submissionRepository.save(entity);
        return toDomain(entity);
    }

    @Override
    public List<SubmissionField> save(List<SubmissionField> submissionFields) {
        List<SubmissionFieldEntity> entities = submissionFields.stream()
                .map(this::toEntity)
                .toList();

        entities = submissionFieldRepository.saveAll(entities);

        return entities.stream()
                .map(this::toDomain)
                .toList();
    }

    private SubmissionEntity toEntity(Submission submission) {
        LayoutEntity layoutEntity = new LayoutEntity(
                submission.getLayout().getId(),
                submission.getLayout().getExternalKey(),
                submission.getLayout().getName(),
                submission.getLayout().getStatus()
        );

        return new SubmissionEntity(
                submission.getId(),
                layoutEntity,
                submission.getStatus(),
                submission.getReceivedAt()
        );
    }

    private Submission toDomain(SubmissionEntity entity) {
        Layout layout = new Layout(
                entity.getLayout().getId(),
                entity.getLayout().getExternalKey(),
                entity.getLayout().getName(),
                entity.getLayout().getStatus()
        );

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
                null,
                submissionField.getSubmission().getStatus(),
                submissionField.getSubmission().getReceivedAt()
        );

        FieldEntity fieldEntity = new FieldEntity(
                submissionField.getField().getId(),
                submissionField.getField().getExternalKey(),
                submissionField.getField().getName(),
                null,
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
        Layout layout = new Layout(
                entity.getSubmission().getLayout().getId(),
                entity.getSubmission().getLayout().getExternalKey(),
                entity.getSubmission().getLayout().getName(),
                entity.getSubmission().getLayout().getStatus()
        );

        Submission submission = new Submission(
                entity.getSubmission().getId(),
                layout,
                entity.getSubmission().getStatus(),
                entity.getSubmission().getReceivedAt()
        );

        Field field = new Field(
                entity.getField().getId(),
                entity.getField().getExternalKey(),
                entity.getField().getName(),
                layout,
                entity.getField().getSource(),
                entity.getField().getFormula(),
                entity.getField().getFieldType(),
                entity.getField().getCalculationOrder()
        );

        return new SubmissionField(submission, field, entity.getValue());
    }
}

