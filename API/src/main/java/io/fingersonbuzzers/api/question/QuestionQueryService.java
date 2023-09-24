package io.fingersonbuzzers.api.question;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QuestionQueryService {

  private final QuestionRepository questionRepository;
  private final EntityManager entityManager;
  private final Random random;

  @Autowired
  public QuestionQueryService(QuestionRepository questionRepository, EntityManager entityManager,
      Random random) {
    this.questionRepository = questionRepository;
    this.entityManager = entityManager;
    this.random = random;
  }


  public Question getRandomQuestion() {
    var questionCount = questionRepository.count();
    var randomOffset = random.nextInt((int) questionCount);

    var cb = entityManager.getCriteriaBuilder();
    var cq = cb.createQuery();
    var root = cq.from(Question.class);
    cq.select(root);

    // Get a single random question by setting the LIMIT to 1, and the OFFSET to a random number
    var query = entityManager
        .createQuery(cq)
        .setFirstResult(randomOffset)
        .setMaxResults(1);

    var results = query.getResultList();

    if (results.isEmpty()) {
      throw new EntityNotFoundException("Unable to find any questions for the given CriteriaQuery");
    }
    if (results.size() != 1) {
      throw new IllegalStateException(
          "Expected CriteriaQuery to return 1 result, got %d".formatted(results.size()));
    }

    var result = results.get(0);

    if (result instanceof Question question) {
      return question;
    }

    throw new IllegalStateException(
        "Expected result of CriteriaQuery to be of type Question, got: %s".formatted(result));
  }
}
