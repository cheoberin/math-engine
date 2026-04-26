CREATE TABLE layout
(
    id           UUID PRIMARY KEY,
    external_key VARCHAR(4)   NOT NULL UNIQUE,
    name         VARCHAR(255) NOT NULL,
    status       VARCHAR(10)  NOT NULL
);