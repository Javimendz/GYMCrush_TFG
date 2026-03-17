//Paquete
package com.backend.security.config;
//Imports
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.backend.security.jwt.JwtAuthEntryPoint;
import com.backend.security.jwt.JwtAuthenticationFilter;

@Configuration //Configuracion de seguridad
@RequiredArgsConstructor // Constructor con argumentos requeridos
@EnableMethodSecurity // Habilitar la seguridad basada en metodos
public class SecurityConfig {

    private final JwtAuthEntryPoint jwtAuthEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthEntryPoint)
                        )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        )
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/api/v1/auth/**").permitAll() // Permitir acceso a la autenticacion
                                .requestMatchers(HttpMethod.GET, "/api/v1/usuario/**").permitAll() // Permitir acceso a la obtencion de usuarios
                                .requestMatchers("/api/v1/reservas/**").permitAll()
                                .requestMatchers("/api/v1/actividades/**").permitAll() // Permitir acceso a la obtencion de reservas
                                .requestMatchers("/api/v1/horarios/**").permitAll() // Permitir acceso a la obtencion de horarios
                                .anyRequest().authenticated() // Requerir autenticacion para cualquier otra solicitud
                );
                http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
                return http.build();
    }
    // Bean para el codificador de contraseñas
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    // Bean para el administrador de autenticacion
    @Bean
    public AuthenticationManager authenticationManager
            (AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

















}
