//Paquete
package com.backend.domain;
//Imports
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;//Importar todo el paquete
import lombok.*;

@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "correo", unique = true, nullable = false, length = 150)
    private String correo;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Column(name = "rol", length = 50)
    private String rol;

    @Column(name = "qr_token", length = 255)
    private String qrToken;

    @Column(name = "token_expira_en")
    private LocalDateTime tokenExpiraEn;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    @Column(name = "createdAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;


    /*@OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Perfil perfil; // Relación 1:1 con Perfil [cite: 107]

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Reservas> reservas; // Un usuario realiza N reservas [cite: 114]

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Entrenamiento> entrenamientos; // Un usuario realiza N entrenamientos [cite: 108]

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Salud> registrosSalud; // Un usuario registra N datos de salud [cite: 109]

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<SugerenciaDieta> sugerenciasDieta; // Un usuario recibe N sugerencias [cite: 110]

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Ticket> tickets; // Un usuario crea N tickets [cite: 113]

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Chat> mensajesChat; // Un usuario conecta con el chat [cite: 112]
    // */
}
