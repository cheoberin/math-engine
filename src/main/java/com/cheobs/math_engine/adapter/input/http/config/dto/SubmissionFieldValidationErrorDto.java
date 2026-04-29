package com.cheobs.math_engine.adapter.input.http.config.dto;

import java.util.Set;

public record SubmissionFieldValidationErrorDto(
        String message,
        String identifier,
        Set<String> duplicatedFields,
        Set<String> missingFields,
        Set<String> notExpectedFields
) {
}
