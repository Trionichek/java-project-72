DROP TABLE IF EXISTS url_check CASCADE;
DROP TABLE IF EXISTS urls CASCADE;

CREATE TABLE urls (
    id         BIGINT GENERATED ALWAYS AS IDENTITY UNIQUE NOT NULL,
    name       VARCHAR(255)                               NOT NULL,
    created_at TIMESTAMP                                  NOT NULL
);

CREATE TABLE url_check (
    id          BIGINT GENERATED ALWAYS AS IDENTITY UNIQUE NOT NULL,
    url_id      BIGINT REFERENCES urls (id)                NOT NULL,
    status_code INT,
    h1          VARCHAR(255),
    title       VARCHAR(255),
    description TEXT,
    created_at  TIMESTAMP
);
