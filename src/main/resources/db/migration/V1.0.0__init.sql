-- USER
CREATE TABLE "user"
(
    id          BIGSERIAL PRIMARY KEY,
    keycloak_id VARCHAR(100) NOT NULL UNIQUE,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(255) NOT NULL UNIQUE,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- PROPERTY GROUP
CREATE TABLE property_group
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(50) NOT NULL,
    user_id_fk BIGINT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id_fk) REFERENCES "user" (id)
);

-- PROPERTY
CREATE TABLE property
(
    id                   BIGSERIAL PRIMARY KEY,
    name                 VARCHAR(50) NOT NULL,
    property_group_id_fk BIGINT,
    user_id_fk           BIGINT,
    description          VARCHAR(500),
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (property_group_id_fk) REFERENCES property_group (id),
    FOREIGN KEY (user_id_fk) REFERENCES "user" (id)
);

-- TAG
CREATE TABLE tag
(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(50) NOT NULL,
    color      VARCHAR(7)  NOT NULL DEFAULT '#000000',
    user_id_fk BIGINT      NOT NULL,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_tag_user_name UNIQUE (user_id_fk, name),
    FOREIGN KEY (user_id_fk) REFERENCES "user" (id) ON DELETE CASCADE
);

-- PROPERTY TAG
CREATE TABLE property_tag
(
    property_id_fk BIGINT NOT NULL,
    tag_id_fk      BIGINT NOT NULL,
    PRIMARY KEY (property_id_fk, tag_id_fk),
    FOREIGN KEY (property_id_fk) REFERENCES property (id) ON DELETE CASCADE,
    FOREIGN KEY (tag_id_fk) REFERENCES tag (id) ON DELETE CASCADE
);
