package com.proyecto.invengest.controllers.ventas;

import com.proyecto.invengest.config.test.CustomWebMvcTest;
import com.proyecto.invengest.dto.ClienteDTO;
import com.proyecto.invengest.service.ventas.ClienteServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@CustomWebMvcTest(ClienteControlador.class)
class ClienteControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteServicio clienteServicio;

    private ClienteDTO clienteDTO;

    @BeforeEach
    void setUp() {
        clienteDTO = new ClienteDTO(
                1,
                "Juan",
                "Carlos",
                "Pérez",
                "Gómez"
        );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void listarClientes() throws Exception {
        when(clienteServicio.obtenerClientes()).thenReturn(List.of(clienteDTO));

        mockMvc.perform(get("/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCliente").value(1))
                .andExpect(jsonPath("$[0].primerNombre").value("Juan"))
                .andExpect(jsonPath("$[0].segundoNombre").value("Carlos"))
                .andExpect(jsonPath("$[0].primerApellido").value("Pérez"))
                .andExpect(jsonPath("$[0].segundoApellido").value("Gómez"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void obtenerCliente() throws Exception {
        when(clienteServicio.obtenerCliente(1)).thenReturn(clienteDTO);

        mockMvc.perform(get("/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCliente").value(1))
                .andExpect(jsonPath("$.primerNombre").value("Juan"))
                .andExpect(jsonPath("$.primerApellido").value("Pérez"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void crearCliente() throws Exception {
        when(clienteServicio.crearCliente(any(ClienteDTO.class))).thenReturn(clienteDTO);

        String json = """
            {
                "idCliente": 1,
                "primerNombre": "Juan",
                "segundoNombre": "Carlos",
                "primerApellido": "Pérez",
                "segundoApellido": "Gómez"
            }
        """;

        mockMvc.perform(post("/clientes/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCliente").value(1))
                .andExpect(jsonPath("$.primerNombre").value("Juan"))
                .andExpect(jsonPath("$.primerApellido").value("Pérez"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void actualizarCliente() throws Exception {
        when(clienteServicio.actualizarCliente(eq(1), any(ClienteDTO.class))).thenReturn(clienteDTO);

        String json = """
            {
                "idCliente": 1,
                "primerNombre": "Juan",
                "segundoNombre": "Carlos",
                "primerApellido": "Pérez",
                "segundoApellido": "Gómez"
            }
        """;

        mockMvc.perform(put("/clientes/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCliente").value(1))
                .andExpect(jsonPath("$.primerNombre").value("Juan"))
                .andExpect(jsonPath("$.primerApellido").value("Pérez"));
    }
}