package com.proyecto.invengest.controllers.ventas;


import com.proyecto.invengest.config.test.CustomWebMvcTest;
import com.proyecto.invengest.dto.DetalleVentaDTO;
import com.proyecto.invengest.dto.VentaDTO;
import com.proyecto.invengest.service.ventas.VentaServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@CustomWebMvcTest(VentaControlador.class)
class VentaControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VentaServicio ventaServicio;

    private VentaDTO ventaDTO;

    @BeforeEach
    void setUp() {
        DetalleVentaDTO detalle = new DetalleVentaDTO(
                "PROD001",
                new BigDecimal("85000"),
                new BigDecimal("170000"),
                2
        );

        ventaDTO = new VentaDTO(
                101,
                10,
                LocalDate.of(2025, 10, 7),
                5,
                new BigDecimal("170000"),
                List.of(detalle)
        );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    public void obtenerVenta() throws Exception {
        when(ventaServicio.obtenerVenta(101)).thenReturn(ventaDTO);

        mockMvc.perform(get("/ventas/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idVenta").value(101))
                .andExpect(jsonPath("$.idUsuario").value(10))
                .andExpect(jsonPath("$.idCliente").value(5))
                .andExpect(jsonPath("$.total").value(170000))
                .andExpect(jsonPath("$.detalles[0].idProducto").value("PROD001"))
                .andExpect(jsonPath("$.detalles[0].cantidad").value(2));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    public void listarVentas() throws Exception {
        when(ventaServicio.listarVentas()).thenReturn(List.of(ventaDTO));

        mockMvc.perform(get("/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idVenta").value(101))
                .andExpect(jsonPath("$[0].idUsuario").value(10))
                .andExpect(jsonPath("$[0].idCliente").value(5))
                .andExpect(jsonPath("$[0].total").value(170000))
                .andExpect(jsonPath("$[0].detalles[0].idProducto").value("PROD001"))
                .andExpect(jsonPath("$[0].detalles[0].cantidad").value(2));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    public void crearVenta() throws Exception {
        when(ventaServicio.crearVenta(any(VentaDTO.class))).thenReturn(ventaDTO);

        String json = """
            {
                "idVenta": 101,
                "idUsuario": 10,
                "fecha": "2025-10-07",
                "idCliente": 5,
                "total": 170000,
                "detalles": [
                    {
                        "idProducto": "PROD001",
                        "precioUnitario": 85000,
                        "subtotal": 170000,
                        "cantidad": 2
                    }
                ]
            }
        """;

        mockMvc.perform(post("/ventas/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idVenta").value(101))
                .andExpect(jsonPath("$.idCliente").value(5))
                .andExpect(jsonPath("$.detalles[0].idProducto").value("PROD001"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    public void eliminarVenta() throws Exception {
        doNothing().when(ventaServicio).eliminarVenta(101);

        mockMvc.perform(delete("/ventas/101"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    public void actualizarVenta() throws Exception {

        when(ventaServicio.actualizarVenta(eq(101), any(VentaDTO.class))).thenReturn(ventaDTO);

        String json = """
            {
                "idVenta": 101,
                "idUsuario": 10,
                "fecha": "2025-10-07",
                "idCliente": 5,
                "total": 170000,
                "detalles": [
                    {
                        "idProducto": "PROD001",
                        "precioUnitario": 85000,
                        "subtotal": 170000,
                        "cantidad": 2
                    }
                ]
            }
        """;

        mockMvc.perform(put("/ventas/101")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idVenta").value(101))
                .andExpect(jsonPath("$.idCliente").value(5))
                .andExpect(jsonPath("$.detalles[0].idProducto").value("PROD001"));
    }
}