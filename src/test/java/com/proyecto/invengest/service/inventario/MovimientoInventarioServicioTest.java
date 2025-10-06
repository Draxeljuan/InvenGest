package com.proyecto.invengest.service.inventario;

import com.proyecto.invengest.dto.MovimientoInventarioDTO;
import com.proyecto.invengest.entities.MovimientoInventario;
import com.proyecto.invengest.entities.Producto;
import com.proyecto.invengest.entities.TipoMovimiento;
import com.proyecto.invengest.entities.Usuario;
import com.proyecto.invengest.exceptions.MovimientoInventarioNoEncontradoException;
import com.proyecto.invengest.repository.MovimientoInventarioRepositorio;
import com.proyecto.invengest.repository.ProductoRepositorio;
import com.proyecto.invengest.repository.TipoMovimientoRepositorio;
import com.proyecto.invengest.repository.UsuarioRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MovimientoInventarioServicioTest {

    @Mock
    private MovimientoInventarioRepositorio movimientoInventarioRepositorio;

    @Mock
    private ProductoRepositorio productoRepositorio;

    @Mock
    private TipoMovimientoRepositorio tipoMovimientoRepositorio;

    @Mock
    private UsuarioRepositorio usuarioRepositorio;

    @InjectMocks
    private MovimientoInventarioServicio movimientoInventarioServicio;

    private Producto producto;
    private TipoMovimiento tipoMovimiento;
    private Usuario usuario;
    private MovimientoInventario movimiento;
    private MovimientoInventarioDTO movimientoDTO;

    @BeforeEach
    void setUp() {
        producto = Producto.builder().idProducto("PROD001").build();
        tipoMovimiento = TipoMovimiento.builder().idMovimiento(1).build();
        usuario = Usuario.builder().idUsuario(10).build();

        movimiento = MovimientoInventario.builder()
                .idMovimientoInventario(100)
                .idProducto(producto)
                .idMovimiento(tipoMovimiento)
                .idUsuario(usuario)
                .cantidad(5)
                .fechaMovimiento(LocalDate.now())
                .observacion("Ingreso por compra")
                .build();

        movimientoDTO = new MovimientoInventarioDTO(
                100, "PROD001", 10, 1, 5, LocalDate.now(), "Ingreso por compra"
        );
    }

    @Test
    public void listarMovimientos() {
        // Se asigna a la consulta del repositorio el movimiento creado
        when(movimientoInventarioRepositorio.findAll()).thenReturn(List.of(movimiento));
        // Se lista el movimiento de inventario con el servicio
        List<MovimientoInventarioDTO> resultado = movimientoInventarioServicio.listarMovimientos();
        // Se valida el resultado
        assertEquals(1, resultado.size());
        assertEquals("PROD001", resultado.getFirst().getIdProducto());
        assertEquals("Ingreso por compra", resultado.getFirst().getObservacion());
    }

    @Test
    public void obtenerMovimiento() {
        // Al consultar el movimiento se retorna el creado inicialmente
        when(movimientoInventarioRepositorio.findById(100)).thenReturn(Optional.of(movimiento));
        // Se llama al servicio que retornara el movimiento creado
        MovimientoInventarioDTO resultado = movimientoInventarioServicio.obtenerMovimiento(100);
        // Se valida el resultado
        assertEquals("PROD001", resultado.getIdProducto());
        assertEquals(10, resultado.getIdUsuario());
    }
    @Test
    public void obtenerMovimientoNotExist() {
        // Cuando se consulte por un movimiento inexistente se retorna empty
        when(movimientoInventarioRepositorio.findById(999)).thenReturn(Optional.empty());
        // Se valida la excepción arrojada por el servicio
        assertThrows(MovimientoInventarioNoEncontradoException.class, () -> movimientoInventarioServicio.obtenerMovimiento(999));
    }

    @Test
    public void registrarMovimientoInventario() {
        // Configuramos los elementos necesarios para un movimiento de inventario
        // en el repositorio temporal
        when(productoRepositorio.findById("PROD001")).thenReturn(Optional.of(producto));
        when(tipoMovimientoRepositorio.findById(1)).thenReturn(Optional.of(tipoMovimiento));
        when(usuarioRepositorio.findById(10)).thenReturn(Optional.of(usuario));
        // Se pasa al servicio un DTO con los datos del movimiento de inventario
        movimientoInventarioServicio.registrarMovimientoInventario(movimientoDTO);
        // Se valida que el movimiento de inventario se realice
        verify(movimientoInventarioRepositorio).save(ArgumentMatchers.<MovimientoInventario>any());

    }

    @Test
    void eliminarMovimiento() {
        // Cuando se consulta el movimiento de inventario se retorna true
        when(movimientoInventarioRepositorio.existsById(100)).thenReturn(true);
        // Se ejecuta el servicio para eliminar el movimiento
        movimientoInventarioServicio.eliminarMovimiento(100);
        // Se valida que el movimiento haya sido eliminado
        verify(movimientoInventarioRepositorio).deleteById(100);
    }

    @Test
    void eliminarMovimientoInexistente() {
        // Si se consulta un movimiento inexistente se retorna false
        when(movimientoInventarioRepositorio.existsById(999)).thenReturn(false);
        // Se valida la excepcion arrojada por el servicio
        assertThrows(MovimientoInventarioNoEncontradoException.class, () -> movimientoInventarioServicio.eliminarMovimiento(999));
    }

    @Test
    public void modificarMovimiento() {
        // Se establecen los atributos para una modificacion de inventario
        when(movimientoInventarioRepositorio.findById(100)).thenReturn(Optional.of(movimiento));
        when(productoRepositorio.findById("PROD001")).thenReturn(Optional.of(producto));
        when(usuarioRepositorio.findById(10)).thenReturn(Optional.of(usuario));
        when(tipoMovimientoRepositorio.findById(1)).thenReturn(Optional.of(tipoMovimiento));
        when(movimientoInventarioRepositorio.save(ArgumentMatchers.<MovimientoInventario>any())).thenReturn(movimiento);
        // Se ejecuta el servicio de modificar movimiento inventario
        MovimientoInventarioDTO resultado = movimientoInventarioServicio.modificarMovimiento(100, movimientoDTO);
        // Se validan los resultados
        assertEquals("PROD001", resultado.getIdProducto());
        assertEquals("Ingreso por compra", resultado.getObservacion());
    }

    @Test
    public void modificarMovimientoInexistente() {
        // Cuando el movimiento de inventario no existe se retorna empty
        when(movimientoInventarioRepositorio.findById(999)).thenReturn(Optional.empty());
        // Se valida la excepción arrojada por el servicio
        assertThrows(MovimientoInventarioNoEncontradoException.class, () -> movimientoInventarioServicio.modificarMovimiento(999, movimientoDTO));
    }
}