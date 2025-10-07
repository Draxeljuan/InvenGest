package com.proyecto.invengest.service.inventario;

import com.proyecto.invengest.dto.MovimientoInventarioDTO;
import com.proyecto.invengest.dto.ProductoDTO;
import com.proyecto.invengest.entities.Categoria;
import com.proyecto.invengest.entities.EstadoProducto;
import com.proyecto.invengest.entities.Producto;
import com.proyecto.invengest.exceptions.ProductoNoEncontradoException;
import com.proyecto.invengest.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
    class ProductoServicioTest {

    @Mock private ProductoRepositorio productoRepositorio;
    @Mock private CategoriaRepositorio categoriaRepositorio;
    @Mock private EstadoProductoRepositorio estadoProductoRepositorio;
    @Mock private MovimientoInventarioServicio movimientoInventarioServicio;
    @Mock private VentaRepositorio ventaRepositorio;
    @Mock private MovimientoInventarioRepositorio movimientoInventarioRepositorio;

    @InjectMocks
    private ProductoServicio productoServicio;

    private Producto producto;
    private Categoria categoria;
    private EstadoProducto estado;
    private ProductoDTO productoDTO;

    @BeforeEach
    void setUp() {
        categoria = Categoria.builder().idCategoria(1).nombre("Electrónica").build();
        estado = EstadoProducto.builder().id(1).nombre("Normal").build();

        producto = Producto.builder()
                .idProducto("PROD001")
                .nombre("Cable HDMI")
                .precioVenta(BigDecimal.valueOf(25_000))
                .costoCompra(BigDecimal.valueOf(15_000))
                .fechaIngreso(LocalDate.now())
                .stock((short) 20)
                .ubicacion("Estante A")
                .idCategoria(categoria)
                .idEstado(estado)
                .build();

        productoDTO = new ProductoDTO(
                "PROD001", 1, "Cable HDMI",
                BigDecimal.valueOf(25_000), BigDecimal.valueOf(15_000),
                LocalDate.now(), (short) 20, "Estante A", 1
        );
    }

    @Test
    public void listarProductos() {
        // Se consultan todos los productos almacenados diferentes al estado 4 (Descontinuados)
        when(productoRepositorio.findAllByIdEstadoNot(4)).thenReturn(List.of(producto));
        // El servicio lista todos los productos encontrados y los guarda en resultado
        List<ProductoDTO> resultado = productoServicio.listarProductos();
        // Se validan los productos encontrados
        assertEquals(1, resultado.size());
        assertEquals("Cable HDMI", resultado.get(0).getNombre());
    }

    @Test
    public void obtenerProducto() {
        // Cuando se consulte en el repositorio un producto se retorna el creado
        when(productoRepositorio.findById("PROD001")).thenReturn(Optional.of(producto));
        // El servicio obtiene en producto creado en setUp
        ProductoDTO resultado = productoServicio.obtenerProducto("PROD001");
        // Se validan los resultados
        assertEquals("Cable HDMI", resultado.getNombre());
        assertEquals(1, resultado.getIdEstado());
    }

    @Test
    public void obtenerProductoDescontinuado() {
        // Se cambia el estado del producto creado a 4
        producto.setIdEstado(EstadoProducto.builder().id(4).build());
        // Se retorna el producto desde el repositorio cuando se llame a findById
        when(productoRepositorio.findById("PROD001")).thenReturn(Optional.of(producto));
        // Como el producto esta descontinuado, sale la excepción de no encontrado en su lugar
        assertThrows(ProductoNoEncontradoException.class, () -> productoServicio.obtenerProducto("PROD001"));
    }

    @Test
    public void obtenerProducto_NoExiste() {
        // Cuando se consulta un producto que no existe
        when(productoRepositorio.findById("NO_EXISTE")).thenReturn(Optional.empty());
        // Se valida la excepción arrojada por el programa
        assertThrows(ProductoNoEncontradoException.class, () -> productoServicio.obtenerProducto("NO_EXISTE"));
    }

    @Test
    public void buscarPorNombre() {
        // Cuando se consulte un producto por el nombre Cable, se retorna el producto creado
        when(productoRepositorio.findByNombreContainingIgnoreCase("Cable")).thenReturn(List.of(producto));
        // El servicio busca el producto en el repositorio por el nombre cable
        List<ProductoDTO> resultado = productoServicio.buscarPorNombre("Cable");
        // Se valida el resultado obtenido
        assertEquals(1, resultado.size());
        assertEquals("Cable HDMI", resultado.get(0).getNombre());
    }

    @Test
    public void crearProducto() {
        // Se asignan los datos requeridos para crear un producto
        when(categoriaRepositorio.findById(1)).thenReturn(Optional.of(categoria));
        when(estadoProductoRepositorio.findById(1)).thenReturn(Optional.of(estado));
        when(productoRepositorio.save(ArgumentMatchers.<Producto>any())).thenReturn(producto);
        // Al servicio se le añaden los datos configurados previamente
        ProductoDTO resultado = productoServicio.crearProducto(productoDTO);
        // Se validan los resultados
        assertEquals("Cable HDMI", resultado.getNombre());
        verify(movimientoInventarioServicio).registrarMovimientoInventario(any(MovimientoInventarioDTO.class));
    }

    @Test
    public void descontinuarProducto() {
        // Se crea una instancia de la clase estado producto para "Descontinuados"
        EstadoProducto descontinuado = EstadoProducto.builder().id(4).nombre("Descontinuado").build();
        // Cuando se consulte el producto por id se retorna el creado
        // Y cuando se consulte un estado por id 4, se retorna el objeto descontinuado
        when(productoRepositorio.findById("PROD001")).thenReturn(Optional.of(producto));
        when(estadoProductoRepositorio.findById(4)).thenReturn(Optional.of(descontinuado));
        // Se llama al servicio que descontinua el producto
        productoServicio.descontinuarProducto("PROD001");
        // Se validan los resultados
        verify(productoRepositorio).save(producto);
        verify(movimientoInventarioServicio).registrarMovimientoInventario(any(MovimientoInventarioDTO.class));
    }

    @Test
    public void descontinuarProductoNoExiste() {
        // Cuando se consulte un producto inexistente se retorna empty
        when(productoRepositorio.findById("NO_EXISTE")).thenReturn(Optional.empty());
        // Se valida la excepción obtenida al descontinuar un producto no existente
        assertThrows(ProductoNoEncontradoException.class, () -> productoServicio.descontinuarProducto("NO_EXISTE"));

    }
    @Test
    public void modificarProducto() {
        // Se asignan los campos presentes en un producto
        when(productoRepositorio.findById("PROD001")).thenReturn(Optional.of(producto));
        when(categoriaRepositorio.findById(1)).thenReturn(Optional.of(categoria));
        when(estadoProductoRepositorio.findById(1)).thenReturn(Optional.of(estado));
        when(productoRepositorio.save(any(Producto.class))).thenReturn(producto);

        productoDTO.setStock((short) 30); // Cambio de stock
        // Se llama al servicio para realizar una modificación de stock
        ProductoDTO resultado = productoServicio.modificarProducto("PROD001", productoDTO);
        // Se valida el resultado obtenido
        assertEquals(30, resultado.getStock());
        verify(movimientoInventarioServicio).registrarMovimientoInventario(any(MovimientoInventarioDTO.class));
    }
}