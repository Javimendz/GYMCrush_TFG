package com.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * Configuración de WebSocket para la aplicación GYM Crush.
 * <p>
 * Esta clase establece la configuración necesaria para habilitar la comunicación
 * en tiempo real mediante WebSockets utilizando el protocolo STOMP (Simple Text Oriented Messaging Protocol).
 * Permite la implementación de funcionalidades como chat en vivo, notificaciones
 * push y actualizaciones en tiempo real entre el servidor y los clientes.
 * </p>
 * * <p>
 * <b>Características principales:</b>
 * <ul>
 * <li>Configuración de broker de mensajes para suscripciones</li>
 * <li>Definición de endpoints para conexión de clientes</li>
 * <li>Soporte para SockJS con fallback para navegadores antiguos</li>
 * <li>Configuración CORS para permitir conexiones desde diferentes orígenes</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configura el broker de mensajes para el manejo de suscripciones y envío de mensajes.
     * <p>
     * Establece los prefijos URL que se utilizarán para:
     * <ul>
     * <li><b>/topic</b>: Para canales públicos y broadcasts</li>
     * <li><b>/queue</b>: Para mensajes privados específicos de un usuario</li>
     * <li><b>/app</b>: Para que los clientes envíen mensajes al servidor</li>
     * </ul>
     * </p>
     *
     * @param config Registro de configuración del broker de mensajes
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita un broker simple en memoria para los prefijos /topic y /queue
        config.enableSimpleBroker("/topic", "/queue");
        
        // Prefijo para los mensajes que van desde el cliente hacia el servidor (@MessageMapping)
        config.setApplicationDestinationPrefixes("/app");
        
        // Prefijo clave para usar convertAndSendToUser() en notificaciones privadas
        config.setUserDestinationPrefix("/user");
    }

    /**
     * Registra los endpoints STOMP para la conexión de clientes WebSocket.
     * <p>
     * Configura dos endpoints para diferentes tipos de clientes:
     * <ul>
     * <li><b>/chats</b>: Endpoint estándar sin SockJS para clientes WebSocket puros y herramientas como Postman</li>
     * <li><b>/chats-sockjs</b>: Endpoint con SockJS para navegadores y clientes que necesitan fallback</li>
     * </ul>
     * </p>
     *
     * @param registry Registro de endpoints STOMP
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        
        // 1. Endpoint estándar WebSocket puro (SIN SockJS)
        // Ideal para clientes móviles nativos (Android/iOS) o Postman
        registry.addEndpoint("/chats")
                .setAllowedOriginPatterns("*");
        
        // 2. Endpoint con soporte SockJS
        // Proporciona fallback para navegadores web
        registry.addEndpoint("/chats-sockjs")
                .setAllowedOriginPatterns("*") // Usar Patterns evita errores de CORS con credenciales
                .withSockJS();
    }
}