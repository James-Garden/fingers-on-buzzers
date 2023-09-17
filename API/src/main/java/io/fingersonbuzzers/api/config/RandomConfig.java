package io.fingersonbuzzers.api.config;

import java.util.Random;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RandomConfig {

  private static final Random RANDOM = new Random();

  @Bean
  public Random random() {
    return RANDOM;
  }
}
