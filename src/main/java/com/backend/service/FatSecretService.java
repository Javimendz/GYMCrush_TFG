package com.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class FatSecretService {

    @Value("${fatsecret.client.id}")
    private String clientId;

    @Value("${fatsecret.client.secret}")
    private String clientSecret;

    @Value("${fatsecret.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper; // Jackson

    public String obtenerTokenAutenticacion() {
        String url = "https://oauth.fatsecret.com/connect/token";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth(clientId, clientSecret);

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "client_credentials");
        map.add("scope", "basic");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
            
            // USANDO JACKSON CORRECTAMENTE:
            JsonNode root = objectMapper.readTree(response.getBody());
            if (root.has("access_token")) {
                return root.get("access_token").asText();
            }
            throw new RuntimeException("Respuesta de FatSecret no contiene token");
            
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener token con Jackson: " + e.getMessage());
        }
    }

    public String buscarRecetasPorMacros(int calorias, int proteinas, int carbohidratos, int grasas) {
        String token = obtenerTokenAutenticacion();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        int calPorPlato = calorias / 3; 
        String url = apiUrl + "?method=recipes.search&format=json" +
                     "&calories.from=" + (calPorPlato - 200) + 
                     "&calories.to=" + (calPorPlato + 200);

        HttpEntity<?> request = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
            String body = response.getBody();

            // JACKSON: Validamos si la respuesta es un error de FatSecret (como el de la IP)
            JsonNode root = objectMapper.readTree(body);
            if (root.has("error")) {
                String errorMsg = root.get("error").get("message").asText();
                System.err.println(" Error de FatSecret (Code " + root.get("error").get("code") + "): " + errorMsg);
                
                // Si hay error, devolvemos un JSON vacío pero válido para que no explote tu DB
                return "{\"recipes\": {\"recipe\": []}}"; 
            }

            return body; // Si todo está bien, devolvemos el JSON original
        } catch (Exception e) {
            System.err.println(" Error procesando recetas: " + e.getMessage());
            return "{\"recipes\": {\"recipe\": []}}";
        }
    }
}