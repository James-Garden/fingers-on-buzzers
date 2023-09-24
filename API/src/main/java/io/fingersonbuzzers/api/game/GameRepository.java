package io.fingersonbuzzers.api.game;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface GameRepository extends CrudRepository<Game, UUID> {

}
