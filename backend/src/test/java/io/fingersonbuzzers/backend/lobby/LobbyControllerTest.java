package io.fingersonbuzzers.backend.lobby;

import io.fingersonbuzzers.backend.AbstractControllerTest;
import io.fingersonbuzzers.backend.lobby.responses.CreateLobbyResponse;
import io.fingersonbuzzers.backend.player.Player;
import io.fingersonbuzzers.backend.player.PlayerForm;
import io.fingersonbuzzers.backend.player.PlayerFormValidator;
import io.fingersonbuzzers.backend.validation.ValidationResultTestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.testcontainers.shaded.org.apache.commons.lang3.reflect.FieldUtils;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LobbyController.class)
class LobbyControllerTest extends AbstractControllerTest {

  @MockBean
  PlayerFormValidator playerFormValidator;

  @MockBean
  LobbyService lobbyService;

  @Test
  void createLobby_InvalidForm_AssertFailure() throws Exception {
    var form = new PlayerForm("");
    var formJson = MAPPER.writeValueAsString(form);
    var validationResult = ValidationResultTestUtil.validationResultWithError();
    var expectedOutputJson = MAPPER.writeValueAsString(CreateLobbyResponse.failure(validationResult));

    when(playerFormValidator.validate(any(PlayerForm.class)))
        .thenReturn(validationResult);

    mockMvc.perform(
        post("/api/lobby/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(formJson)
        )
        .andExpect(status().isOk())
        .andExpect(content().json(expectedOutputJson));

    verifyNoInteractions(lobbyService);
  }

  @Test
  void createLobby_ValidForm_AssertCreatesLobby() throws Exception {
    var form = new PlayerForm("name ");
    var formJson = MAPPER.writeValueAsString(form);
    var validationResult = ValidationResultTestUtil.validationResultNoError();
    var lobbyId = UUID.randomUUID();
    var playerId = UUID.randomUUID();
    var playerCaptor = ArgumentCaptor.forClass(Player.class);
    var lobbyCaptor = ArgumentCaptor.forClass(Lobby.class);
    var expectedOutputJson = MAPPER.writeValueAsString(CreateLobbyResponse.success(lobbyId, playerId));

    when(playerFormValidator.validate(any(PlayerForm.class)))
        .thenReturn(validationResult);
    doAnswer(invocation -> {
      var lobby = (Lobby) invocation.getArgument(0);
      var lobbyIdField = FieldUtils.getField(Lobby.class, "id", true);
      FieldUtils.writeField(lobbyIdField, lobby, lobbyId, true);

      var player = (Player) invocation.getArgument(1);
      var playerIdField = FieldUtils.getField(Player.class, "id", true);
      FieldUtils.writeField(playerIdField, player, playerId, true);
      return null;
    })
        .when(lobbyService)
        .createLobby(any(Lobby.class), any(Player.class));

    mockMvc.perform(
            post("/api/lobby/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(formJson)
        )
        .andExpect(status().isOk())
        .andExpect(content().json(expectedOutputJson));

    verify(lobbyService).createLobby(lobbyCaptor.capture(), playerCaptor.capture());

    var lobby = lobbyCaptor.getValue();

    assertThat(lobby.getId()).isEqualTo(lobbyId);
    assertThat(playerCaptor.getValue()).extracting(
        Player::getId,
        Player::getName,
        Player::getLobby
    ).containsExactly(
        playerId,
        form.playerName().strip(),
        lobby
    );
  }
}
