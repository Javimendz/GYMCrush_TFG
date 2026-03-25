package com.backend.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.backend.domain.enums.EnumMessageType;

@Entity
@Table(name = "mensajes_chat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MensajeChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contenido;
    private String emisor;
    
    private LocalDateTime fechaEnvio;

    @Enumerated(EnumType.STRING)
    private EnumMessageType tipo;// Necesitas el Enum que creamos antes
}