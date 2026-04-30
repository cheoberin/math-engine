CREATE TABLE submission
(
    id          UUID PRIMARY KEY,
    layout_id   UUID        NOT NULL,
    status      VARCHAR(10) NOT NULL,
    received_at TIMESTAMP WITH TIME ZONE NOT NULL,
    FOREIGN KEY (layout_id) REFERENCES layout (id)
);

CREATE TABLE submission_field
(
    id            UUID PRIMARY KEY,
    submission_id UUID           NOT NULL,
    field_id      UUID           NOT NULL,
    value         DECIMAL(19, 5) NOT NULL,
    UNIQUE (submission_id, field_id),
    FOREIGN KEY (submission_id) REFERENCES submission (id),
    FOREIGN KEY (field_id) REFERENCES field (id)
);

CREATE INDEX idx_submission_layout_id ON submission (layout_id);
CREATE INDEX idx_submission_field_submission_id ON submission_field (submission_id);
CREATE INDEX idx_submission_field_field_id ON submission_field (field_id);

