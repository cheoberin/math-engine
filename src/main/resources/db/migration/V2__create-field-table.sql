CREATE TABLE field
(
    id                 UUID PRIMARY KEY,
    external_key       VARCHAR(7)   NOT NULL,
    layout_id          UUID         NOT NULL,
    source             VARCHAR(20)  NOT NULL,
    formula            VARCHAR(500),
    field_type         VARCHAR(20)  NOT NULL,
    calculation_order  INTEGER,
    CONSTRAINT fk_field_layout FOREIGN KEY (layout_id) REFERENCES layout (id) ON DELETE CASCADE,
    CONSTRAINT uk_field_layout_external_key UNIQUE (layout_id, external_key)
);

CREATE INDEX idx_field_layout_id ON field (layout_id);

