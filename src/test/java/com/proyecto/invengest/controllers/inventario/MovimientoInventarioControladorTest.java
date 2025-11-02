package com.proyecto.invengest.controllers.inventario;

import com.proyecto.invengest.config.test.CustomWebMvcTest;
import com.proyecto.invengest.dto.MovimientoInventarioDTO;
import com.proyecto.invengest.service.inventario.MovimientoInventarioServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@CustomWebMvcTest(MovimientoInventarioControlador.class)
class MovimientoInventarioControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovimientoInventarioServicio movimientoInventarioServicio;

    private MovimientoInventarioDTO movimientoDTO;

    @BeforeEach
    void setUp() {
        movimientoDTO = new MovimientoInventarioDTO(
                1,
                "PROD001",
                10,
                2,
                5,
                LocalDate.of(2025, 10, 7),
                "Ingreso por compra"
        );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void listarMovimientos() throws Exception {

        when(movimientoInventarioServicio.listarMovimientos()).thenReturn(List.of(movimientoDTO));

        mockMvc.perform(get("/movimiento"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idMovimientoInventario").value(1))
                .andExpect(jsonPath("$[0].idProducto").value("PROD001"))
                .andExpect(jsonPath("$[0].idUsuario").value(10))
                .andExpect(jsonPath("$[0].idMovimiento").value(2))
                .andExpect(jsonPath("$[0].cantidad").value(5))
                .andExpect(jsonPath("$[0].fechaMovimiento").value("2025-10-07"))
                .andExpect(jsonPath("$[0].observacion").value("Ingreso por compra"));

    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void obtenerMovimiento() throws Exception {
        when(movimientoInventarioServicio.obtenerMovimiento(1)).thenReturn(movimientoDTO);

        mockMvc.perform(get("/movimiento/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMovimientoInventario").value(1))
                .andExpect(jsonPath("$.idProducto").value("PROD001"))
                .andExpect(jsonPath("$.idUsuario").value(10))
                .andExpect(jsonPath("$.idMovimiento").value(2))
                .andExpect(jsonPath("$.cantidad").value(5))
                .andExpect(jsonPath("$.fechaMovimiento").value("2025-10-07"))
                .andExpect(jsonPath("$.observacion").value("Ingreso por compra"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void modificarMovimiento() throws Exception{
        when(movimientoInventarioServicio.modificarMovimiento(eq(1), any(MovimientoInventarioDTO.class)))
                .thenReturn(movimientoDTO);

        String json = """
            {
                "idMovimientoInventario": 1,
                "idProducto": "PROD001",
                "idUsuario": 10,
                "idMovimiento": 2,
                "cantidad": 5,
                "fechaMovimiento": "2025-10-07",
                "observacion": "Ingreso por compra"
            }
        """;

        mockMvc.perform(put("/movimiento/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idMovimientoInventario").value(1))
                .andExpect(jsonPath("$.idProducto").value("PROD001"))
                .andExpect(jsonPath("$.idUsuario").value(10))
                .andExpect(jsonPath("$.idMovimiento").value(2))
                .andExpect(jsonPath("$.cantidad").value(5))
                .andExpect(jsonPath("$.fechaMovimiento").value("2025-10-07"))
                .andExpect(jsonPath("$.observacion").value("Ingreso por compra"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void eliminarMovimiento() throws Exception {
        doNothing().when(movimientoInventarioServicio).eliminarMovimiento(1);

        mockMvc.perform(delete("/movimiento/1"))
                .andExpect(status().isOk());
    }


}