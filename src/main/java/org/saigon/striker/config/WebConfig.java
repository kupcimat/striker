package org.saigon.striker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
@Import(SecurityConfig.class)
public class WebConfig implements WebFluxConfigurer {

    @Bean
    public DefaultErrorResponse defaultErrorResponse() {
        return new DefaultErrorResponse();
    }
}
