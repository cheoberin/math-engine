package com.cheobs.math_engine.adapter.input.http.api;

import com.cheobs.math_engine.adapter.input.http.dto.submission.reponse.SubmissionResponse;
import com.cheobs.math_engine.adapter.input.http.dto.submission.request.SubmissionRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Submission", description = "Endpoints for Submission Management")
@RequestMapping("/api/submissions")
public interface SubmissionApi {

    @PostMapping
    @Operation(summary = "Create a new data submission for evaluation")
    ResponseEntity<SubmissionResponse> submitData(@RequestBody @Valid SubmissionRequestDto submissionRequest);

}
