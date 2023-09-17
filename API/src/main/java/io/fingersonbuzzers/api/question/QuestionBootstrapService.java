package io.fingersonbuzzers.api.question;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

@Service
class QuestionBootstrapService {

  private static final Logger LOGGER = LoggerFactory.getLogger(QuestionBootstrapService.class);
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final String INITIAL_QUESTION_DATA_PATH = "data/questions/initial.json";

  private final QuestionRepository questionRepository;

  @Autowired
  QuestionBootstrapService(QuestionRepository questionRepository) {
    this.questionRepository = questionRepository;
  }

  @EventListener(ApplicationReadyEvent.class)
  public void bootstrapQuestionsData() throws IOException {
    LOGGER.debug("Attempting to initialise questions...");
    if (anyQuestionsExist()) {
      LOGGER.debug("Questions already exist, no initialisation necessary");
      return;
    }

    LOGGER.trace("Loading initial question data from '{}'", INITIAL_QUESTION_DATA_PATH);
    var questionsJson = new ClassPathResource(INITIAL_QUESTION_DATA_PATH).getFile();
    LOGGER.trace("Question data loaded, construction Question entities");
    var questions = MAPPER.readValue(questionsJson, new TypeReference<Collection<Question>>() {
    });
    LOGGER.trace("Successfully constructed {} Question objects", questions.size());

    LOGGER.trace("Saving Question entities to database");
    var stopwatch = new StopWatch();
    stopwatch.start();
    questionRepository.saveAll(questions);
    stopwatch.stop();
    LOGGER.debug("Successfully initialised {} questions in {} seconds", questions.size(),
        stopwatch.getTotalTimeSeconds());
  }

  private boolean anyQuestionsExist() {
    return questionRepository.existsByIdIsNotNull();
  }
}
