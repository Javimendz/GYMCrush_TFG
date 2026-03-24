//Paquete
package com.backend.security.jwt;

//Imports
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException; // Nueva importación para la excepción de firma
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.crypto.SecretKey; // Importar SecretKey para el cast en verifyWith
//Clase para generar tokens JWT

@Component // Componente para inyectar la clase en otras partes de la aplicacion
@ConfigurationProperties(prefix = "jwt") // Propiedades de configuracion para la clase,todos con prefijo jwt
public class JwtGenerator {

    @Value("${jwt.secret}") // Propiedad de configuracion para la clave secreta del token
    private String jwtSecret;

    @Value("${jwt.expiration}") // Propiedad de configuracion para la expiracion del token
    private long jwtExpiration;

    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        
        // Extraer los roles del usuario autenticado
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
        
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(username)
                .claim("roles", roles) // Agregar los roles al token
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    // Método actualizado para obtener el nombre de usuario del token
    public String getUsernameFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }

    // Método para obtener los roles del token JWT
    @SuppressWarnings("unchecked")
    public List<String> getRoleFromJwt(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("roles", List.class);
    }

    // Método actualizado para validar el token
    public boolean validateToken(String token) {
        try {
            // La forma moderna para validación también
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token); // Aquí solo nos interesa que no lance excepción
            return true;
        } catch (MalformedJwtException e) {
            System.out.println("Invalid JWT token: " + e.getMessage());
        } catch (ExpiredJwtException e) {
            System.out.println("JWT token is expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("JWT token is unsupported: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("JWT claims string is empty: " + e.getMessage());
        } catch (SignatureException e) { // Importante para HS512
            System.out.println("Signature validation failed: " + e.getMessage());
        }
        return false;
    }

    //Metodo para generar el token jwt para el qr temporal
    public String generateQrToken(String username, Long userId){
        //Fecha actual
        Date currentDate = new Date();

        //Tiempo de expiracion corto
        Long qrExpiration = 30000L;
        //Fecha de expiracion
        Date expireDate = new Date(currentDate.getTime() + qrExpiration);
        //Construir el token
        return Jwts.builder()
                .subject(username)
                .claim("userId", userId)
                .claim("purpose", "QR_ACCESS")
                .issuedAt(currentDate)
                .expiration(expireDate)
                .signWith(getSigningKey(), Jwts.SIG.HS512)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }
}
