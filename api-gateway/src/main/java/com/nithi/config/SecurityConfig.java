package com.nithi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

    @Configuration
    @EnableWebFluxSecurity
    public class SecurityConfig {

        @Bean
        public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity){

            serverHttpSecurity.csrf(csrfSpec -> csrfSpec.disable())
                    .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                            .pathMatchers("/eureka/**")
                            .permitAll()
                            .pathMatchers("/actuator/**")
                            .permitAll()
                            .anyExchange()
                            .authenticated())
                    .oauth2ResourceServer(oauth2ResourceServerSpec ->
                            oauth2ResourceServerSpec.jwt(withDefaults())
                    );

            return serverHttpSecurity.build();
        }

    }
