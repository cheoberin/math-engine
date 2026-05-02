package com.cheobs.math_engine.domain.port.output;

import com.cheobs.math_engine.domain.model.submission.ProcessedField;
import com.cheobs.math_engine.domain.model.submission.Submission;
import com.cheobs.math_engine.domain.model.submission.SubmissionField;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubmissionPort {

    Submission save(Submission submission);

    List<SubmissionField> saveSubmissionFields(List<SubmissionField> submissionFields);

    List<SubmissionField> getSubmissionFieldsBySubmission(UUID id);

    Optional<Submission> getById(UUID id);

    List<ProcessedField> getProcessedFieldsBySubmission(UUID id);

    List<ProcessedField> saveProcessedFields(List<ProcessedField> processedFields);

    List<Submission> pullPendingSubmisions(Integer quantity);
}
