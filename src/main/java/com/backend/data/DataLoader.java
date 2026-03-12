//Paquete
package com.backend.data;

import java.time.LocalDate;
//Imports
import java.util.HashSet;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.backend.domain.Perfil;
import com.backend.domain.Role;
import com.backend.domain.Usuario;
import com.backend.domain.enums.EnumGenero;
import com.backend.repository.PerfilRepository;
import com.backend.repository.RoleRepository;
import com.backend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

//Clase para cargar datos iniciales en la base de datos
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository; // Repositorio de usuarios
    private final RoleRepository roleRepository; // Repositorio de roles
    private final PasswordEncoder passwordEncoder; // Codificador de contraseñas
    private final PerfilRepository perfilRepository; // Repositorio de perfiles

    @Override
    @Transactional // Usamos transactional para que lo haga todo a la vez
    public void run(String... args) throws Exception {

        // Se ejecta todo antes de hacer peticiones, asi creara los usuarios lo primero.

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("ROLE_ADMIN");
                    return roleRepository.save(newRole);

                });

        Role usuarioRole = roleRepository.findByName("ROLE_USUARIO")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("ROLE_USUARIO");
                    return roleRepository.save(newRole);

                });

        if (usuarioRepository.findByUsername("admin").isEmpty()) {

            Usuario admin = new Usuario();
            // admin.setNombre("Administrador");
            admin.setUsername("admin");
            // admin.setApellidos("admin admin");
            admin.setCorreo("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin1234"));

            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(usuarioRole);
            admin.setRol(adminRoles);

            usuarioRepository.save(admin);
            System.out.println("Usuario admin creado!!..");
        }

        if (usuarioRepository.findByUsername("usuario").isEmpty()) {

            Usuario usuario = new Usuario();
            // usuario.setNombre("Usuario normal");
            usuario.setUsername("usuario");
            // usuario.setApellidos("de prueba");
            usuario.setCorreo("usuario@example.com");
            usuario.setPassword(passwordEncoder.encode("123456"));

            Set<Role> usuarioRoles = new HashSet<>();

            usuarioRoles.add(usuarioRole);
            usuario.setRol(usuarioRoles);

            usuarioRepository.save(usuario);
            System.out.println("Usuario creado!!..");
        }

        if (!perfilRepository.existsByDni("12345678A")) {
            // Crear perfil
            Perfil perfil = new Perfil();
            perfil.setNombre("Perfil de prueba");
            perfil.setApellidos("de prueba");
            perfil.setTelefono("123456789");
            perfil.setDni("12345678A");
            perfil.setFechaNacimiento(LocalDate.of(1999, 5, 15));
            perfil.setGenero(EnumGenero.Hombre);
            perfil.setUsuario(usuarioRepository.findByUsername("usuario").get());
            perfil.setBio("bio de prueba");
            perfilRepository.save(perfil);
            System.out.println("Perfil creado!!..");
        }
        if (!perfilRepository.existsByDni("00000000A")) {
            Usuario adminUser = usuarioRepository.findByUsername("admin")
                    .orElseThrow(() -> new RuntimeException("Error: Usuario admin no encontrado"));

            Perfil perfilAdmin = Perfil.builder()
                    .nombre("Admin")
                    .apellidos("Sistema GymCrush")
                    .telefono("000000000") // Debe ser unico
                    .dni("00000000A") // Debe ser unico
                    .fechaNacimiento(LocalDate.of(1985, 1, 1))
                    .genero(EnumGenero.Hombre)
                    .bio("Cuenta de administración principal. Acceso total al sistema de gestión.")
                    .usuario(adminUser)
                    .build();

            perfilRepository.save(perfilAdmin);
            System.out.println("Perfil del Administrador creado!!..");
        }

        // cuando tenga SaludRepository, añadir esto:
        /*
         * Salud inicial = Salud.builder()
         * .peso(85.5)
         * .estatura(1.80)
         * .nivelActividad("Moderado")
         * .fechaMedicion(LocalDateTime.now())
         * .usuario(user)
         * .build();
         * saludRepository.save(inicial);
         */
    }

}