package com.rjtmahinay.collateral.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

        @Bean
        public OpenAPI collateralServiceOpenAPI() {

                Server demoServer = new Server();
                demoServer.setUrl("https://collateral-service-git-rjtmahinay-dev.apps.rm1.0a51.p1.openshiftapps.com");
                demoServer.setDescription("Demo Server");

                Contact contact = new Contact();
                contact.setEmail("support@rjtmahinay.com");
                contact.setName("Collateral Service Team");
                contact.setUrl("https://www.rjtmahinay.com");

                License mitLicense = new License()
                                .name("MIT License")
                                .url("https://choosealicense.com/licenses/mit/");

                Info info = new Info()
                                .title("Collateral Management Service API")
                                .version("1.0.0")
                                .contact(contact)
                                .description("This API provides comprehensive collateral management capabilities including "
                                                +
                                                "collateral CRUD operations, encumbrance management, and auto loan valuation services. "
                                                +
                                                "It supports managing various types of collaterals and their associated encumbrances "
                                                +
                                                "with real-time valuation and market analysis features.")
                                .termsOfService("https://www.rjtmahinay.com/terms")
                                .license(mitLicense);

                return new OpenAPI()
                                .info(info)
                                .servers(List.of(demoServer));
        }
}
