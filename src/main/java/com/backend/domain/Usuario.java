//Paquete
package com.backend.domain;

//Imports
import java.time.LocalDateTime;
import java.util.List;

import com.backend.domain.enums.EnumRol;

import jakarta.persistence.*;//Importar todo el paquete
import lombok.*;

@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "correo", unique = true, nullable = false, length = 150)
    private String correo;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING) // Indicamos que es un enum de tipo string
    @Column(name = "rol", length = 15)
    private EnumRol rol;

    @Column(name = "qr_token", length = 255)
    private String qrToken;

    @Column(name = "token_expira_en", nullable = true)
    private LocalDateTime tokenExpiraEn;

    @Column(name = "fecha_registro", nullable = true)
    private LocalDateTime fechaRegistro;

    @Column(name = "createdAt", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Perfil perfil; // Relación 1:1 con Perfil [cite: 107]

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Reserva> reservas; // Un usuario realiza N reservas [cite: 114]

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Entrenamiento> entrenamientos; // Un usuario realiza N entrenamientos [cite: 108]

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Salud> salud; // Un usuario registra N datos de salud [cite: 109]

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<SugerenciaDieta> sugerenciasDieta; // Un usuario recibe N sugerencias [cite: 110]

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Ticket> tickets; // Un usuario crea N tickets [cite: 113]

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Chat> chat; // Un usuario conecta con el chat [cite: 112]

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.fechaRegistro = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }


   
}
