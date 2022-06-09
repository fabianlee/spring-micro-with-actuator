/**
 * Seeing if we can change the context path called by the OpenAPI swagger page
 * when we are using external ingress to change the path
 */
package org.fabianlee.springmicrowithactuator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfiguration {

    /*
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("/api/v1"));
    }
    */
}