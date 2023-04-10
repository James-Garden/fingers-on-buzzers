package io.fingersonbuzzers.backend.player;

import io.fingersonbuzzers.backend.lobby.Lobby;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "players")
@NoArgsConstructor
public class Player {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  private Lobby lobby;

  private String name;

  @CreationTimestamp
  private Instant createdTimestamp;

  @UpdateTimestamp
  private Instant updatedTimestamp;

  public Player(Lobby lobby, String name) {
    this.lobby = lobby;
    this.name = name;
  }

  public UUID getId() {
    return id;
  }


  public Lobby getLobby() {
    return lobby;
  }

  public String getName() {
    return name;
  }
}
