package com.proyecto.invengest.controllers.inventario;

import com.proyecto.invengest.config.test.CustomWebMvcTest;
import com.proyecto.invengest.dto.AlertaDTO;
import com.proyecto.invengest.enumeradores.leidaAlerta;
import com.proyecto.invengest.service.inventario.AlertaServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;


import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@CustomWebMvcTest(AlertaControlador.class)
class AlertaControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlertaServicio alertaServicio;

    private AlertaDTO alertaDTO;

    @BeforeEach
    void setUp() {
        alertaDTO = new AlertaDTO(
                1,                      // idAlerta
                "PROD001",                    // idProducto
                LocalDate.of(2025, 10, 6),
                5,                      // idTipo
                leidaAlerta.no_visto
        );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void listarAlertas() throws Exception {
        when(alertaServicio.listarAlertas()).thenReturn(List.of(alertaDTO));

        mockMvc.perform(get("/alerta"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idAlerta").value(1))
                .andExpect(jsonPath("$[0].idProducto").value("PROD001"))
                .andExpect(jsonPath("$[0].tipo").value(5))
                .andExpect(jsonPath("$[0].leida").value("no_visto"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void listarAlertasConProducto() throws Exception {
        Map<String, Object> alertaMap = Map.of(
                "idAlerta", 1,
                "producto", "Mouse Logitech",
                "fecha", "2025-10-06",
                "tipo", "Stock Bajo",
                "leida", "no_visto"
        );

        when(alertaServicio.listarAlertasConProducto()).thenReturn(List.of(alertaMap));

        mockMvc.perform(get("/alerta/con-producto"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idAlerta").value(1))
                .andExpect(jsonPath("$[0].producto").value("Mouse Logitech"))
                .andExpect(jsonPath("$[0].tipo").value("Stock Bajo"))
                .andExpect(jsonPath("$[0].leida").value("no_visto"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void obtenerAlerta() throws Exception {
        when(alertaServicio.obtenerAlerta(1)).thenReturn(alertaDTO);

        mockMvc.perform(get("/alerta/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idAlerta").value(1))
                .andExpect(jsonPath("$.idProducto").value("PROD001"))
                .andExpect(jsonPath("$.tipo").value(5))
                .andExpect(jsonPath("$.leida").value("no_visto"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void limpiarAlertasInnecesarias() throws Exception {
        doNothing().when(alertaServicio).limpiarAlertasInnecesarias();

        mockMvc.perform(delete("/alerta/limpiar"))
                .andExpect(status().isOk())
                .andExpect(content().string("Alertas innecesarias limpiadas correctamente"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void eliminarAlerta() throws Exception {
        doNothing().when(alertaServicio).eliminarAlerta(1);

        mockMvc.perform(delete("/alerta/1"))
                .andExpect(status().isOk());
    }
}