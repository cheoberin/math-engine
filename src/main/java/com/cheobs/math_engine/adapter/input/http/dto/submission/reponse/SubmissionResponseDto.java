package com.cheobs.math_engine.adapter.input.http.dto.submission.reponse;

import com.cheobs.math_engine.domain.model.submission.Submission;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SubmissionResponseDto(UUID submissionId, OffsetDateTime receivedAt) {

    public SubmissionResponseDto(Submission submission) {
        this(submission.getId(), submission.getReceivedAt());
    }

}
