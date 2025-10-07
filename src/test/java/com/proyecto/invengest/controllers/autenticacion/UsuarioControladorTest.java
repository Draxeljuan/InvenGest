package com.proyecto.invengest.controllers.autenticacion;

import com.proyecto.invengest.config.test.CustomWebMvcTest;
import com.proyecto.invengest.dto.UsuarioDTO;
import com.proyecto.invengest.entities.RolUsuario;
import com.proyecto.invengest.entities.Usuario;
import com.proyecto.invengest.security.JwtAuthFilter;
import com.proyecto.invengest.security.JwtUtils;
import com.proyecto.invengest.service.autenticacion.UsuarioDetallesServicio;
import com.proyecto.invengest.service.autenticacion.UsuarioServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@CustomWebMvcTest(UsuarioControlador.class)
class UsuarioControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioServicio usuarioServicio;

    // Mockear las dependencias de seguridad
    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private JwtAuthFilter jwtAuthFilter;

    @MockitoBean
    private UsuarioDetallesServicio usuarioDetallesServicio;

    private UsuarioDTO usuarioDTO;
    private Usuario usuario;
    private RolUsuario rol;

    @BeforeEach
    void setUp() {
        rol = RolUsuario.builder()
                .idRolUsuario(1)
                .nombre("Administrador")
                .build();

        usuario = Usuario.builder()
                .idUsuario(1)
                .nombre("Juan Pérez")
                .email("juan@example.com")
                .telefono("123456789")
                .ultimoAcceso(Instant.now())
                .idRol(rol)
                .nombreUsuario("juanp")
                .build();

        usuarioDTO = new UsuarioDTO(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getEmail(),
                usuario.getTelefono(),
                usuario.getUltimoAcceso(),
                rol.getNombre(),
                usuario.getNombreUsuario()
        );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void listarUsuarios() throws Exception {
        when(usuarioServicio.listarUsuarios()).thenReturn(List.of(usuarioDTO));

        mockMvc.perform(get("/usuario"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombreUsuario").value("juanp"))
                .andExpect(jsonPath("$[0].nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$[0].email").value("juan@example.com"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void obtenerUsuario() throws Exception {
        when(usuarioServicio.obtenerUsuario(1)).thenReturn(usuarioDTO);

        mockMvc.perform(get("/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUsuario").value("juanp"))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"))
                .andExpect(jsonPath("$.email").value("juan@example.com"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void actualizarUsuario() throws Exception {
        when(usuarioServicio.actualizarUsuario(eq(1), any(Usuario.class))).thenReturn(usuarioDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/usuario/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                                "idUsuario": 1,
                                "nombreUsuario": "juanp",
                                "nombre": "Juan",
                                "apellido": "Pérez",
                                "email": "juan@example.com",
                                "telefono": "123456789"
                            }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombreUsuario").value("juanp"))
                .andExpect(jsonPath("$.nombre").value("Juan Pérez"));
    }
}