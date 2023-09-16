package io.fingersonbuzzers.api.question;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class QuestionBootstrapServiceTest {

  @Mock
  private QuestionRepository questionRepository;

  @InjectMocks
  private QuestionBootstrapService questionBootstrapService;

  @Captor
  private ArgumentCaptor<List<Question>> questionsArgumentCaptor;

  @Test
  void bootstrapQuestionsData_EmptyDatabase() throws IOException {
    when(questionRepository.existsByIdIsNotNull()).thenReturn(false);

    questionBootstrapService.bootstrapQuestionsData();

    verify(questionRepository).saveAll(questionsArgumentCaptor.capture());

    assertThat(questionsArgumentCaptor.getValue()).extracting(
        Question::getQuestionText,
        Question::getAnswer
    ).containsExactly(
        tuple("Test question 1", "Test answer 1"),
        tuple("Test question 2", "Test answer 2")
    );
  }

  @Test
  void bootstrapQuestionsData_NonEmptyDatabase() throws IOException {
    when(questionRepository.existsByIdIsNotNull()).thenReturn(true);

    questionBootstrapService.bootstrapQuestionsData();

    verifyNoMoreInteractions(questionRepository);
  }

}
