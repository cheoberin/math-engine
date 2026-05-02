package com.cheobs.math_engine.domain.model.submission;

import java.util.List;
import java.util.UUID;

public record SubmissionDetails(
        UUID id,
        UUID layoutId,
        String layoutExternalKey,
        SubmissionStatus status,
        List<SubmissionDetailsField> fields
) {
}

