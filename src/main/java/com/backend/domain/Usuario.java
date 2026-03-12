//Paquete
package com.backend.domain;

//Imports
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.*;//Importar todo el paquete
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "apellido", nullable = false)
    private String apellidos;

    @Column(name = "correo", unique = true, nullable = false, length = 150)
    private String correo;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

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

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Role> rol = new HashSet<>();

    @Column(name = "qr_token", length = 255)
    private String qrToken;

    @Column(name = "token_expira_en", nullable = true)
    private LocalDateTime tokenExpiraEn;

    @Column(name = "fecha_registro", nullable = true)
    private LocalDateTime fechaRegistro;

    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
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

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.fechaRegistro = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void addRole(Role role) {
        this.rol.add(role);
        role.getUsuarios().add(this);
    }

    public void removeRole(Role role) {
        this.rol.remove(role);
        role.getUsuarios().remove(this);
    }
}
