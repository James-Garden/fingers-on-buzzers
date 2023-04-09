package io.fingersonbuzzers.backend.question;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Clock;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
class QuestionBootstrapService {

  private static final ObjectMapper MAPPER = new ObjectMapper();

  private final QuestionRepository questionRepository;
  private final Clock clock;

  @Autowired
  QuestionBootstrapService(QuestionRepository questionRepository,
                           Clock clock) {
    this.questionRepository = questionRepository;
    this.clock = clock;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void bootstrapQuestionsData() throws IOException {
    if (questionRepository.existsByIdIsNotNull()) {
      return;
    }

    var questionsFile = new ClassPathResource("json/questions-bootstrap-data.json").getFile();
    var questions = MAPPER.readValue(questionsFile, new TypeReference<List<Question>>() {});
    questions.forEach(question -> {
      question.setCreatedTimestamp(clock.instant());
      question.setUpdatedTimestamp(question.getCreatedTimestamp());
    });
    questionRepository.saveAll(questions);
  }
}
