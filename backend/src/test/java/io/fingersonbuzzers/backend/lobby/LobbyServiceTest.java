package io.fingersonbuzzers.backend.lobby;

import io.fingersonbuzzers.backend.player.Player;
import io.fingersonbuzzers.backend.player.PlayerDto;
import io.fingersonbuzzers.backend.player.PlayerRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LobbyServiceTest {

  @Mock
  private LobbyRepository lobbyRepository;

  @Mock
  private PlayerRepository playerRepository;

  @InjectMocks
  private LobbyService lobbyService;

  @Captor
  private ArgumentCaptor<Player> playerArgumentCaptor;

  @Captor
  private ArgumentCaptor<Lobby> lobbyArgumentCaptor;

  @Test
  void createLobby_ExistingPlayer_PlayerNotFound_AssertThrows() {
    var form = new LobbyForm("test name", UUID.randomUUID());
    var expectedErrorMessage =
        "Failed to create Lobby as Player with [playerId=%s] and [playerName=%s] could not be found"
        .formatted(form.playerId(), form.playerName());

    when(playerRepository.getById(form.playerId())).thenCallRealMethod();
    when(playerRepository.findById(form.playerId())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> lobbyService.createLobby(form))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(expectedErrorMessage);
  }

  @Test
  void createLobby_ExistingPlayer_AssertSaves() {
    var playerId = UUID.randomUUID();
    var form = new LobbyForm("test name", playerId);
    var player = new Player();

    when(playerRepository.getById(form.playerId())).thenCallRealMethod();
    when(playerRepository.findById(form.playerId())).thenReturn(Optional.of(player));

    var response = lobbyService.createLobby(form);

    var inOrder = inOrder(playerRepository, lobbyRepository);

    inOrder.verify(playerRepository).getById(playerId);
    inOrder.verify(playerRepository).findById(playerId);
    inOrder.verify(playerRepository).save(player);
    inOrder.verify(lobbyRepository).save(lobbyArgumentCaptor.capture());
    inOrder.verify(playerRepository).save(player);
    inOrder.verifyNoMoreInteractions();

    assertThat(lobbyArgumentCaptor.getValue())
        .extracting(Lobby::getHost)
        .isEqualTo(player);
    assertThat(player)
        .extracting(Player::getLobby, Player::getName)
        .containsExactly(lobbyArgumentCaptor.getValue(), form.playerName());
    assertThat(response.playerData())
        .extracting(PlayerDto::playerName)
        .isEqualTo(form.playerName());
    assertThat(response.errors()).isNull();
  }

  @Test
  void createLobby_NewPlayer_AssertSaves() {
    var form = new LobbyForm("test name", null);

    var response = lobbyService.createLobby(form);

    var inOrder = inOrder(playerRepository, lobbyRepository);

    inOrder.verify(playerRepository).save(playerArgumentCaptor.capture());
    inOrder.verify(lobbyRepository).save(lobbyArgumentCaptor.capture());
    inOrder.verify(playerRepository).save(playerArgumentCaptor.capture());
    inOrder.verifyNoMoreInteractions();

    assertThat(lobbyArgumentCaptor.getValue())
        .extracting(Lobby::getHost)
        .isEqualTo(playerArgumentCaptor.getValue());
    assertThat(playerArgumentCaptor.getValue())
        .extracting(Player::getLobby, Player::getName)
        .containsExactly(lobbyArgumentCaptor.getValue(), form.playerName());
    assertThat(response.playerData())
        .extracting(PlayerDto::playerName)
        .isEqualTo(form.playerName());
    assertThat(response.errors()).isNull();
  }
}
