package io.fingersonbuzzers.api;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled
class ApiApplicationTests {

  @Test
  void contextLoads() {
    assertThat(1+1).isEqualTo(2);
  }

}
