package io.fingersonbuzzers.backend;

import io.fingersonbuzzers.backend.configuration.AllowedOrigins;
import io.fingersonbuzzers.backend.configuration.WebSecurityConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@Import({
    WebSecurityConfiguration.class
})
@EnableConfigurationProperties(value = AllowedOrigins.class)
public abstract class AbstractControllerTest {

  protected static final ObjectMapper MAPPER = new ObjectMapper();

  @Autowired
  protected MockMvc mockMvc;

}
