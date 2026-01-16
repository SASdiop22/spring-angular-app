package edu.miage.springboot.security;

import edu.miage.springboot.services.impl.security.AuthUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    AuthUserDetailsService authUserDetailsService;
   
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // 🔓 Endpoints publics (pas de JWT)
        if (path.startsWith("/api/auth")
                || path.startsWith("/api/test")
                || path.startsWith("/api/ai")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            username = jwtService.extractUsername(token);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            System.out.println("🔐 JwtAuthFilter - Authentification pour: " + username);
            UserDetails userDetails = authUserDetailsService.loadUserByUsername(username);
            if (jwtService.validateToken(token, userDetails)) {
                // Utiliser DIRECTEMENT les autorités de UserDetails (qui charge depuis la base)
                System.out.println("✅ JwtAuthFilter - Autorités de UserDetails: " + userDetails.getAuthorities());

                UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                System.out.println("✅ JwtAuthFilter - Authentification définie avec succès pour: " + username);
            } else {
                System.err.println("❌ JwtAuthFilter - Token invalide pour: " + username);
            }
        }

        filterChain.doFilter(request, response);
    }
}
