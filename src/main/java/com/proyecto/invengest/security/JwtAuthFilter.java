package com.proyecto.invengest.security;

import com.proyecto.invengest.service.autenticacion.UsuarioDetallesServicio;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UsuarioDetallesServicio userDetailsService;

    public JwtAuthFilter(JwtUtils jwtUtils, UsuarioDetallesServicio userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        System.out.println("Encabezado Authorization recibido: " + authHeader); // Verificar qué token se está enviando

        String username = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            System.out.println("Token extraído: " + token); // Verificar si el token realmente tiene contenido

            try {
                username = jwtUtils.extractUsername(token);
            } catch (Exception e) {
                System.err.println("Error al extraer el username del token: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userDetails = userDetailsService.loadUserByUsername(username);

            try {
                if (jwtUtils.isTokenValid(token, userDetails)) {
                    // Extraer los roles directamente como lista de String
                    List<String> roles = (List<String>) jwtUtils.extractAllClaims(token).get("roles");

                    // Mapear los roles a authorities válidas
                    List<GrantedAuthority> authorities = roles.stream()
                            .map(role -> (GrantedAuthority) new SimpleGrantedAuthority(role))
                            .collect(Collectors.toList());

                    System.out.println("Autoridades desde token: " + authorities);

                    var authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            authorities
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println("Usuario autenticado: " + userDetails.getUsername());
                    System.out.println("Roles: " + authorities);
                }
            } catch (Exception e) {
                System.err.println("Error al validar el token JWT: " + e.getMessage());
            }
        } else {
            if (username == null) {
                System.err.println("Error: No se pudo extraer el username del token.");
            } else {
                System.err.println("Error: Ya existe una autenticación en el contexto de seguridad.");
            }
        }
        System.out.println("Encabezado Authorization recibido: " + request.getHeader("Authorization"));
        filterChain.doFilter(request, response);
    }
}
