package io.fingersonbuzzers.backend.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "questions")
public class Question {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String questionText;

  private String answer;

  private Instant createdTimestamp;

  private Instant updatedTimestamp;

  public Question() {
  }

  @SuppressWarnings("unused")
  public Question(@JsonProperty("question") String questionText,
                  @JsonProperty("answer") String answer) {
    this.questionText = questionText;
    this.answer = answer;
  }

  public String getQuestionText() {
    return questionText;
  }

  public String getAnswer() {
    return answer;
  }

  public Instant getCreatedTimestamp() {
    return createdTimestamp;
  }

  public void setCreatedTimestamp(Instant createdTimestamp) {
    this.createdTimestamp = createdTimestamp;
  }

  public Instant getUpdatedTimestamp() {
    return updatedTimestamp;
  }

  public void setUpdatedTimestamp(Instant updatedTimestamp) {
    this.updatedTimestamp = updatedTimestamp;
  }
}
