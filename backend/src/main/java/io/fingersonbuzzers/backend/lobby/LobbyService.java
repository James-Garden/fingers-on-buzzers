package io.fingersonbuzzers.backend.lobby;

import io.fingersonbuzzers.backend.player.Player;
import io.fingersonbuzzers.backend.player.PlayerDto;
import io.fingersonbuzzers.backend.player.PlayerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LobbyService {

  private static final Logger LOGGER = LoggerFactory.getLogger(LobbyService.class);

  private final LobbyRepository lobbyRepository;
  private final PlayerRepository playerRepository;

  @Autowired
  public LobbyService(LobbyRepository lobbyRepository,
                      PlayerRepository playerRepository) {
    this.lobbyRepository = lobbyRepository;
    this.playerRepository = playerRepository;
  }

  public CreateLobbyResponse createLobby(LobbyForm form) {
    try {
      return createLobbyUnchecked(form);
    } catch (EntityNotFoundException exception) {
      var message = "Failed to create Lobby as Player with [playerId=%s] and [playerName=%s] could not be found"
          .formatted(form.playerId(), form.playerName());
      LOGGER.error(message, exception);
      throw new EntityNotFoundException(message, exception);
    }
  }

  @Transactional
  public CreateLobbyResponse createLobbyUnchecked(LobbyForm form) throws EntityNotFoundException {
    var player = Optional.ofNullable(form.playerId())
        .map(playerRepository::getById)
        .orElse(new Player());
    player.setName(form.playerName());
    playerRepository.save(player);

    var lobby = new Lobby();
    lobby.setHost(player);
    lobbyRepository.save(lobby);

    player.setLobby(lobby);
    playerRepository.save(player);

    return CreateLobbyResponse.success(LobbyDto.from(lobby), PlayerDto.from(player));
  }

}
