package io.fingersonbuzzers.backend.lobby;

import io.fingersonbuzzers.backend.player.Player;
import io.fingersonbuzzers.backend.player.PlayerDto;
import io.fingersonbuzzers.backend.player.PlayerRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LobbyService {


  private final LobbyRepository lobbyRepository;
  private final PlayerRepository playerRepository;

  @Autowired
  public LobbyService(LobbyRepository lobbyRepository,
                      PlayerRepository playerRepository) {
    this.lobbyRepository = lobbyRepository;
    this.playerRepository = playerRepository;
  }

  @Transactional
  public LobbyResponse createLobby(LobbyForm form) {
    var player = Optional.ofNullable(form.playerId())
        .flatMap(playerRepository::findById)
        .orElse(new Player());
    player.setName(form.playerName());
    playerRepository.save(player);

    var lobby = new Lobby();
    lobby.setHost(player);
    lobbyRepository.save(lobby);

    player.setLobby(lobby);
    playerRepository.save(player);

    return LobbyResponse.success(LobbyDto.from(lobby), PlayerDto.from(player));
  }

  @Transactional
  public LobbyResponse joinLobby(LobbyForm form) throws EntityNotFoundException {
    var lobby = lobbyRepository.findById(form.lobbyId())
        .orElseThrow(() ->
            new EntityNotFoundException("Failed to join Lobby with [lobbyId=%s] as it could not be found"
                .formatted(form.lobbyId().toString())));
    var player = Optional.ofNullable(form.playerId())
        .flatMap(playerRepository::findById)
        .orElse(new Player());
    player.setName(form.playerName());
    player.setLobby(lobby);
    playerRepository.save(player);

    return LobbyResponse.success(LobbyDto.from(lobby), PlayerDto.from(player));
  }
}
