package edu.miage.springboot.security;

import edu.miage.springboot.services.impl.security.AuthUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    JwtAuthFilter jwtAuthFilter;

    @Bean
    public UserDetailsService userDetailsService(){
        return new AuthUserDetailsService();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                // 1. Configuration CORS propre (AVANT CSRF)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Désactivation CSRF (nécessaire pour les API REST)
                .csrf(csrf -> csrf.disable())

                // 3. Gestion de session Stateless (pas de session serveur)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Autorisation des requêtes
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/auth/**").permitAll() // Login et Register publics
                        .requestMatchers(HttpMethod.GET, "/api/joboffers/**").permitAll() // Offres publiques en lecture
                        .requestMatchers(HttpMethod.GET, "/api/files/**").permitAll() // Téléchargement des fichiers (CVs)
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Permettre les pré-requêtes CORS
                        .requestMatchers("/", "/index.html", "/*.ico", "/*.css", "/*.js").permitAll() // Frontend statique
                        .requestMatchers("/actuator/**").permitAll() // Actuator de Spring Boot
                        .anyRequest().authenticated() // Le reste nécessite un token
                )

                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // On autorise explicitement ton Frontend Angular
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type", "Access-Control-Allow-Origin"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}