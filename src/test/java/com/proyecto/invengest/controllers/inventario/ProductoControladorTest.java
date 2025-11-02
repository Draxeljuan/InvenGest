package com.proyecto.invengest.controllers.inventario;

import com.proyecto.invengest.config.test.CustomWebMvcTest;
import com.proyecto.invengest.dto.ProductoDTO;
import com.proyecto.invengest.service.inventario.ProductoServicio;
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

@CustomWebMvcTest(ProductoControlador.class)
class ProductoControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductoServicio productoServicio;

    private ProductoDTO productoDTO;

    @BeforeEach
    void setUp() {
        productoDTO = new ProductoDTO(
                "PROD001",
                3,
                "Mouse Logitech",
                new BigDecimal("85000"),
                new BigDecimal("60000"),
                LocalDate.of(2025, 10, 7),
                (short) 25,
                "Estante A3",
                1
        );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    public void listarProductos() throws Exception {
        when(productoServicio.listarProductos()).thenReturn(List.of(productoDTO));

        mockMvc.perform(get("/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProducto").value("PROD001"))
                .andExpect(jsonPath("$[0].nombre").value("Mouse Logitech"))
                .andExpect(jsonPath("$[0].precioVenta").value(85000))
                .andExpect(jsonPath("$[0].stock").value(25));
    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    public void obtenerProducto() throws Exception {
        when(productoServicio.obtenerProducto("PROD001")).thenReturn(productoDTO);

        mockMvc.perform(get("/productos/PROD001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value("PROD001"))
                .andExpect(jsonPath("$.nombre").value("Mouse Logitech"))
                .andExpect(jsonPath("$.precioVenta").value(85000));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    public void buscarProductos() throws Exception {
        when(productoServicio.buscarPorNombre("Mouse")).thenReturn(List.of(productoDTO));

        mockMvc.perform(get("/productos/buscar").param("nombre", "Mouse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idProducto").value("PROD001"))
                .andExpect(jsonPath("$[0].nombre").value("Mouse Logitech"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    public void crearProducto() throws Exception {
        when(productoServicio.crearProducto(any(ProductoDTO.class))).thenReturn(productoDTO);

        String json = """
            {
                "idProducto": "PROD001",
                "idCategoria": 3,
                "nombre": "Mouse Logitech",
                "precioVenta": 85000,
                "costoCompra": 60000,
                "fechaIngreso": "2025-10-07",
                "stock": 25,
                "ubicacion": "Estante A3",
                "idEstado": 1
            }
        """;

        mockMvc.perform(post("/productos/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value("PROD001"))
                .andExpect(jsonPath("$.nombre").value("Mouse Logitech"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    public void descontinuarProducto() throws Exception {
        doNothing().when(productoServicio).descontinuarProducto("PROD001");

        mockMvc.perform(put("/productos/descontinuar/PROD001"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    public void modificarProducto() throws Exception {
        when(productoServicio.modificarProducto(eq("PROD001"), any(ProductoDTO.class))).thenReturn(productoDTO);

        String json = """
            {
                "idProducto": "PROD001",
                "idCategoria": 3,
                "nombre": "Mouse Logitech",
                "precioVenta": 85000,
                "costoCompra": 60000,
                "fechaIngreso": "2025-10-07",
                "stock": 25,
                "ubicacion": "Estante A3",
                "idEstado": 1
            }
        """;

        mockMvc.perform(put("/productos/PROD001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idProducto").value("PROD001"))
                .andExpect(jsonPath("$.nombre").value("Mouse Logitech"));
    }
}