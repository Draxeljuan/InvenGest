package com.proyecto.invengest.service.ventas;

import com.proyecto.invengest.dto.DetalleVentaDTO;
import com.proyecto.invengest.dto.MovimientoInventarioDTO;
import com.proyecto.invengest.dto.VentaDTO;
import com.proyecto.invengest.entities.*;
import com.proyecto.invengest.exceptions.VentaNoEncontradaException;
import com.proyecto.invengest.repository.*;
import com.proyecto.invengest.service.inventario.AlertaServicio;
import com.proyecto.invengest.service.inventario.MovimientoInventarioServicio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class VentaServicioTest {

    @Mock private VentaRepositorio ventaRepositorio;
    @Mock private ClienteRepositorio clienteRepositorio;
    @Mock private ProductoRepositorio productoRepositorio;
    @Mock private UsuarioRepositorio usuarioRepositorio;
    @Mock private AlertaServicio alertaServicio;
    @Mock private MovimientoInventarioServicio movimientoInventarioServicio;
    @Mock private EstadoProductoRepositorio estadoProductoRepositorio;

    @InjectMocks
    private VentaServicio ventaServicio;

    private Venta venta;
    private Producto producto;
    private Usuario usuario;
    private Cliente cliente;
    private EstadoProducto estado;
    private DetalleVentaDTO detalleDTO;
    private VentaDTO ventaDTO;

    @BeforeEach
    void setUp() {

        estado = EstadoProducto.builder().id(1).nombre("Normal").build();
        producto = Producto.builder().idProducto("PROD001").nombre("Cable HDMI").stock((short) 20).precioVenta(BigDecimal.valueOf(25_000)).idEstado(estado).build();
        usuario = Usuario.builder().idUsuario(10).nombreUsuario("admin").build();
        cliente = Cliente.builder().idCliente(1).primerNombre("Juan").build();

        detalleDTO = new DetalleVentaDTO("PROD001", BigDecimal.valueOf(50_000), BigDecimal.valueOf(25_000), 2);
        ventaDTO = new VentaDTO(null, 10, LocalDate.now(), 1, BigDecimal.ZERO, List.of(detalleDTO));

        venta = Venta.builder().idVenta(100).idUsuario(usuario).idCliente(cliente).fecha(LocalDate.now()).total(BigDecimal.valueOf(50_000)).detalleVentas(new LinkedHashSet<>()).build();

    }

    @Test
    public void listarVentas() {
        // Cuando se busque una venta en el repositorio se retorna la creada
        when(ventaRepositorio.findAll()).thenReturn(List.of(venta));
        // Se llama al servicio que lista las ventas
        List<VentaDTO> resultado = ventaServicio.listarVentas();
        // Se valida el resultado obtenido
        assertEquals(1, resultado.size());
        assertEquals(100, resultado.get(0).getIdVenta());
    }

    @Test
    public void obtenerVenta() {
        // Cuando se busca una venta en el repositorio se retorna la venta y detalle creados
        when(ventaRepositorio.existsById(100)).thenReturn(true);
        when(ventaRepositorio.findVentaConDetalles(100)).thenReturn(venta);
        // Se llama al servicio para obtener ventas
        VentaDTO resultado = ventaServicio.obtenerVenta(100);
        // Se valida el resultado
        assertEquals(100, resultado.getIdVenta());
    }

    @Test
    public void obtenerVentaNoExiste() {
        // Cuando se busca una venta en el repositorio que no existe se retorna false
        when(ventaRepositorio.existsById(999)).thenReturn(false);
        // Se valida la excepción obtenida
        assertThrows(VentaNoEncontradaException.class, () -> ventaServicio.obtenerVenta(999));
    }
    @Test
    public void crearVenta() {
        // Se configuran los campos necesarios para crear la venta en el repositorio
        when(usuarioRepositorio.findById(10)).thenReturn(Optional.of(usuario));
        when(clienteRepositorio.findById(1)).thenReturn(Optional.of(cliente));
        when(productoRepositorio.findById("PROD001")).thenReturn(Optional.of(producto));
        when(estadoProductoRepositorio.findById(1)).thenReturn(Optional.of(estado));
        when(ventaRepositorio.saveAndFlush(ArgumentMatchers.<Venta>any())).thenReturn(venta);
        when(productoRepositorio.save(ArgumentMatchers.<Producto>any())).thenReturn(producto);
        // Se llama el servicio para crear la venta
        VentaDTO resultado = ventaServicio.crearVenta(ventaDTO);
        // Se validan los resultados
        assertEquals(100, resultado.getIdVenta());
        verify(alertaServicio).generarAlertaStock(producto);
        verify(movimientoInventarioServicio).registrarMovimientoInventario(ArgumentMatchers.<MovimientoInventarioDTO>any());
    }

    @Test
    public void crearVentaStockInsuficiente() {
        // Intentar crear una venta sin stock suficiente
        producto.setStock((short) 1); // menor que cantidad solicitada
        when(usuarioRepositorio.findById(10)).thenReturn(Optional.of(usuario));
        when(clienteRepositorio.findById(1)).thenReturn(Optional.of(cliente));
        when(productoRepositorio.findById("PROD001")).thenReturn(Optional.of(producto));
        // Validar el resultado
        assertThrows(IllegalArgumentException.class, () -> ventaServicio.crearVenta(ventaDTO));
    }

    @Test
    public void eliminarVenta() {
        // Cuando se busque una venta en el repositorio con id 100 retorna true
        when(ventaRepositorio.existsById(100)).thenReturn(true);
        // Se llama al servicio para eliminar venta
        ventaServicio.eliminarVenta(100);
        // Se valida la eliminación
        verify(ventaRepositorio).deleteById(100);
    }

    @Test
    public void eliminarVentaNoExiste() {
        // Si se busca una venta inexistente retorna false
        when(ventaRepositorio.existsById(999)).thenReturn(false);
        // Se valida la excepción obtenida al eliminar una venta inexistente
        assertThrows(VentaNoEncontradaException.class, () -> ventaServicio.eliminarVenta(999));
    }

    @Test
    public void actualizarVenta() {
        // Se añaden campos para una actualizacion
        venta.setDetalleVentas(new LinkedHashSet<>());
        when(ventaRepositorio.findById(100)).thenReturn(Optional.of(venta));
        when(usuarioRepositorio.findById(10)).thenReturn(Optional.of(usuario));
        when(clienteRepositorio.findById(1)).thenReturn(Optional.of(cliente));
        when(productoRepositorio.findById("PROD001")).thenReturn(Optional.of(producto));
        when(ventaRepositorio.save(ArgumentMatchers.<Venta>any())).thenReturn(venta);
        // Se llama al servicio para actualizar una venta
        VentaDTO resultado = ventaServicio.actualizarVenta(100, ventaDTO);
        // Se validan los resultados
        assertEquals(100, resultado.getIdVenta());
        assertEquals("Cable HDMI", resultado.getDetalles().get(0).getIdProducto());

    }

    @Test
    public void actualizarVentaNoExiste() {

        // Si se consulta una venta inexistente retorna empty
        when(ventaRepositorio.findById(999)).thenReturn(Optional.empty());
        // Se valida la excepción obtenida
        assertThrows(VentaNoEncontradaException.class, () -> ventaServicio.actualizarVenta(999, ventaDTO));

    }

}