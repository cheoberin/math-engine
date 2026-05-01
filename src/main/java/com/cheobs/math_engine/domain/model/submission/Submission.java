package com.cheobs.math_engine.domain.model.submission;

import com.cheobs.math_engine.domain.model.layout.Layout;

import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class Submission {

    private UUID id;
    private final Layout layout;
    private SubmissionStatus status;
    private final OffsetDateTime receivedAt;

    public Submission(UUID id, Layout layout, SubmissionStatus status, OffsetDateTime receivedAt) {
        this.id = id;
        this.layout = Objects.requireNonNull(layout, "Layout cannot be null");
        this.status = Objects.requireNonNull(status, "Status cannot be null");
        this.receivedAt = Objects.requireNonNull(receivedAt, "ReceivedAt cannot be null");
    }

    public Submission(Layout layout) {
        this.layout = Objects.requireNonNull(layout, "Layout cannot be null");
        this.status = SubmissionStatus.PENDING;
        this.receivedAt = OffsetDateTime.now();
    }

    public void markAsProcessing(){
        this.status = SubmissionStatus.PROCESSING;
    }

    public void completeProcessing(){
        this.status = SubmissionStatus.COMPLETED;
    }

    public void failProcessing(){
        this.status = SubmissionStatus.FAILED;
    }

    public UUID getId() {
        return id;
    }

    public Layout getLayout() {
        return layout;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public OffsetDateTime getReceivedAt() {
        return receivedAt;
    }

}
