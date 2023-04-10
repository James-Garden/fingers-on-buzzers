package io.fingersonbuzzers.backend;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = {AbstractIntegrationTest.Initializer.class})
public abstract class AbstractIntegrationTest {

  @Autowired
  protected EntityManager entityManager;

  @SuppressWarnings("resource")
  @Container
  private static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER =
      new PostgreSQLContainer<>("postgres:14.3-alpine")
      .withDatabaseName("integration-test-db")
      .withUsername("test_username")
      .withPassword("test1");

  static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(
          "spring.datasource.url=" + POSTGRE_SQL_CONTAINER.getJdbcUrl(),
          "spring.datasource.username=" + POSTGRE_SQL_CONTAINER.getUsername(),
          "spring.datasource.password=" + POSTGRE_SQL_CONTAINER.getPassword()
      ).applyTo(configurableApplicationContext.getEnvironment());
    }
  }
}
