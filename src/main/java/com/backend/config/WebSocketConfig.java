    package com.backend.config;

    import org.springframework.context.annotation.Configuration;
    import org.springframework.messaging.simp.config.MessageBrokerRegistry;
    import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
    import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
    import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

    @Configuration // para habilitar la configuración de websockets
    @EnableWebSocketMessageBroker // para habilitar el broker de mensajes
    public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

        @Override
        public void configureMessageBroker(MessageBrokerRegistry config) {
            config.enableSimpleBroker("/topic"); // prefijo para recibir informacion de los mensajes
            config.setApplicationDestinationPrefixes("/app"); // prefijo a utilizar para indicar que el cliente envia
                                                            // informacion a nuestro websockets
        }

        /*
        
        
        
        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/chats")
                    .setAllowedOrigins("*") // Importante para que Android conecte sin bloqueos de CORS
                    .withSockJS();
        }

        
        
        */

        //para pruebas este, para produccion el de arribaS
        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
 
        // Endpoint SIN SockJS → para Postman y clientes WebSocket puros
        registry.addEndpoint("/chats")
                .setAllowedOriginPatterns("*");
 
        // Endpoint CON SockJS → para Android y navegadores con fallback
        registry.addEndpoint("/chats-sockjs")
                .setAllowedOrigins("*")
                .withSockJS();
    }
    }
