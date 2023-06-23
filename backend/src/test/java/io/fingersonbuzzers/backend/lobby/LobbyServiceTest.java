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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
  void createLobby_ExistingPlayer_PlayerNotFound_AssertCreatesNew() {
    var playerId = UUID.randomUUID();
    var form = new LobbyForm("test name", playerId, null);

    when(playerRepository.findById(form.playerId())).thenReturn(Optional.empty());

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
  }

  @Test
  void createLobby_ExistingPlayer_AssertSaves() {
    var playerId = UUID.randomUUID();
    var form = new LobbyForm("test name", playerId, null);
    var player = new Player();

    when(playerRepository.findById(form.playerId())).thenReturn(Optional.of(player));

    var response = lobbyService.createLobby(form);

    var inOrder = inOrder(playerRepository, lobbyRepository);

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
  }

  @Test
  void createLobby_NewPlayer_AssertSaves() {
    var form = new LobbyForm("test name", null, null);

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
  }

  @Test
  void joinLobby_LobbyNotFound_AssertThrows() {
    var form = new LobbyForm(null, null, UUID.randomUUID());
    var expectedErrorMessage = "Failed to join Lobby with [lobbyId=%s] as it could not be found"
        .formatted(form.lobbyId().toString());

    when(lobbyRepository.findById(form.lobbyId())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> lobbyService.joinLobby(form))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessage(expectedErrorMessage);

    verifyNoMoreInteractions(lobbyRepository, playerRepository);
  }

  @Test
  void joinLobby_NewPlayer_AssertSaves() {
    var form = new LobbyForm("test name", null, UUID.randomUUID());
    var lobby = new Lobby();

    when(lobbyRepository.findById(form.lobbyId())).thenReturn(Optional.of(lobby));

    var response = lobbyService.joinLobby(form);

    verify(playerRepository).save(playerArgumentCaptor.capture());
    verifyNoMoreInteractions(lobbyRepository, playerRepository);

    assertThat(playerArgumentCaptor.getValue())
        .extracting(Player::getLobby, Player::getName)
        .containsExactly(lobby, form.playerName());
    assertThat(response.playerData())
        .extracting(PlayerDto::playerName)
        .isEqualTo(form.playerName());
  }

  @Test
  void joinLobby_ExpiredPlayer_AssertSaves() {
    var form = new LobbyForm("test name", UUID.randomUUID(), UUID.randomUUID());
    var lobby = new Lobby();

    when(lobbyRepository.findById(form.lobbyId())).thenReturn(Optional.of(lobby));
    when(playerRepository.findById(form.playerId())).thenReturn(Optional.empty());

    var response = lobbyService.joinLobby(form);

    verify(playerRepository).save(playerArgumentCaptor.capture());
    verifyNoMoreInteractions(lobbyRepository, playerRepository);

    assertThat(playerArgumentCaptor.getValue())
        .extracting(Player::getLobby, Player::getName)
        .containsExactly(lobby, form.playerName());
    assertThat(response.playerData())
        .extracting(PlayerDto::playerName)
        .isEqualTo(form.playerName());
  }

  @Test
  void joinLobby_ExistingPlayer_AssertSaves() {
    var form = new LobbyForm("test name", UUID.randomUUID(), UUID.randomUUID());
    var lobby = new Lobby();
    var player = new Player();

    when(lobbyRepository.findById(form.lobbyId())).thenReturn(Optional.of(lobby));
    when(playerRepository.findById(form.playerId())).thenReturn(Optional.of(player));

    var response = lobbyService.joinLobby(form);

    verify(playerRepository).save(player);
    verifyNoMoreInteractions(lobbyRepository, playerRepository);

    assertThat(player)
        .extracting(Player::getLobby, Player::getName)
        .containsExactly(lobby, form.playerName());
    assertThat(response.playerData())
        .extracting(PlayerDto::playerName)
        .isEqualTo(form.playerName());
  }
}
