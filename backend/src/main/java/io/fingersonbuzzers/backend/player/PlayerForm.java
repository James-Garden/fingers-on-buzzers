package io.fingersonbuzzers.backend.player;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PlayerForm(@JsonProperty String playerName) {
}
