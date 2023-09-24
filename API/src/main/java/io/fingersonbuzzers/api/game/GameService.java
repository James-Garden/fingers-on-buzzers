package io.fingersonbuzzers.api.game;

import jakarta.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
public class GameService {

  private static final Logger LOGGER = LoggerFactory.getLogger(GameService.class);

  private final GameRepository gameRepository;
  private final ApplicationEventPublisher applicationEventPublisher;

  @Autowired
  GameService(GameRepository gameRepository, ApplicationEventPublisher applicationEventPublisher) {
    this.gameRepository = gameRepository;
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Transactional
  public Game newGame(String hostName) {
    var host = new Player(hostName);
    var game = new Game(host);
    return save(game);
  }

  @Transactional
  public void joinGame(Game game, String newPlayerName) {
    game.addPlayer(new Player(newPlayerName));
    game.resetTimeToLive();
    save(game);
  }

  public Optional<Game> findGame(UUID gameId) {
    return gameRepository.findById(gameId);
  }

  public Game getGame(UUID gameId) {
    return findGame(gameId).orElseThrow(() ->
        new IllegalArgumentException("No Game exists with the ID: '%s'".formatted(gameId)));
  }

  private Game save(Game game) {
    game.resetTimeToLive();
    gameRepository.save(game);
    LOGGER.debug("Publishing GameUpdatedEvent for game: {}", game.getId());
    applicationEventPublisher.publishEvent(new GameUpdatedEvent(this, game.getId()));
    return game;
  }
}
