package io.fingersonbuzzers.backend.lobby;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "lobbies")
public class Lobby {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @CreationTimestamp
  private Instant createdTimestamp;

  public UUID getId() {
    return id;
  }

}
