package io.fingersonbuzzers.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebSecurityConfiguration implements WebMvcConfigurer {

  private final FrontendConfiguration frontendConfiguration;

  @Autowired
  public WebSecurityConfiguration(FrontendConfiguration frontendConfiguration) {
    this.frontendConfiguration = frontendConfiguration;
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**")
        .allowedMethods("*")
        .allowedOrigins(frontendConfiguration.url());
  }
}
