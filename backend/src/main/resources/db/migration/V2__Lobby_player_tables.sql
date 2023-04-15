CREATE TABLE lobbies(
    id UUID PRIMARY KEY,
    host_id UUID,
    created_timestamp TIMESTAMP
);

CREATE INDEX lobby_host_id_idx ON lobbies(host_id);

CREATE TABLE players(
    id UUID PRIMARY KEY,
    lobby_id UUID,
    name TEXT,
    created_timestamp TIMESTAMP,
    updated_timestamp TIMESTAMP,
    CONSTRAINT player_lobby_id_fk FOREIGN KEY (lobby_id) REFERENCES lobbies(id)
);

CREATE INDEX player_lobby_id_idx ON players(lobby_id);

ALTER TABLE lobbies ADD CONSTRAINT lobby_host_id_fk FOREIGN KEY (host_id) REFERENCES players(id);
