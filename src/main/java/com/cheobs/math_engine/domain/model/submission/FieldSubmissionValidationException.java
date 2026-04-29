package com.cheobs.math_engine.domain.model.submission;

import java.util.Set;

public class FieldSubmissionValidationException extends RuntimeException {

    public final String identifier;
    public final Set<String> duplicateCodes;
    public final Set<String> missingCodes;
    public final Set<String> notExpectedCodes;

    public FieldSubmissionValidationException(
            String message,
            String identifier,
            Set<String> duplicateCodes,
            Set<String> missingCodes,
            Set<String> notExpectedCodes) {
        super(message);
        this.identifier = identifier;
        this.duplicateCodes = duplicateCodes;
        this.missingCodes = missingCodes;
        this.notExpectedCodes = notExpectedCodes;
    }

    public String getIdentifier() {
        return identifier;
    }

    public Set<String> getDuplicateCodes() {
        return duplicateCodes;
    }

    public Set<String> getMissingCodes() {
        return missingCodes;
    }

    public Set<String> getNotExpectedCodes() {
        return notExpectedCodes;
    }
}
