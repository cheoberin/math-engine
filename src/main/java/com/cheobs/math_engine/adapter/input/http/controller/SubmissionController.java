package com.cheobs.math_engine.adapter.input.http.controller;

import com.cheobs.math_engine.adapter.input.http.api.SubmissionApi;
import com.cheobs.math_engine.adapter.input.http.dto.submission.reponse.SubmissionDetailsResponseDto;
import com.cheobs.math_engine.adapter.input.http.dto.submission.reponse.SubmissionResponseDto;
import com.cheobs.math_engine.adapter.input.http.dto.submission.request.SubmissionRequestDto;
import com.cheobs.math_engine.domain.port.input.SubmissionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class SubmissionController implements SubmissionApi {

    private final SubmissionUseCase submissionUseCase;

    public SubmissionController(SubmissionUseCase submissionUseCase) {
        this.submissionUseCase = submissionUseCase;
    }

    @Override
    public ResponseEntity<SubmissionResponseDto> submitData(SubmissionRequestDto submissionRequest) {
        var domain = submissionUseCase.createSubmission(submissionRequest.toCommand());
        var response = new SubmissionResponseDto(domain);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<SubmissionDetailsResponseDto> getSubmissionStatus(UUID id) {
        var domain = submissionUseCase.getSubmissionDetails(id);
        var response = new SubmissionDetailsResponseDto(domain);
        return ResponseEntity.ok(response);
    }
}
