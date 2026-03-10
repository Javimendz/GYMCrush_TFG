//Paquete
package com.backend.security.jwt;
//Imports
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;
//Componente para manejar la autenticacion fallida
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // Enviar una respuesta de error 401 (No autorizado) con el mensaje de la excepcion de autenticacion
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());

    }
}