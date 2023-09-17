package io.fingersonbuzzers.api.question;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "questions")
public class Question {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  private String questionText;

  private String answer;

  @CreationTimestamp
  private Instant createdTimestamp;

  @UpdateTimestamp
  private Instant updatedTimestamp;

  public Question() {
  }

  @SuppressWarnings("unused")
  public Question(@JsonProperty("question") String questionText,
      @JsonProperty("answer") String answer) {
    this.questionText = questionText;
    this.answer = answer;
  }

  public UUID getId() {
    return id;
  }

  public String getQuestionText() {
    return questionText;
  }

  public String getAnswer() {
    return answer;
  }
}
