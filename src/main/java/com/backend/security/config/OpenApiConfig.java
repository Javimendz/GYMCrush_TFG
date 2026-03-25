package com.backend.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.OpenAPI;
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API para GymCrush - TFG")
                        .version("1.0")
                        .description("Documentación de la API Restful para GymCrush. Proyecto de fin de grado.")
                        .contact(new Contact()
                                .name("GymCrush")
                                .email("gymcrush@example.com")
                                .url("https://www.gymcrush.com"))
                        .license(new License()

                                .name("Apache 2.0 License")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")

                        ));

    }
}
