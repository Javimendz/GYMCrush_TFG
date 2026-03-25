//Paquete
package com.backend.security.config;
//Imports
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
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
                .cors(Customizer.withDefaults())
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthEntryPoint)
                        )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                        )
                .authorizeHttpRequests(auth ->
                        auth
                                .requestMatchers("/api/v1/auth/**").permitAll() // Permitir acceso a la autenticacion
                                .requestMatchers("/chats/**").permitAll()  //Permitir sala chat
                                .requestMatchers("/chats-sockjs/**").permitAll() //Permitir sala chat
                                .requestMatchers("/swagger-ui/**").permitAll() //Permitir acceso a swagger
                                .requestMatchers("/v3/api-docs/**").permitAll() //Permitir acceso a swagger
                                .requestMatchers("/v3/api-docs.yaml").permitAll() //Permitir acceso a swagger

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
