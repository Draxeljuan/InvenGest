package com.proyecto.invengest.service.autenticacion;

import com.proyecto.invengest.entities.RolUsuario;
import com.proyecto.invengest.entities.Usuario;
import com.proyecto.invengest.repository.UsuarioRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioDetallesServicioTest {

    @Mock
    private UsuarioRepositorio usuarioRepositorio;

    @InjectMocks
    private UsuarioDetallesServicio usuarioDetallesServicio;

    private RolUsuario rolUsuario;
    private Usuario usuario;


    @BeforeEach
    void setUp() {
        rolUsuario = RolUsuario.builder()
                .idRolUsuario(1)
                .nombre("Admin")
                .build();

        usuario = Usuario.builder()
                .idUsuario(1)
                .nombre("Juan")
                .apellido("Perez")
                .email("drxjd@gmail.com")
                .telefono("3103364700")
                .idRol(rolUsuario)
                .nombreUsuario("JuanP")
                .contrasena("Secret")
                .build();

    }

    @Test
    public void loadUserByUsername() {

        when(usuarioRepositorio.findByNombreUsuario("JuanP")).thenReturn(Optional.of(usuario));

        UserDetails usuariodetalles = usuarioDetallesServicio.loadUserByUsername("JuanP");

        // Validar que la información extraida del usuario sea valida
        assertEquals("JuanP", usuariodetalles.getUsername());
        assertEquals("Secret", usuariodetalles.getPassword());
        assertTrue(usuariodetalles.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

    }

    @Test
    public void loadUserByUsernameError() {
        //Probando metodo cuando un usuario no existe o no es encontrado
        when(usuarioRepositorio.findByNombreUsuario("inexistente")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> usuarioDetallesServicio.loadUserByUsername("inexistente"));
    }
}