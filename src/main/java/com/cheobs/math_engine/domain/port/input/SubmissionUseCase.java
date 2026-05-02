package com.cheobs.math_engine.domain.port.input;

import com.cheobs.math_engine.domain.model.submission.Submission;
import com.cheobs.math_engine.domain.model.submission.SubmissionCommand;
import com.cheobs.math_engine.domain.model.submission.SubmissionDetails;

import java.util.UUID;

public interface SubmissionUseCase {

    Submission createSubmission(SubmissionCommand command);
    
    SubmissionDetails getSubmissionDetails(UUID id);
    
}
