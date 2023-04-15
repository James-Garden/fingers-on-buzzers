package io.fingersonbuzzers.backend.lobby;

import org.springframework.beans.factory.annotation.Autowired;
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
  public CreateLobbyResponse createLobby(@RequestBody LobbyForm form) {
    var validationResult = lobbyFormValidator.validate(form);
    if (validationResult.hasErrors()) {
      return CreateLobbyResponse.failure(validationResult);
    }

    return lobbyService.createLobby(form);
  }
}
