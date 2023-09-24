package io.fingersonbuzzers.api.game;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/game")
public class GameController {

  private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);

  private final GameService gameService;
  private final SimpMessagingTemplate messagingTemplate;

  @Autowired
  public GameController(GameService gameService, SimpMessagingTemplate messagingTemplate) {
    this.gameService = gameService;
    this.messagingTemplate = messagingTemplate;
  }

  @PostMapping
  public ResponseEntity<Game> createGame(@RequestBody CreateGameRequest body) {
    LOGGER.debug("Request to create game: {}", body);
    var game = gameService.newGame(body.hostName());

    return ResponseEntity.ok(game);
  }

  @PatchMapping
  public ResponseEntity<Game> joinGame(@RequestBody JoinGameRequest body) {
    LOGGER.debug("Request to join game: {}", body);
    var gameOptional = gameService.findGame(body.gameId());
    if (gameOptional.isEmpty()) {
      return ResponseEntity.notFound().build();
    }

    var game = gameOptional.get();
    gameService.joinGame(game, body.playerName());
    return ResponseEntity.ok(game);
  }

  @EventListener
  public void handleGameUpdatedEvent(GameUpdatedEvent event) {
    LOGGER.debug("Detected game updated event: {}", event);
    var game = gameService.getGame(event.getGameId());
    messagingTemplate.convertAndSend("/topic/%s".formatted(game.getId()), game);
  }

  public record CreateGameRequest(String hostName) {

  }

  public record JoinGameRequest(UUID gameId, String playerName) {

  }

}
