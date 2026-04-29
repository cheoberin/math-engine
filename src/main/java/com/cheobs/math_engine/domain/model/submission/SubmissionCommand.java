package com.cheobs.math_engine.domain.model.submission;

import java.util.List;

public record SubmissionCommand(
        String layoutExternalKey,
        List<SubmissionFieldCommand> fields
) {
}
