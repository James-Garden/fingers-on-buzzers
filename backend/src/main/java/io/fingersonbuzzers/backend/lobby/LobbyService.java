package io.fingersonbuzzers.backend.lobby;

import io.fingersonbuzzers.backend.player.Player;
import io.fingersonbuzzers.backend.player.PlayerRepository;
import jakarta.transaction.Transactional;
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
  public void createLobby(Lobby lobby, Player player) {
    lobbyRepository.save(lobby);
    playerRepository.save(player);
  }
}
