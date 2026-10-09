CREATE TABLE users_details (
    user_id INTEGER PRIMARY KEY,
    nickname VARCHAR(60) NOT NULL UNIQUE,
    avatar_url TEXT NULL DEFAULT './images/avatar_defaultUrl.png',
    role_id INTEGER NOT NULL,
    updated_at TIMESTAMP NULL,

    CONSTRAINT fk_users_details_user
        FOREIGN KEY (user_id)
        REFERENCES users_auth(id),

    CONSTRAINT fk_users_role_user
        FOREIGN KEY (role_id)
        REFERENCES users_role(id)
);