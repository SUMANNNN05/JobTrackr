CREATE TABLE users (
    id            BIGSERIAL PRIMARY KEY,
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    full_name     VARCHAR(120) NOT NULL,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

CREATE TABLE job_applications (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT       NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    company     VARCHAR(150) NOT NULL,
    role        VARCHAR(150) NOT NULL,
    location    VARCHAR(120),
    job_url     VARCHAR(500),
    status      VARCHAR(30)  NOT NULL,
    applied_on  DATE,
    notes       TEXT,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_application_status CHECK (status IN
        ('WISHLIST','APPLIED','ONLINE_ASSESSMENT','INTERVIEW','OFFER','REJECTED','WITHDRAWN'))
);

CREATE TABLE application_status_history (
    id             BIGSERIAL PRIMARY KEY,
    application_id BIGINT      NOT NULL REFERENCES job_applications (id) ON DELETE CASCADE,
    from_status    VARCHAR(30),
    to_status      VARCHAR(30) NOT NULL,
    changed_at     TIMESTAMPTZ NOT NULL DEFAULT now()
);
