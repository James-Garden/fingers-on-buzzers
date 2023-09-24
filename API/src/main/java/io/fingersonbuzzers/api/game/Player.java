package io.fingersonbuzzers.api.game;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import java.util.UUID;

public class Player {

  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  private String name;
  private Integer score;

  public Player(String name) {
    this.id = UUID.randomUUID();
    this.name = name;
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getScore() {
    return score;
  }

  public void setScore(Integer score) {
    this.score = score;
  }
}
