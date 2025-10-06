package com.proyecto.invengest.service.autenticacion;

import com.proyecto.invengest.dto.UsuarioDTO;
import com.proyecto.invengest.entities.RolUsuario;
import com.proyecto.invengest.entities.Usuario;
import com.proyecto.invengest.exceptions.UsuarioNoEncontradoException;
import com.proyecto.invengest.repository.UsuarioRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServicioTest {

    @Mock
    private UsuarioRepositorio usuarioRepositorio;
    @InjectMocks
    private UsuarioServicio usuarioServicio;


    private RolUsuario rolUsuario1;
    private RolUsuario rolUsuario2;
    private Usuario usuario1;
    private Usuario usuario2;

    @BeforeEach
    void setUp() {

        rolUsuario1 = RolUsuario.builder()
                .idRolUsuario(1)
                .nombre("Admin")
                .build();
        rolUsuario2 = RolUsuario.builder()
                .idRolUsuario(2)
                .nombre("Vendedor")
                .build();
        usuario1 = Usuario.builder()
                .idUsuario(1)
                .nombre("Juan")
                .apellido("Perez")
                .email("drxjd@gmail.com")
                .telefono("3103364700")
                .idRol(rolUsuario1)
                .nombreUsuario("JuanP")
                .contrasena("Secret")
                .build();
        usuario2 = Usuario.builder()
                .idUsuario(2)
                .nombre("Ana")
                .apellido("Gómez")
                .email("ana@example.com")
                .telefono("987654321")
                .idRol(rolUsuario2)
                .nombreUsuario("anag")
                .contrasena("clave")
                .build();



    }

    @Test
    public void listarUsuarios() {
        // Cuando se ejecute el listado de usuarios, el repositorio retornara lo siguiente
        when(usuarioRepositorio.findAll()).thenReturn(List.of(usuario1, usuario2));

        // Se llama al servicio de listar usuarios para obtener los usuarios creados
        List<UsuarioDTO> usuariosListados = usuarioServicio.listarUsuarios();

        // Se valida que hay 2 usuarios
        assertEquals(2, usuariosListados.size());

        // Se valida la información retornada de los usuarios
        assertEquals("JuanP", usuariosListados.get(0).getNombreUsuario());
        assertEquals("anag", usuariosListados.get(1).getNombreUsuario());
        assertEquals("Admin", usuariosListados.get(0).getRolUsuario());

    }

    @Test
    public void obtenerUsuario() {
        // Se valida que cuando se use este metodo en el servicio retornara la info de usuario1
        when(usuarioRepositorio.findById(1)).thenReturn(Optional.of(usuario1));

        UsuarioDTO usuariodto = usuarioServicio.obtenerUsuario(1);

        // Se valida que los datos retornados coincidan
        assertEquals("JuanP", usuariodto.getNombreUsuario());
        assertEquals("Juan", usuariodto.getNombre());
        assertEquals("Admin", usuariodto.getRolUsuario());
    }

    @Test
    public void obtenerUsuarioNoExiste(){
        // Se asigna un id que no existe a la busqueda
        when(usuarioRepositorio.findById(99)).thenReturn(Optional.empty());
        // Se verifica que al consultar un usuario inexistente
        // Se arroja una excepción
        assertThrows(UsuarioNoEncontradoException.class, () -> usuarioServicio.obtenerUsuario(99));
    }
}