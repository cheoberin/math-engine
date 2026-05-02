package com.cheobs.math_engine.adapter.input.http.dto.submission.reponse;

import com.cheobs.math_engine.domain.model.submission.SubmissionDetails;
import com.cheobs.math_engine.domain.model.submission.SubmissionStatus;

import java.util.List;
import java.util.UUID;

public record SubmissionDetailsResponseDto(
        UUID id,
        UUID layoutId,
        String layoutExternalKey,
        SubmissionStatus status,
        List<SubmissionFieldResponseDto> fieldResponseDtos
) {

    public SubmissionDetailsResponseDto(SubmissionDetails details) {
        this(
                details.id(),
                details.layoutId(),
                details.layoutExternalKey(),
                details.status(),
                details.fields().stream().map(SubmissionFieldResponseDto::new).toList()
        );
    }
}
