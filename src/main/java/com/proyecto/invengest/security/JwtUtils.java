package com.proyecto.invengest.security;

import com.proyecto.invengest.entities.Usuario;
import com.proyecto.invengest.exceptions.UsuarioNoEncontradoException;
import com.proyecto.invengest.repository.UsuarioRepositorio;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    private final JwtConfig jwtConfig;
    private final UsuarioRepositorio usuarioRepositorio;

    public JwtUtils(JwtConfig jwtConfig, UsuarioRepositorio usuarioRepositorio) {
        this.jwtConfig = jwtConfig;
        this.usuarioRepositorio = usuarioRepositorio;
    }

    public String getSecretKey(){
        return jwtConfig.getSecret();
    }


    public String extractUsername(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("El token está vacío o no es válido.");
        }
        if (!token.contains(".")) {
            throw new MalformedJwtException("Formato de token inválido. No contiene puntos.");
        }

        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {

        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("token esta vacio");
        }

        return Jwts
                .parser()
                .setSigningKey(getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public String generateToken(UserDetails userDetails) {
        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepositorio.findByNombreUsuario(userDetails.getUsername())
                .orElseThrow(() -> new UsuarioNoEncontradoException("Usuario no encontrado con username: " + userDetails.getUsername()));

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts
                .builder()
                .setSubject(usuario.getNombreUsuario()) // Mantener el nombre de usuario en "sub"
                .claim("idUsuario", usuario.getIdUsuario()) // Agregar ID del usuario
                .claim("roles", roles) // Guardar roles como lista de strings
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Expira en 10 horas
                .signWith(SignatureAlgorithm.HS256, getSecretKey())
                .compact();
    }

}