//Paquete
package com.backend.security.jwt;
//Imports
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.backend.security.dto.ApiResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
//Componente para manejar la autenticacion fallida
@Component
public class JwtAuthEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
       // Configurar el tipo de respuesta como JSON
        response.setContentType("application/json");
        //  Establecer el código de estado 401 (Unauthorized)
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Crear  envoltorio ApiResponseDto personalizado
        ApiResponseDto<Void> apiResponse = ApiResponseDto.<Void>builder()
                .mensaje("Acceso denegado: No tienes autorización o el token es inválido.")
                .success(false)
                .datos(null)
                .build();

        //  Convertir el objeto Java a un String JSON usando Jackson
        ObjectMapper mapper = new ObjectMapper();
        String jsonResponse = mapper.writeValueAsString(apiResponse);

        //  Escribir el JSON en el cuerpo de la respuesta
        response.getWriter().write(jsonResponse);
    }
}