package com.cheobs.math_engine.adapter.input.http.dto.submission.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record SubmissionFieldRequestDto(
        @NotBlank
        String fieldCode,
        @NotNull
        BigDecimal value
) {
}
