package io.fingersonbuzzers.backend.player;

import jakarta.persistence.EntityNotFoundException;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, UUID> {

  default Player getById(UUID playerId) throws EntityNotFoundException {
    return this.findById(playerId)
        .orElseThrow(() ->
            new EntityNotFoundException("Could not find Player with ID %s".formatted(playerId.toString())));
  }
}
