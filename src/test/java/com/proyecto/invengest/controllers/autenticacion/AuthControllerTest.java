package com.proyecto.invengest.controllers.autenticacion;

import com.proyecto.invengest.config.test.CustomWebMvcTest;
import com.proyecto.invengest.security.JwtUtils;
import com.proyecto.invengest.service.autenticacion.UsuarioDetallesServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test para AuthController
 *
 * IMPORTANTE: Este controlador SÍ usa componentes de seguridad directamente,
 * pero NO necesita los filtros de seguridad (JWT) porque él mismo genera los tokens.
 */
@CustomWebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private UsuarioDetallesServicio userDetailsService;

    @MockitoBean
    private JwtUtils jwtUtils;

    private UserDetails userDetails;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        // Crear un usuario mock para los tests
        userDetails = User.builder()
                .username("testuser")
                .password("password123")
                .authorities(Collections.emptyList())
                .build();

        // Crear un objeto Authentication mock
        authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                "password123",
                Collections.emptyList()
        );
    }

    @Test
    void autenticarUsuario_CredencialesCorrectas_RetornaToken() throws Exception {
        // Arrange: Configurar el comportamiento de los mocks
        String expectedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.test.token";

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        when(userDetailsService.loadUserByUsername("testuser"))
                .thenReturn(userDetails);

        when(jwtUtils.generateToken(userDetails))
                .thenReturn(expectedToken);

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "nombreUsuario": "testuser",
                                "contrasena": "password123"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(expectedToken));

        // Verify
        verify(authenticationManager, times(1))
                .authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userDetailsService, times(1))
                .loadUserByUsername("testuser");
        verify(jwtUtils, times(1))
                .generateToken(userDetails);
    }

    @Test
    void autenticarUsuario_CredencialesIncorrectas_RetornaUnauthorized() throws Exception {
        // Arrange: Simular fallo de autenticación
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales inválidas"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "nombreUsuario": "wronguser",
                                "contrasena": "wrongpassword"
                            }
                        """))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Credenciales inválidas"));

        // Verify: No se deben llamar estos servicios si la autenticación falla
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(jwtUtils, never()).generateToken(any(UserDetails.class));
    }

    @Test
    void autenticarUsuario_UsuarioNoExiste_RetornaUnauthorized() throws Exception {
        // Arrange: Usuario no encontrado
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Usuario no encontrado"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "nombreUsuario": "nonexistent",
                                "contrasena": "anypassword"
                            }
                        """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void autenticarUsuario_ContrasenaIncorrecta_RetornaUnauthorized() throws Exception {
        // Arrange: Contraseña incorrecta
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Contraseña incorrecta"));

        // Act & Assert
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "nombreUsuario": "testuser",
                                "contrasena": "wrongpassword"
                            }
                        """))
                .andExpect(status().isUnauthorized());
    }
}