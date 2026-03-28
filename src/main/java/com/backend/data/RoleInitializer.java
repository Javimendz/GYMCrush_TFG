package com.backend.data;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.backend.domain.Role;
import com.backend.repository.RoleRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleInitializer {

    private final RoleRepository roleRepository;

    @EventListener(ApplicationReadyEvent.class)
    public void initializeRoles() {
        if (!roleRepository.existsByName("ROLE_USUARIO")) {
            Role usuarioRole = new Role();
            usuarioRole.setName("ROLE_USUARIO");
            roleRepository.save(usuarioRole);
            System.out.println("Rol ROLE_USUARIO creado");
        }

        if (!roleRepository.existsByName("ROLE_ADMIN")) {
            Role adminRole = new Role();
            adminRole.setName("ROLE_ADMIN");
            roleRepository.save(adminRole);
            System.out.println("Rol ROLE_ADMIN creado");
        }
    }
}
