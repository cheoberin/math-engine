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
import java.util.UUID;

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

    @Override
    public List<SubmissionField> getSubmissionFieldsBySubmission(UUID id) {

        var entities = submissionFieldRepository.findBySubmissionIdWithField(id);

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
}

