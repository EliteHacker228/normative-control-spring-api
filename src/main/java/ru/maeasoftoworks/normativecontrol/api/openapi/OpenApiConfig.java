package ru.maeasoftoworks.normativecontrol.api.openapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@OpenAPIDefinition(
        info = @Info(
                title = "API для веб-приложения автоматического нормоконтроля",
                description = "API, который лежит в основе веб-приложения автоматического нормоконтроля, и обеспечивает взаимодействие клиентской части с модулем проверки документов", version = "1.0.0-beta1"
        )
)
@SecurityScheme(
        name = "JWT",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        scheme = "bearer"
)
public class OpenApiConfig {
}