package io.fingersonbuzzers.backend.context;

import io.fingersonbuzzers.backend.AbstractIntegrationTest;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class BackendApplicationTests extends AbstractIntegrationTest {

  @Test
  void contextLoads() {
    assertThat(entityManager).isNotNull();
  }
}
