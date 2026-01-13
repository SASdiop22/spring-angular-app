package edu.miage.springboot.security;

import edu.miage.springboot.services.impl.AuthUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypcdto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Autowired
    JwtAuthFilter jwtAuthFilter;
    @Value("${app.dev.frontend.local}") // Ajout du $
    String allowedOrigins;
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
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authz -> authz
                        //AUTORISER EXPLICITEMENT LES REQUÊTES OPTIONS (CORS)
                        .requestMatchers(org.springframework.web.cors.CorsUtils::isPreFlightRequest).permitAll() 
                        .requestMatchers("/api/auth/login","/api/auth/signin")
                        .permitAll()
                        .requestMatchers("/", "/index.html", "*.ico", "*.css", "*.js")
                        .permitAll()
                        .requestMatchers("/actuator/**")
                        .hasRole("ADMIN")
                        .anyRequest()
                        .authenticated()
                        // Autoriser l'accès à la console H2
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        // Autoriser Swagger pour pouvoir tester tes APIs
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/login").permitAll()
                        // Autoriser tes endpoints d'authentification
                        .requestMatchers("/api/auth/**").permitAll()

                        // Autoriser à voir les offres sans être connecté
                        .requestMatchers(HttpMethod.GET, "/api/joboffers/**").permitAll()

                        // Autoriser les fichiers statiques du frontend
                        .requestMatchers("/", "/index.html", "/static/**", "/*.js", "/*.css", "/*.ico").permitAll()

                        // TOUT le reste demande une connexion
                        .anyRequest().authenticated()
                )
                // Pour H2-console (si nécessaire)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        if(StringUtils.hasText(allowedOrigins)){
            config.setAllowedOrigins(List.of(allowedOrigins));
            config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            config.setAllowedHeaders(List.of("Content-Type", "Authorization"));
            config.setAllowCredentials(true);
        }
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }



}
