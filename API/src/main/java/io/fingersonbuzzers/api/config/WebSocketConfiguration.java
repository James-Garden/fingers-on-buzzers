package io.fingersonbuzzers.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

  @Override
  public void registerStompEndpoints(StompEndpointRegistry registry) {
    // Endpoint initially used to connect to the WebSocket server
    registry.addEndpoint("/websocket").setAllowedOriginPatterns("*").withSockJS();
  }

  @Override
  public void configureMessageBroker(MessageBrokerRegistry registry) {
    // Prefix for channels which data will be broadcast to, and a client may subscribe to
    registry.enableSimpleBroker("/topic");

    // Prefix for endpoints which the client will send data to
    registry.setApplicationDestinationPrefixes("/app");
  }
}

