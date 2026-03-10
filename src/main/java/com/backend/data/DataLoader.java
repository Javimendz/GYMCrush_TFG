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

//Interfaz de spring para ejecutar inmediatamente despues de iniciar la aplicación antes de recibir peticiones
@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
     @Transactional //Usamos transactional para que lo haga todo a la vez
    public void run(String... args) throws Exception {

        Role adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("ADMIN");
                    return roleRepository.save(newRole);

                });

        Role usuarioRole = roleRepository.findByName("USUARIO")
                .orElseGet(() -> {
                    Role newRole = new Role();
                    newRole.setName("USUARIO");
                    return roleRepository.save(newRole);

                });

        if (usuarioRepository.findByUsername("admin").isEmpty()) {

            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setUsername("admin");
            admin.setCorreo("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin1234"));

            Set<Role> adminRoles = new HashSet<>();
            adminRoles.add(adminRole);
            adminRoles.add(usuarioRole);
            admin.setRoles(adminRoles);

            usuarioRepository.save(admin);
            System.out.println("Usuario admin creado!!..");
        }

        if (usuarioRepository.findByUsername("usuario").isEmpty()) {

            Usuario usuario = new Usuario();
            usuario.setNombre("Usuario normal");
            usuario.setUsername("usuario");
            usuario.setCorreo("usuario@example.com");
            usuario.setPassword(passwordEncoder.encode("123456"));

            Set<Role> usuarioRoles = new HashSet<>();
           
            usuarioRoles.add(usuarioRole);
            usuario.setRoles(usuarioRoles);

            usuarioRepository.save(usuario);
            System.out.println("Usuario creado!!..");
        }

    }

}