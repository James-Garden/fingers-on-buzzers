package io.fingersonbuzzers.backend.context;

import io.fingersonbuzzers.backend.AbstractIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.assertj.core.api.Assertions.assertThat;

class BackendApplicationTests extends AbstractIntegrationTest {

  @Autowired
  EntityManager entityManager;

  @Test
  void contextLoads() {
    assertThat(entityManager).isNotNull();
  }
}
