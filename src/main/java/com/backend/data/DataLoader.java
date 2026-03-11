//Paquete
package com.backend.data;
//Imports
import java.util.HashSet;
import java.util.Set;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.backend.domain.Role;
import com.backend.domain.Usuario;
import com.backend.repository.RoleRepository;
import com.backend.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
//Clase para cargar datos iniciales en la base de datos
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository; //Repositorio de usuarios
    private final RoleRepository roleRepository; //Repositorio de roles
    private final PasswordEncoder passwordEncoder; //Codificador de contraseñas

    @Override
     @Transactional //Usamos transactional para que lo haga todo a la vez
    public void run(String... args) throws Exception {

        //Se ejecta todo antes de hacer peticiones, asi creara los usuarios lo primero.

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
            admin.setNombre("Administrador");
            admin.setUsername("admin");
            admin.setApellidos("admin admin");
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
            usuario.setNombre("Usuario normal");
            usuario.setUsername("usuario");
            usuario.setApellidos("de prueba");
            usuario.setCorreo("usuario@example.com");
            usuario.setPassword(passwordEncoder.encode("123456"));

            Set<Role> usuarioRoles = new HashSet<>();
           
            usuarioRoles.add(usuarioRole);
            usuario.setRol(usuarioRoles);

            usuarioRepository.save(usuario);
            System.out.println("Usuario creado!!..");
        }

    }

}