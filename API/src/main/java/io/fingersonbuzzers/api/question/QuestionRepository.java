package io.fingersonbuzzers.api.question;

import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface QuestionRepository extends CrudRepository<Question, UUID> {

  boolean existsByIdIsNotNull();

}
