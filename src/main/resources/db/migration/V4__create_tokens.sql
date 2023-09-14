CREATE TABLE tokens (
    id         BIGINT PRIMARY KEY NOT NULL,
    expired    BOOLEAN            NOT NULL,
    revoked    BOOLEAN            NOT NULL,
    user_id    BIGINT             NOT NULL,
    token      VARCHAR(255) UNIQUE,
    token_type VARCHAR(255)
        CONSTRAINT tokens_token_type_check
            CHECK ((token_type)::text = 'BEARER'::text),
    FOREIGN KEY (user_id) REFERENCES users (id)
);