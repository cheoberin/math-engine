package com.cheobs.math_engine.application.service;

import com.cheobs.math_engine.domain.model.common.ConflictException;
import com.cheobs.math_engine.domain.model.common.NotFoundException;
import com.cheobs.math_engine.domain.model.field.Field;
import com.cheobs.math_engine.domain.model.field.FieldSource;
import com.cheobs.math_engine.domain.model.layout.LayoutStatus;
import com.cheobs.math_engine.domain.model.submission.*;
import com.cheobs.math_engine.domain.port.input.SubmissionUseCase;
import com.cheobs.math_engine.domain.port.output.FieldPort;
import com.cheobs.math_engine.domain.port.output.LayoutPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SubmissionService implements SubmissionUseCase {

    private final LayoutPort layoutPort;
    private final FieldPort fieldPort;

    public SubmissionService(
            LayoutPort layoutPort,
            FieldPort fieldPort
    ) {
        this.layoutPort = layoutPort;
        this.fieldPort = fieldPort;
    }

    @Override
    @Transactional
    public Submission createSubmission(SubmissionCommand command) {

        var layout = layoutPort.getByExternalKey(command.layoutExternalKey())
                .orElseThrow(() -> new NotFoundException("layout external key not found", "not-found.layout"));

        if (LayoutStatus.INACTIVE.equals(layout.getStatus())) {
            throw new ConflictException("Cannot submit to an inactive layout: " + command.layoutExternalKey(), "conflict.layout.inactive");
        }

        var inputFields = fieldPort.getByLayoutIdAndFieldType(layout.getId(), FieldSource.INPUT);

        validateSubmissionFields(command.fields(), inputFields);

        Submission submission = new Submission(layout);

        List<SubmissionField> submissionFields = inputFields.stream().map(
                field -> new SubmissionField(

                )
        )


        return null;
    }

    private void validateSubmissionFields(List<SubmissionFieldCommand> submissionFields, List<Field> expectedFields) {

        Set<String> duplicateFields = getDuplicateFields(submissionFields);

        Set<String> submissionCodes = submissionFields.stream().map(SubmissionFieldCommand::fieldExternalKey).collect(Collectors.toSet());
        Set<String> expectedCodes = expectedFields.stream().map(Field::getExternalKey).collect(Collectors.toSet());

        Set<String> missingCodes = new HashSet<>(expectedCodes);
        missingCodes.removeAll(submissionCodes);

        Set<String> notExpectedCodes = new HashSet<>(submissionCodes);
        notExpectedCodes.removeAll(expectedCodes);

        if (!duplicateFields.isEmpty() || !missingCodes.isEmpty() || !notExpectedCodes.isEmpty()) {
            throw new FieldSubmissionValidationException(
                    "Submission field validation failed",
                    "validation.submission.fields",
                    duplicateFields,
                    missingCodes,
                    notExpectedCodes
            );
        }

    }

    private static Set<String> getDuplicateFields(List<SubmissionFieldCommand> submissionFields) {
        Set<String> seenFields = new HashSet<>();
        Set<String> duplicateFields = new HashSet<>();

        for (SubmissionFieldCommand fieldCommand : submissionFields) {

            Optional<String> keyOpt = fieldCommand.fieldExternalKey().describeConstable();
            String key = keyOpt.orElseThrow(
                    () -> new ConflictException("Field external key cannot be null: " + fieldCommand.fieldExternalKey(), "conflict.field.external-key.null")
            );

            if (!seenFields.add(key)) {
                duplicateFields.add(key);
            }
        }

        return duplicateFields;
    }
}
