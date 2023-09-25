drop table IF EXISTS users, categories, location, events, requests, compilations, event_compilations, event_comments CASCADE;

CREATE SEQUENCE IF NOT EXISTS hibernate_sequence START 1;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT PRIMARY KEY,
    email TEXT NOT NULL,
    name  VARCHAR(250) NOT NULL
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT PRIMARY KEY,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT PRIMARY KEY,
    annotation         VARCHAR(2000) NOT NULL,
    category_id        INTEGER NOT NULL REFERENCES categories(id),
    confirmed_requests BIGINT DEFAULT 0,
    created_on         TIMESTAMP,
    description        VARCHAR(7000) NOT NULL,
    event_date         TIMESTAMP,
    user_id            INTEGER NOT NULL REFERENCES users(id),
    lat                REAL,
    lon                REAL,
    paid               BOOLEAN,
    participant_limit  INTEGER,
    published_on       TIMESTAMP,
    request_moderation BOOLEAN,
    state              TEXT,
    title              VARCHAR(120),
    views              BIGINT
);

CREATE TABLE IF NOT EXISTS participation_requests
(
    id           BIGINT PRIMARY KEY,
    created      TIMESTAMP,
    event_id     INTEGER,
    requester_id INTEGER,
    status       VARCHAR(10) NOT NULL,
    FOREIGN KEY (event_id) REFERENCES events (id),
    FOREIGN KEY (requester_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id     BIGINT PRIMARY KEY,
    pinned BOOLEAN,
    title  VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS compilation_event
(
    compilation_id BIGINT REFERENCES compilations(id),
    event_id BIGINT REFERENCES events(id),
    CONSTRAINT PK_event_compilations PRIMARY KEY (compilation_id, event_id)
);

CREATE TABLE IF NOT EXISTS event_comments (
    id BIGINT PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    author_id INTEGER NOT NULL,
    event_id INTEGER NOT NULL,
    comment VARCHAR(1000),
    FOREIGN KEY (author_id) REFERENCES users(id),
    FOREIGN KEY (event_id) REFERENCES events(id)
);