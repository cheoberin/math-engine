package com.cheobs.math_engine.adapter.input.http.dto.submission.request;

import com.cheobs.math_engine.domain.model.submission.SubmissionCommand;
import com.cheobs.math_engine.domain.model.submission.SubmissionFieldCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record SubmissionRequestDto(@NotBlank String layout,
                                   @Valid @NotEmpty @NotNull List<SubmissionFieldRequestDto> fields) {

    public SubmissionCommand toCommand() {
        return new SubmissionCommand(
                layout,
                fields.stream().map(
                        field ->
                                new SubmissionFieldCommand(
                                        field.fieldCode(),
                                        field.value()
                                )
                ).toList()
        );
    }
}
