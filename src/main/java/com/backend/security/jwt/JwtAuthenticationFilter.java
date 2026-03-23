//Paquete
package com.backend.security.jwt;

//Imports
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component // Componente para inyectar la clase en otras partes de la aplicacion
@RequiredArgsConstructor // Constructor con los campos final lombok
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtGenerator jwtGenerator; // Inyeccion de dependencia de la clase JwtGenerator
    private final UserDetailsService userDetailsService; // Inyeccion de dependencia de la clase UserDetailsService

    // Metodo para filtrar las solicitudes HTTP y agregar la autenticacion al
    // contexto de seguridad
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String token = getJwtFromRequest(request);

        if (StringUtils.hasText(token) && jwtGenerator.validateToken(token)
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            //  Obtenemos el username directamente del JWT
            String username = jwtGenerator.getUsernameFromJwt(token);

            // Cargamos los detalles actualizados del usuario desde la DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            //  Creamos la autenticación con las autoridades (roles) de la base de datos
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            //  Añadimos detalles de la solicitud (IP, navegador, etc.)
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            // Establecemos el usuario en el contexto de seguridad global
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        // Continuar con el siguiente filtro en la cadena
        filterChain.doFilter(request, response);
    }

    // Metodo para obtener el token JWT de la solicitud HTTP
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7).trim();
        }
        return null;
    }
}