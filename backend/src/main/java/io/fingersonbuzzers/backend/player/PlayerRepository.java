package io.fingersonbuzzers.backend.player;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepository extends CrudRepository<Player, UUID> {

}
