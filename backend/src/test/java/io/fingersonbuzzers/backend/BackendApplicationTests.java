package io.fingersonbuzzers.backend;

import io.fingersonbuzzers.backend.configuration.ClockConfiguration;
import io.fingersonbuzzers.backend.integrationtest.AbstractIntegrationTest;
import jakarta.persistence.EntityManager;
import java.time.Clock;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;

@Import(ClockConfiguration.class)
class BackendApplicationContextTests extends AbstractIntegrationTest {

  @Autowired
  EntityManager entityManager;

  @Autowired
  Clock clock;

  @Test
  void contextLoads() {
    assertThat(entityManager).isNotNull();
    assertThat(clock).isNotNull();
  }
}
