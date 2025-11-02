package com.proyecto.invengest.controllers.autenticacion;

import com.proyecto.invengest.security.JwtUtils;
import com.proyecto.invengest.service.autenticacion.IntentosFallidos;
import com.proyecto.invengest.service.autenticacion.UsuarioDetallesServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioDetallesServicio userDetailsService;
    private final IntentosFallidos intentosFallidos;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager,
                          UsuarioDetallesServicio userDetailsService,
                          JwtUtils jwtUtils, IntentosFallidos intentosFallidos) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
        this.intentosFallidos = intentosFallidos;
    }

    @PostMapping("/login")
    public ResponseEntity<?> autenticarUsuario(@RequestBody AuthRequest request) {

        // Validar intentos fallidos al autenticar un Usuario

        String usuario = request.getNombreUsuario();

        if (intentosFallidos.estaBloqueado(usuario)) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("message", "Usuario bloqueado temporalmente");

            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(respuesta);
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getNombreUsuario(), request.getContrasena())
            );
            intentosFallidos.limpiarIntentos(usuario);
        } catch (BadCredentialsException e) {
            intentosFallidos.registrarIntentoFallido(usuario);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales inválidas");
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getNombreUsuario());
        final String token = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(new AuthResponse(token));
    }
}
