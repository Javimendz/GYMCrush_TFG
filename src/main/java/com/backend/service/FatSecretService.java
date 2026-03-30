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
    private String obtenerTokenAutenticacion() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("scope", "basic");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        
        ResponseEntity<JsonNode> response = restTemplate.postForEntity(oauthUrl, request, JsonNode.class);
        return response.getBody().get("access_token").asText();
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
    System.out.println("--------------------------------------");
    System.out.println("🚀 FATSECRET CONFIG CHECK:");
    System.out.println("ID: " + (clientId != null ? clientId.substring(0, 4) + "****" : "NULL"));
    System.out.println("OAUTH URL: " + oauthUrl);
    System.out.println("--------------------------------------");
}
}