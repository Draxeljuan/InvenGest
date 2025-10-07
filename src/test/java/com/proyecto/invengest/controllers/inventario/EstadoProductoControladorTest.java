package com.proyecto.invengest.controllers.inventario;


import com.proyecto.invengest.config.test.CustomWebMvcTest;
import com.proyecto.invengest.dto.EstadoProductoDTO;
import com.proyecto.invengest.service.inventario.EstadoProductoServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@CustomWebMvcTest(EstadoProductoControlador.class)
class EstadoProductoControladorTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    EstadoProductoServicio estadoProductoServicio;

    private EstadoProductoDTO estadoProductoDTO;


    @BeforeEach
    void setUp() {

        estadoProductoDTO = new EstadoProductoDTO(
                1,
                "Disponible"
        );

    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void listarEstados() throws Exception {
        when(estadoProductoServicio.listarEstados()).thenReturn(List.of(estadoProductoDTO));

        mockMvc.perform(get("/estados-producto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idEstado").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Disponible"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void obtenerEstado() throws Exception {
        when(estadoProductoServicio.obtenerEstado(1)).thenReturn(estadoProductoDTO);

        mockMvc.perform(get("/estados-producto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idEstado").value(1))
                .andExpect(jsonPath("$.nombre").value("Disponible"));
    }
}