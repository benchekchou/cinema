CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL CHECK (role IN ('ADMIN', 'CLIENT')),
    enabled BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE INDEX idx_app_user_email ON app_user (email);
