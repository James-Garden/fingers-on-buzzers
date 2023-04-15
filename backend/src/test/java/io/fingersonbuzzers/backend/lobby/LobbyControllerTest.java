package io.fingersonbuzzers.backend.lobby;

import io.fingersonbuzzers.backend.AbstractControllerTest;
import io.fingersonbuzzers.backend.player.PlayerDto;
import io.fingersonbuzzers.backend.validation.ValidationResultTestUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LobbyController.class)
class LobbyControllerTest extends AbstractControllerTest {

  @MockBean
  private LobbyFormValidator lobbyFormValidator;

  @MockBean
  private LobbyService lobbyService;

  @Captor
  private ArgumentCaptor<LobbyForm> lobbyFormArgumentCaptor;

  @Test
  void createLobby_InvalidForm_AssertFailure() throws Exception {
    var form = new LobbyForm("", UUID.randomUUID());
    var formJson = MAPPER.writeValueAsString(form);
    var validationResult = ValidationResultTestUtil.validationResultWithError();
    var expectedResponseJson = MAPPER.writeValueAsString(CreateLobbyResponse.failure(validationResult));

    when(lobbyFormValidator.validate(any(LobbyForm.class))).thenReturn(validationResult);

    mockMvc.perform(
        post("/api/lobby/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(formJson))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedResponseJson));

    verify(lobbyFormValidator).validate(lobbyFormArgumentCaptor.capture());
    verifyNoInteractions(lobbyService);

    assertThat(lobbyFormArgumentCaptor.getValue())
        .extracting(LobbyForm::playerId, LobbyForm::playerName)
        .containsExactly(form.playerId(), form.playerName());
  }

  @Test
  void createLobby_ValidForm_AssertSuccess() throws Exception {
    var form = new LobbyForm("test name", UUID.randomUUID());
    var formJson = MAPPER.writeValueAsString(form);
    var validationResult = ValidationResultTestUtil.validationResultNoError();
    var expectedResponse = CreateLobbyResponse.success(
        new LobbyDto(UUID.randomUUID()),
        new PlayerDto(UUID.randomUUID(), "test name")
    );
    var expectedResponseJson = MAPPER.writeValueAsString(expectedResponse);

    when(lobbyFormValidator.validate(any(LobbyForm.class))).thenReturn(validationResult);
    when(lobbyService.createLobby(any(LobbyForm.class))).thenReturn(expectedResponse);

    mockMvc.perform(
            post("/api/lobby/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(formJson))
        .andExpect(status().isOk())
        .andExpect(content().json(expectedResponseJson));

    verify(lobbyFormValidator).validate(lobbyFormArgumentCaptor.capture());
    verify(lobbyService).createLobby(lobbyFormArgumentCaptor.getValue());

    assertThat(lobbyFormArgumentCaptor.getValue())
        .extracting(LobbyForm::playerId, LobbyForm::playerName)
        .containsExactly(form.playerId(), form.playerName());
  }
}
