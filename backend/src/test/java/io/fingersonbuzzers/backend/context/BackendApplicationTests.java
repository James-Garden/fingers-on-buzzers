package io.fingersonbuzzers.backend.context;

import io.fingersonbuzzers.backend.AbstractIntegrationTest;
import io.fingersonbuzzers.backend.configuration.AllowedOrigins;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.assertThat;

class BackendApplicationTests extends AbstractIntegrationTest {

  @Autowired
  AllowedOrigins allowedOrigins;

  @Test
  void contextLoads() {
    assertThat(entityManager).isNotNull();
    assertThat(allowedOrigins)
        .extracting(AllowedOrigins::frontendHostUrl)
        .isEqualTo("testUrl");
  }
}
