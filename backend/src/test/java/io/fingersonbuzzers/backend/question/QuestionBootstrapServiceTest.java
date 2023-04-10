package io.fingersonbuzzers.backend.question;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionBootstrapServiceTest {

  @Mock
  private QuestionRepository questionRepository;

  @InjectMocks
  private QuestionBootstrapService questionBootstrapService;

  @Captor
  private ArgumentCaptor<List<Question>> questionsArgumentCaptor;

  @Test
  void bootstrapQuestionsData_NonEmptyDb_AssertDoesNotSave() throws IOException {
    when(questionRepository.existsByIdIsNotNull()).thenReturn(true);

    questionBootstrapService.bootstrapQuestionsData();

    verifyNoMoreInteractions(questionRepository);
  }

  @Test
  void bootstrapQuestionsData_EmptyDb_AssertSaves() throws IOException {
    when(questionRepository.existsByIdIsNotNull()).thenReturn(false);

    questionBootstrapService.bootstrapQuestionsData();

    verify(questionRepository).saveAll(questionsArgumentCaptor.capture());
    verifyNoMoreInteractions(questionRepository);

    var savedQuestions = questionsArgumentCaptor.getValue();
    assertThat(savedQuestions).extracting(
        Question::getQuestionText,
        Question::getAnswer
    ).containsExactly(
        tuple(
            "Test question 1",
            "Test answer 1"
        ),
        tuple(
            "Test question 2",
            "Test answer 2"
        )
    );
  }
}
