package io.fingersonbuzzers.backend;

import io.fingersonbuzzers.backend.integrationtest.AbstractIntegrationTest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class BackendApplicationTests extends AbstractIntegrationTest {

  @Autowired
  EntityManager entityManager;

  @Test
  void contextLoads() {
    assertDoesNotThrow(() -> {});
  }

}
