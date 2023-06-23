package io.fingersonbuzzers.backend.lobby;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.fingersonbuzzers.backend.player.PlayerDto;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LobbyResponse(LobbyDto lobbyData,
                            PlayerDto playerData) {}
