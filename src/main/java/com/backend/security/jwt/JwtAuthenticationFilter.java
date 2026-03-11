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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component // Componente para inyectar la clase en otras partes de la aplicacion
@RequiredArgsConstructor // Constructor con los campos final lombok
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtGenerator jwtGenerator;  // Inyeccion de dependencia de la clase JwtGenerator
    private final UserDetailsService userDetailsService; // Inyeccion de dependencia de la clase UserDetailsService

    // Metodo para filtrar las solicitudes HTTP y agregar la autenticacion al contexto de seguridad
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = getJwtFromRequest(request); // Obtener el token JWT de la solicitud HTTP

        if(StringUtils.hasText(token) && jwtGenerator.validateToken(token) // Validar el token JWT
            && SecurityContextHolder.getContext().getAuthentication()==null // Verificar si no hay autenticacion en el contexto de seguridad
        ){
            String username = jwtGenerator.getUsernameFromJwt(token); // Obtener el nombre de usuario del token JWT
 
            UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Cargar los detalles del usuario por nombre de usuario

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities() // Crear un objeto de autenticacion con los detalles del usuario y sus autoridades
            );
            // Establecer los detalles de autenticacion en el objeto de autenticacion
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken); // Establecer la autenticacion en el contexto de seguridad

        }
        // Continuar con el siguiente filtro en la cadena
        filterChain.doFilter(request, response);


    }

    // Metodo para obtener el token JWT de la solicitud HTTP
    private String getJwtFromRequest(HttpServletRequest request){
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7);
        }
        return null;
    }
}