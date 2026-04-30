package com.cheobs.math_engine.domain.port.output;

import com.cheobs.math_engine.domain.model.submission.Submission;
import com.cheobs.math_engine.domain.model.submission.SubmissionField;

import java.util.List;

public interface SubmissionPort {

    Submission save(Submission submission);

    List<SubmissionField> save(List<SubmissionField> submissionFields);

}
