package com.cheobs.math_engine.adapter.input.http.api;

import com.cheobs.math_engine.adapter.input.http.dto.submission.reponse.SubmissionDetailsResponseDto;
import com.cheobs.math_engine.adapter.input.http.dto.submission.reponse.SubmissionResponseDto;
import com.cheobs.math_engine.adapter.input.http.dto.submission.request.SubmissionRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Submission", description = "Endpoints for Submission Management")
@RequestMapping("/api/submissions")
public interface SubmissionApi {

    @PostMapping
    @Operation(summary = "Create a new data submission for evaluation")
    ResponseEntity<SubmissionResponseDto> submitData(@RequestBody @Valid SubmissionRequestDto submissionRequest);

    @GetMapping("/{id}")
    @Operation(summary = "Get the status and results of a specific submission by its ID")
    ResponseEntity<SubmissionDetailsResponseDto> getSubmissionStatus(@PathVariable UUID id);

}
