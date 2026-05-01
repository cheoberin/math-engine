package com.cheobs.math_engine.adapter.input.http.config;

import com.cheobs.math_engine.adapter.input.http.config.dto.ErrorDto;
import com.cheobs.math_engine.adapter.input.http.config.dto.FieldErrorDto;
import com.cheobs.math_engine.adapter.input.http.config.dto.SubmissionFieldValidationErrorDto;
import com.cheobs.math_engine.domain.model.common.exceptions.ConflictException;
import com.cheobs.math_engine.domain.model.common.exceptions.NotFoundException;
import com.cheobs.math_engine.domain.model.common.exceptions.ValidationException;
import com.cheobs.math_engine.domain.model.submission.FieldSubmissionValidationException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdviser {

    private final Logger logger = LoggerFactory.getLogger(ControllerAdviser.class);

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDto> handleDocumentNotFoundException(NotFoundException error) {

        logger.warn(error.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(
                        new ErrorDto(error.getMessage(), error.getIdentifier())
                );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<FieldErrorDto>> argumentNotValid(MethodArgumentNotValidException error) {

        logger.warn(error.getMessage());

        var fields = error.getFieldErrors().stream().map(
                fieldError -> new FieldErrorDto(
                        fieldError.getField(),
                        fieldError.getDefaultMessage()
                )).toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(fields);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorDto> handleConflictException(ConflictException error) {

        logger.warn(error.getMessage());

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorDto(error.getMessage(), error.getIdentifier()));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDto> handleValidationException(ValidationException error) {

        logger.warn(error.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ErrorDto(error.getMessage(), error.getIdentifier()));
    }

    @ExceptionHandler(FieldSubmissionValidationException.class)
    public ResponseEntity<SubmissionFieldValidationErrorDto> handleFieldSubmissionValidationException(FieldSubmissionValidationException error) {
        logger.warn(error.getMessage());

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new SubmissionFieldValidationErrorDto(
                        error.getMessage(),
                        error.identifier,
                        error.getDuplicateCodes(),
                        error.getMissingCodes(),
                        error.getNotExpectedCodes()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(Exception e, HttpServletRequest request) {

        logger.error("Unexpected error on {} {}", request.getMethod(), request.getRequestURI(), e);

        Map<String, Object> body = Map.of(
                "status", 500,
                "error", "Internal Server Error",
                "message", e.getMessage(), "traceId",
                MDC.get("traceId"));

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

}
