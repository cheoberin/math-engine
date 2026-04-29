package com.cheobs.math_engine.domain.model.submission;

import java.math.BigDecimal;

public record SubmissionFieldCommand(
        String fieldExternalKey,
        BigDecimal value
) {
}
