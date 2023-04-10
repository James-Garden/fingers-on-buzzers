package io.fingersonbuzzers.backend.lobby;

import io.fingersonbuzzers.backend.player.Player;
import io.fingersonbuzzers.backend.player.PlayerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LobbyServiceTest {

  @Mock
  private LobbyRepository lobbyRepository;

  @Mock
  private PlayerRepository playerRepository;

  @InjectMocks
  private LobbyService lobbyService;

  @Test
  void createLobby() {
    var lobby = new Lobby();
    var player = new Player();

    lobbyService.createLobby(lobby, player);

    verify(lobbyRepository).save(lobby);
    verify(playerRepository).save(player);
  }
}
