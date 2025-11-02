package com.proyecto.invengest.controllers.inventario;

import com.proyecto.invengest.config.test.CustomWebMvcTest;
import com.proyecto.invengest.dto.CategoriaDTO;
import com.proyecto.invengest.service.inventario.CategoriaServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;



import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;



@CustomWebMvcTest(CategoriaControlador.class)
class CategoriaControladorTest {


    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoriaServicio categoriaServicio;

    private CategoriaDTO categoriaDTO;

    @BeforeEach
    void setUp() {
        categoriaDTO = new CategoriaDTO(
                1,
                "Electrónica",
                "Dispositivos y accesorios tecnológicos"
        );
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void listarCategorias() throws Exception {
        when(categoriaServicio.listarCategorias()).thenReturn(List.of(categoriaDTO));

        mockMvc.perform(get("/categorias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idCategoria").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Electrónica"))
                .andExpect(jsonPath("$[0].descripcion").value("Dispositivos y accesorios tecnológicos"));

    }
    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void obtenerCategoria() throws Exception {
        when(categoriaServicio.obtenerCategoria(1)).thenReturn(categoriaDTO);

        mockMvc.perform(get("/categorias/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idCategoria").value(1))
                .andExpect(jsonPath("$.nombre").value("Electrónica"))
                .andExpect(jsonPath("$.descripcion").value("Dispositivos y accesorios tecnológicos"));
    }


    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void crearCategoria() throws Exception {
        when(categoriaServicio.crearCategoria(any(CategoriaDTO.class))).thenReturn(categoriaDTO);

        String json = """
            {
                "idCategoria": 1,
                "nombre": "Electrónica",
                "descripcion": "Dispositivos y accesorios tecnológicos"
            }
        """;

        mockMvc.perform(post("/categorias/crear")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idCategoria").value(1))
                .andExpect(jsonPath("$.nombre").value("Electrónica"))
                .andExpect(jsonPath("$.descripcion").value("Dispositivos y accesorios tecnológicos"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void eliminarCategoria() throws Exception {
        doNothing().when(categoriaServicio).eliminarCategoria(1);

        mockMvc.perform(delete("/categorias/1"))
                .andExpect(status().isOk());
    }

}