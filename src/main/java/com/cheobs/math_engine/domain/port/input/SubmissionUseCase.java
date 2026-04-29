package com.cheobs.math_engine.domain.port.input;

import com.cheobs.math_engine.domain.model.submission.Submission;
import com.cheobs.math_engine.domain.model.submission.SubmissionCommand;

public interface SubmissionUseCase {

    Submission createSubmission(SubmissionCommand command);

}
