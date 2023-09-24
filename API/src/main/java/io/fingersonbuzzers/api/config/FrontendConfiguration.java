package io.fingersonbuzzers.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "frontend")
public record FrontendConfiguration(String url) {

}
