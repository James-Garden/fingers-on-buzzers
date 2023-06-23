package io.fingersonbuzzers.backend.lobby;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LobbyForm(@JsonProperty String playerName,
                        @JsonDeserialize(using = UUIDDeserializer.class)
                        @JsonProperty UUID playerId,
                        @JsonDeserialize(using = UUIDDeserializer.class)
                        @JsonProperty UUID lobbyId) {

  enum FormType {
    JOIN_LOBBY, CREATE_LOBBY
  }
  public static final String PLAYER_NAME_FIELD = "playerName";
  public static final String PLAYER_ID_FIELD = "playerId";
  public static final String LOBBY_ID_FIELD = "lobbyId";
}
