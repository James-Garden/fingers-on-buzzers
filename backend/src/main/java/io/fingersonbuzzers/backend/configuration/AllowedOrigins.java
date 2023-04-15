package io.fingersonbuzzers.backend.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "allowed-origins")
public record AllowedOrigins(String frontendHostUrl) {
}
