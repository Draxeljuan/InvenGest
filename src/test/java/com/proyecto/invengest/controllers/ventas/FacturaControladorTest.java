package com.proyecto.invengest.controllers.ventas;

import com.proyecto.invengest.config.test.CustomWebMvcTest;
import com.proyecto.invengest.dto.DetalleFacturaDTO;
import com.proyecto.invengest.dto.FacturaDTO;
import com.proyecto.invengest.service.ventas.FacturaServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@CustomWebMvcTest(FacturaControlador.class)
class FacturaControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacturaServicio facturaServicio;

    private FacturaDTO facturaDTO;
    private byte[] pdfBytes;

    @BeforeEach
    void setUp() {
        DetalleFacturaDTO detalle = new DetalleFacturaDTO(
                "Mouse Logitech",
                2,
                new BigDecimal("85000"),
                new BigDecimal("170000")
        );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    public void generarFactura() throws Exception {
        when(facturaServicio.generarFacturaDesdeVenta(101)).thenReturn(facturaDTO);
        when(facturaServicio.generarFacturaPDF(facturaDTO)).thenReturn("PDF simulado".getBytes());

        mockMvc.perform(get("/facturas/101/generar-pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Factura_101.pdf"))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/pdf"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes("PDF simulado".getBytes()));
    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMINISTRADOR"})
    void generarFacturaSiVentaNoExiste() throws Exception {
        when(facturaServicio.generarFacturaDesdeVenta(999)).thenThrow(new RuntimeException("Venta no encontrada"));

        mockMvc.perform(get("/facturas/999/generar-pdf"))
                .andExpect(status().isNotFound())
                .andExpect(content().bytes(new byte[0]));
    }

}