package com.conti_talent.springboot.appweb.conti_talent_web.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final SessionSecurityFilter sessionSecurityFilter;

    public SecurityConfig(SessionSecurityFilter sessionSecurityFilter) {
        this.sessionSecurityFilter = sessionSecurityFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .addFilterBefore(sessionSecurityFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/index", "/index.html",
                                "/login", "/login.html",
                                "/registro", "/registro.html",
                                "/ofertas", "/ofertas.html",
                                "/detalle-oferta", "/detalle-oferta.html",
                                "/areas", "/areas.html",
                                "/contacto", "/contacto.html",
                                "/publicidad", "/publicidad.html",
                                "/css/**", "/js/**", "/img/**", "/images/**", "/assets/**",
                                "/uploads/**",
                                "/error", "/error/**",
                                "/api/auth/**",
                                "/api/ofertas/**", "/api/areas/**"
                        ).permitAll()
                        .requestMatchers("/admin/**", "/api/usuarios/**", "/api/roles/**",
                                "/api/preguntas/**", "/api/metricas/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/postular", "/postular.html", "/evaluacion", "/evaluacion.html",
                                "/mi-estado", "/mi-estado.html", "/mis-respuestas", "/mis-respuestas.html",
                                "/api/postulantes/**", "/api/evaluaciones/**", "/api/estados/**",
                                "/api/uploads/**")
                        .authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler((request, response, accessDeniedException) -> {
                            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                            request.getRequestDispatcher("/error/403").forward(request, response);
                        })
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (request.getRequestURI().startsWith("/api/")) {
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                            } else {
                                response.sendRedirect(request.getContextPath() + "/login");
                            }
                        })
                );

        return http.build();
    }
}
