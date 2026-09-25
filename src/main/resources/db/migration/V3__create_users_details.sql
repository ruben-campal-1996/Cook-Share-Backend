CREATE TABLE users_details (
    user_id INTEGER PRIMARY KEY,
    nickname VARCHAR(60) NOT NULL UNIQUE,
    avatar_url TEXT NULL,
    role_id INTEGER,
    updated_at TIMESTAMP,

    CONSTRAINT fk_users_details_user
        FOREIGN KEY (user_id)
        REFERENCES users_auth(id),

    CONSTRAINT fk_users_role_user
        FOREIGN KEY (role_id)
        REFERENCES users_role(id)
);