CREATE TABLE questions
(
    id                UUID PRIMARY KEY,
    question_text     TEXT,
    answer            TEXT,
    created_timestamp TIMESTAMP,
    updated_timestamp TIMESTAMP
);
