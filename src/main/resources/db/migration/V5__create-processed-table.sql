CREATE TABLE processed_field
(
    id            UUID PRIMARY KEY,
    submission_id UUID           NOT NULL,
    field_id      UUID           NOT NULL,
    value         DECIMAL(19, 6) NOT NULL,
    UNIQUE (submission_id, field_id),
    FOREIGN KEY (submission_id) REFERENCES submission (id),
    FOREIGN KEY (field_id) REFERENCES field (id)
);

CREATE INDEX idx_processed_field_submission_id ON processed_field (submission_id);
CREATE INDEX idx_processed_field_field_id ON processed_field (field_id);

