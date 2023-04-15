package io.fingersonbuzzers.backend.lobby;

import io.fingersonbuzzers.backend.player.Player;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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

  @OneToOne
  @JoinColumn(name = "host_id")
  private Player host;

  @CreationTimestamp
  private Instant createdTimestamp;

  public UUID getId() {
    return id;
  }

  public Player getHost() {
    return host;
  }

  public void setHost(Player host) {
    this.host = host;
  }
}
