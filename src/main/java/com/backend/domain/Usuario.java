//Paquete
package com.backend.domain;

//Imports
import java.time.LocalDateTime;
<<<<<<< HEAD
import java.util.HashSet;
import java.util.List;
import java.util.Set;

=======
import java.util.List;

import com.backend.domain.enums.EnumRol;
>>>>>>> f4570e441899ef30c12df4e8042f8e8f12d93eac

import jakarta.persistence.*;//Importar todo el paquete
import lombok.*;

@Entity
@Table(name = "usuarios")
@Data
@AllArgsConstructor
@NoArgsConstructor
<<<<<<< HEAD

=======
@Builder
>>>>>>> f4570e441899ef30c12df4e8042f8e8f12d93eac
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

<<<<<<< HEAD
    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "nombre", nullable = false) 
    private String nombre;

    @Column(name = "apellido", nullable = false) 
    private String apellidos;

=======
>>>>>>> f4570e441899ef30c12df4e8042f8e8f12d93eac
    @Column(name = "correo", unique = true, nullable = false, length = 150)
    private String correo;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

<<<<<<< HEAD

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "direccion", length = 255)
    private String direccion;

    @Column(name = "ciudad", length = 100)
    private String ciudad;

    @Column(name = "pais", length = 100)
    private String pais;

    @Column(name = "codigo_postal", length = 20)
    private String codigoPostal;
    
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(  //Creamos una relacion N:M y creamos la tabla intermedia de la relacion con JoinTable
        name="users_roles",
        joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
        inverseJoinColumns = @JoinColumn(name= "role_id", referencedColumnName = "id") 
    )
    private Set<Role> roles = new HashSet<>();
=======
    @Enumerated(EnumType.STRING) // Indicamos que es un enum de tipo string
    @Column(name = "rol", length = 15)
    private EnumRol rol;
>>>>>>> f4570e441899ef30c12df4e8042f8e8f12d93eac

    @Column(name = "qr_token", length = 255)
    private String qrToken;

    @Column(name = "token_expira_en", nullable = true)
    private LocalDateTime tokenExpiraEn;

    @Column(name = "fecha_registro", nullable = true)
    private LocalDateTime fechaRegistro;

<<<<<<< HEAD
    @Column(name = "createdAt")
=======
    @Column(name = "createdAt", updatable = false, nullable = false)
>>>>>>> f4570e441899ef30c12df4e8042f8e8f12d93eac
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
<<<<<<< HEAD
    @ToString.Exclude // Para evitar bucle infinito con Lombok
    private Perfil perfil; // Relación 1:1 con Perfil

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Reserva> reservas; // Un usuario realiza N reservas

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Entrenamiento> entrenamientos; // Un usuario realiza N entrenamientos

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Salud> salud; // Un usuario registra N datos de salud

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<SugerenciaDieta> sugerenciasDieta; // Un usuario recibe N sugerencias

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Ticket> tickets; // Un usuario crea N tickets

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Chat> chat; // Un usuario conecta con el chat
=======
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
>>>>>>> f4570e441899ef30c12df4e8042f8e8f12d93eac

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
