package com.backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "usuarios")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;
    @Column(name = "apellido", nullable = false, length = 50)
    private String apellido;
    @Column(name = "correo", nullable = false, length = 100, unique = true)
    private String correo;
    @Column(name = "password", nullable = false, length = 255) 
    private String password;
    @Column(name = "telefono", nullable = false, length = 50)
    private String telefono;
    @Column(name = "direccion", nullable = false, length = 50)
    private String direccion;
    @Column(name = "ciudad", nullable = false, length = 50)
    private String ciudad;
    @Column(name = "pais", nullable = false, length = 50)
    private String pais;
    @Column(name = "codigo_postal", nullable = false, length = 50)
    private String codigoPostal;

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
