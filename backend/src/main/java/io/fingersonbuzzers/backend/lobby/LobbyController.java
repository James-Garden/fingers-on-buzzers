package io.fingersonbuzzers.backend.lobby;

import io.fingersonbuzzers.backend.lobby.responses.CreateLobbyResponse;
import io.fingersonbuzzers.backend.player.Player;
import io.fingersonbuzzers.backend.player.PlayerForm;
import io.fingersonbuzzers.backend.player.PlayerFormValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lobby")
public class LobbyController {

  private final LobbyService lobbyService;
  private final PlayerFormValidator playerFormValidator;

  @Autowired
  public LobbyController(LobbyService lobbyService,
                         PlayerFormValidator playerFormValidator) {
    this.lobbyService = lobbyService;
    this.playerFormValidator = playerFormValidator;
  }

  @PostMapping("/create")
  public CreateLobbyResponse createLobby(@RequestBody PlayerForm form) {
    var validationResult = playerFormValidator.validate(form);
    if (validationResult.hasErrors()) {
      return CreateLobbyResponse.failure(validationResult);
    }

    var playerName = form.playerName().strip();
    var lobby = new Lobby();
    var player = new Player(lobby, playerName);
    lobbyService.createLobby(lobby, player);

    return CreateLobbyResponse.success(lobby.getId(), player.getId());
  }
}
