package com.jogadores.jogadores.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    private static final String BEARER = "bearerAuth";

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("Jogadores API")
                        .version("1.0")
                        .description("Cadastrar os jogadores no sistema e fornecer uma interface para que as demais aplicações possam verificar as suas credenciais e autorizações"))
                .components(new Components().addSecuritySchemes(BEARER,
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
                // Global: o botão Authorize do Swagger envia o JWT em todas as rotas
                .addSecurityItem(new SecurityRequirement().addList(BEARER));
    }
}
