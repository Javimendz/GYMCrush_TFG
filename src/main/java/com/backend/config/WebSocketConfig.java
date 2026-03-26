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
 * 
 * <p>
 * <b>Características principales:</b>
 * <ul>
 *   <li>Configuración de broker de mensajes para suscripciones</li>
 *   <li>Definición de endpoints para conexión de clientes</li>
 *   <li>Soporte para SockJS con fallback para navegadores antiguos</li>
 *   <li>Configuración CORS para permitir conexiones desde diferentes orígenes</li>
 * </ul>
 * </p>
 *
 * @author Backend Team
 * @version 1.0
 * @since 2026
 */
@Configuration // Marca esta clase como configuración de Spring
@EnableWebSocketMessageBroker // Habilita el broker de mensajes WebSocket
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    /**
     * Configura el broker de mensajes para el manejo de suscripciones y envío de mensajes.
     * <p>
 * Establece los prefijos URL que se utilizarán para:
 * <ul>
 *   <li><b>/topic</b>: Para que los clientes se suscriban y reciban mensajes</li>
 *   <li><b>/app</b>: Para que los clientes envíen mensajes al servidor</li>
 * </ul>
 * </p>
 * 
 * <p>
 * <b>Ejemplo de uso:</b>
 * <ul>
 *   <li>Cliente se suscribe: {@code ws://localhost:8080/topic/chat}</li>
 *   <li>Cliente envía mensaje: {@code ws://localhost:8080/app/chat}</li>
 * </ul>
 * </p>
 *
 * @param config Registro de configuración del broker de mensajes
 */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Habilita un broker simple en memoria para el prefijo /topic
        // Los clientes se suscribirán a /topic/nombre-canal para recibir mensajes
        config.enableSimpleBroker("/topic");
        
        // Establece el prefijo /app para los destinos de la aplicación
        // Los clientes enviarán mensajes a /app/nombre-destino
        config.setApplicationDestinationPrefixes("/app");
    }

    /**
     * Registra los endpoints STOMP para la conexión de clientes WebSocket.
     * <p>
     * Configura dos endpoints para diferentes tipos de clientes:
     * <ul>
     *   <li><b>/chats</b>: Endpoint estándar sin SockJS para clientes WebSocket puros y herramientas como Postman</li>
     *   <li><b>/chats-sockjs</b>: Endpoint con SockJS para navegadores y clientes que necesitan fallback</li>
     * </ul>
     * </p>
     * 
     * <p>
     * <b>Nota importante:</b> Se permite acceso desde cualquier origen (*) mediante {@code setAllowedOriginPatterns("*")}
     * para facilitar el desarrollo y testing. En producción, se debe restringir a dominios específicos.
     * </p>
     * 
     * <p>
     * <b>Ejemplos de conexión:</b>
     * <ul>
     *   <li>WebSocket puro: {@code ws://localhost:8080/chats}</li>
     *   <li>Con SockJS: {@code http://localhost:8080/chats-sockjs}</li>
     * </ul>
     * </p>
     *
     * @param registry Registro de endpoints STOMP
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
 
        // Endpoint estándar WebSocket sin SockJS
        // Ideal para clientes WebSocket puros como Postman o aplicaciones móviles nativas
        registry.addEndpoint("/chats")
                .setAllowedOriginPatterns("*");
 
        // Endpoint con soporte SockJS
        // Proporciona fallback para navegadores antiguos y clientes con limitaciones
        registry.addEndpoint("/chats-sockjs")
                .setAllowedOrigins("*")
                .withSockJS();
    }
    }
