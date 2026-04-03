package com.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class FatSecretService {

    @Value("${fatsecret.client.id}")
    private String clientId;

    @Value("${fatsecret.client.secret}")
    private String clientSecret;

    @Value("${fatsecret.oauth.url}")
    private String oauthUrl;

    @Value("${fatsecret.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate; // Necesitas declarar un @Bean RestTemplate en tu Webconfig
    private final ObjectMapper objectMapper;

    /**
     * PASO 1: Obtener el token de FatSecret
     */
    public String obtenerTokenAutenticacion() {
    String url = "https://oauth.fatsecret.com/connect/token";
    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    headers.setBasicAuth(clientId, clientSecret);

    MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
    map.add("grant_type", "client_credentials");
    map.add("scope", "basic");

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

    // CAMBIO CLAVE: Pedimos la respuesta como String, no como JsonNode u Objeto
    ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
    String body = response.getBody();

    // Extraemos el token con lógica de String básica para evitar Jackson
    if (body != null && body.contains("access_token\":\"")) {
        return body.split("access_token\":\"")[1].split("\"")[0];
    }
    
    throw new RuntimeException("No se pudo obtener el token de FatSecret");
}
    /**
     * PASO 2: Buscar recetas basadas en macros
     */
   public String buscarRecetasPorMacros(int calorias, int proteinas, int carbohidratos, int grasas) {
    String token = obtenerTokenAutenticacion();
    System.out.println("🔑 Token obtenido correctamente");

    HttpHeaders headers = new HttpHeaders();
    headers.setBearerAuth(token);

    // Reducimos el rango de búsqueda a algo más realista por plato
    int calPorPlato = calorias / 3; 

    String url = apiUrl + "?method=recipes.search&format=json" +
                 "&calories.from=" + (calPorPlato - 200) + 
                 "&calories.to=" + (calPorPlato + 200);

    System.out.println("🌐 Llamando a URL: " + url);

    HttpEntity<?> request = new HttpEntity<>(headers);
    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);

    // ESTO ES LO IMPORTANTE: Imprimir qué nos dice FatSecret
    System.out.println("🍱 Respuesta de FatSecret: " + response.getBody());

    return response.getBody(); 
}
    @jakarta.annotation.PostConstruct
    public void validarConfiguracion() {
        // COMENTA TODO ESTO PARA QUE NO EXPLOTE AL ARRANCAR
        /*
        System.out.println("--------------------------------------");
        System.out.println("🚀 FATSECRET CONFIG CHECK:");
        System.out.println("ID: " + (clientId != null ? clientId.substring(0, 4) + "****" : "NULL"));
        System.out.println("OAUTH URL: " + oauthUrl);
        System.out.println("--------------------------------------");
        */
        System.out.println("🚀 FatSecret Service cargado (Validación omitida)");
    }
}