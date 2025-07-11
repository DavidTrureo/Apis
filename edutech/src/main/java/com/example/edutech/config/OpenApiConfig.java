package com.example.edutech.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "EduTech API",
        version = "1.0.0",
        description = "API REST para la plataforma educativa EduTech. Permite la gestión de cursos, usuarios, inscripciones y más.",
        contact = @Contact(
            name = "Equipo de Desarrollo EduTech",
            email = "dev-team@edutech.com"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "http://www.apache.org/licenses/LICENSE-2.0.html"
        )
    )
)
public class OpenApiConfig {
    // Esta clase puede estar vacía, solo sirve para contener la anotación @OpenAPIDefinition.
}