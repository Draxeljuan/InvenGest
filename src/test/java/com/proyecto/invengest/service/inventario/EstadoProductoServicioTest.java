package com.proyecto.invengest.service.inventario;

import com.proyecto.invengest.dto.EstadoProductoDTO;
import com.proyecto.invengest.entities.EstadoProducto;
import com.proyecto.invengest.repository.EstadoProductoRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstadoProductoServicioTest {

    @Mock
    private EstadoProductoRepositorio estadoProductoRepositorio;

    @InjectMocks
    private EstadoProductoServicio estadoProductoServicio;

    private EstadoProducto estadoProducto;

    @BeforeEach
    void setUp() {

        estadoProducto = EstadoProducto.builder()
                .id(1)
                .nombre("Stock Normal")
                .build();
    }

    @Test
    public void listarEstados() {

        // Construimos un nuevo estado para probar el metodo de listado
        EstadoProducto otroEstado = EstadoProducto.builder()
                .id(2)
                .nombre("Descontinuado")
                .build();

        // Cuando se consulte al repositorio temporal, se retonara el estado original creado
        // y el que se acaba de crear
        when(estadoProductoRepositorio.findAll()).thenReturn(List.of(estadoProducto, otroEstado));

        // Se llama al servicio para probar el listado
        List<EstadoProductoDTO> resultado = estadoProductoServicio.listarEstados();

        // Se validan los resultados
        assertEquals(2, resultado.size());
        assertEquals("Stock Normal", resultado.get(0).getNombre());
        assertEquals("Descontinuado", resultado.get(1).getNombre());

    }

    @Test
    public void obtenerEstado() {

        // Cuando se consulte al repositorio temporal se retorna el estado creado inicialmente
        when(estadoProductoRepositorio.findById(1)).thenReturn(Optional.of(estadoProducto));

        // Se llama al servicio para obtener el estado
        EstadoProductoDTO resultado = estadoProductoServicio.obtenerEstado(1);

        // Se validan los resultados
        assertEquals(1, resultado.getIdEstado());
        assertEquals("Stock Normal", resultado.getNombre());
    }

    @Test
    public void obtenerEstadoInexistente() {
        // Se establece que al consultar un estado que no existe se retorna empty
        when(estadoProductoRepositorio.findById(99)).thenReturn(Optional.empty());
        // Se valida la excepción obtenida al ejecutar el servicio con argumento empty
        assertThrows(RuntimeException.class, () -> estadoProductoServicio.obtenerEstado(99));
    }


}