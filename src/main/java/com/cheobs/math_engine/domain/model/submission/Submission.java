package com.cheobs.math_engine.domain.model.submission;

import com.cheobs.math_engine.domain.model.layout.Layout;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Submission {

    private UUID id;
    private final Layout layout;
    private SubmissionStatus status;
    private final OffsetDateTime receivedAt;

    public Submission(UUID id, Layout layout, SubmissionStatus status, OffsetDateTime receivedAt) {
        this.id = id;
        this.layout = layout;
        this.status = status;
        this.receivedAt = receivedAt;
    }

    public Submission(Layout layout) {
        this.layout = layout;
        this.status = SubmissionStatus.PENDING;
        this.receivedAt = OffsetDateTime.now();
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
