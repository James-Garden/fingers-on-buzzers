package io.fingersonbuzzers.backend.lobby;

import io.fingersonbuzzers.backend.validation.ValidationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lobby")
public class LobbyController {

  private final LobbyFormValidator lobbyFormValidator;
  private final LobbyService lobbyService;

  @Autowired
  public LobbyController(LobbyFormValidator lobbyFormValidator,
                         LobbyService lobbyService) {
    this.lobbyFormValidator = lobbyFormValidator;
    this.lobbyService = lobbyService;
  }

  @PostMapping("/create")
  public ResponseEntity<LobbyResponse> createLobby(@RequestBody LobbyForm form) throws ValidationFailedException {
    lobbyFormValidator.validate(form, LobbyForm.FormType.CREATE_LOBBY);

    return ResponseEntity.ok().body(lobbyService.createLobby(form));
  }

  @PostMapping("/join")
  public ResponseEntity<LobbyResponse> joinLobby(@RequestBody LobbyForm form) throws ValidationFailedException {
    lobbyFormValidator.validate(form, LobbyForm.FormType.JOIN_LOBBY);

    return ResponseEntity.ok().body(lobbyService.joinLobby(form));
  }
}
