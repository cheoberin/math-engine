package com.cheobs.math_engine.adapter.input.http.controller;

import com.cheobs.math_engine.adapter.input.http.api.SubmissionApi;
import com.cheobs.math_engine.adapter.input.http.dto.submission.reponse.SubmissionResponse;
import com.cheobs.math_engine.adapter.input.http.dto.submission.request.SubmissionRequestDto;
import com.cheobs.math_engine.domain.port.input.SubmissionUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubmissionController implements SubmissionApi {


    private final SubmissionUseCase submissionUseCase;

    public SubmissionController(SubmissionUseCase submissionUseCase) {
        this.submissionUseCase = submissionUseCase;
    }

    @Override
    public ResponseEntity<SubmissionResponse> submitData(SubmissionRequestDto submissionRequest) {
        var domain = submissionUseCase.createSubmission(submissionRequest.toCommand());
        var response = new SubmissionResponse(domain);
        return ResponseEntity.ok(response);
    }
}
