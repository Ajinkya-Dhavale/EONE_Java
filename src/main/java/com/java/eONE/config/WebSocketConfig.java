package com.java.eONE.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue"); // destinations prefix for broker
        config.setApplicationDestinationPrefixes("/app"); // prefix for client send endpoints
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Endpoint clients connect to, enable SockJS fallback
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*").withSockJS();
    }
}
