package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.Categoria;
import com.proyecto.invengest.entities.EstadoProducto;
import com.proyecto.invengest.entities.Producto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class ProductoRepositorioTest {

    @Autowired
    ProductoRepositorio productoRepositorio;

    @Autowired
    TestEntityManager testEntityManager;

    private Producto producto1;
    private Producto producto2;
    private Producto producto3;
    private Producto producto4;

    // Todo lo que este dentro de este metodo se ejecutara antes de
    // las pruebas unitarias
    @BeforeEach
    void setUp() {

        // Dependencias para producto
        Categoria categoria = testEntityManager.persistFlushFind(
                Categoria.builder()
                        .nombre("Categoría de prueba")
                        .descripcion("Descripción de prueba")
                        .build()
        );

        EstadoProducto estado = testEntityManager.persistFlushFind(
                EstadoProducto.builder()
                        .nombre("Disponible")
                        .build()
        );

        EstadoProducto estadoAlternativo = testEntityManager.persistFlushFind(
                EstadoProducto.builder()
                        .nombre("Descontinuado")
                        .build()
        );

        // Producto 1
        producto1 = testEntityManager.persistFlushFind(
                Producto.builder()
                        .idProducto("PROD001")
                        .nombre("Peluche de Regalo")
                        .precioVenta(BigDecimal.valueOf(45000))
                        .costoCompra(BigDecimal.valueOf(30000))
                        .fechaIngreso(LocalDate.now().minusDays(2))
                        .stock((short) 5)
                        .ubicacion("Bodega A1")
                        .idCategoria(categoria)
                        .idEstado(estado)
                        .build()
        );

        // Producto 2
        producto2 = testEntityManager.persistFlushFind(
                Producto.builder()
                        .idProducto("PROD002")
                        .nombre("Caja de regalo")
                        .precioVenta(BigDecimal.valueOf(12000))
                        .costoCompra(BigDecimal.valueOf(8000))
                        .fechaIngreso(LocalDate.now().minusDays(1))
                        .stock((short) 50)
                        .ubicacion("Bodega B2")
                        .idCategoria(categoria)
                        .idEstado(estado)
                        .build()
        );

        // Producto 3
        producto3 = testEntityManager.persistFlushFind(
                Producto.builder()
                        .idProducto("PROD003")
                        .nombre("Tijeras punta roma")
                        .precioVenta(BigDecimal.valueOf(3500))
                        .costoCompra(BigDecimal.valueOf(2000))
                        .fechaIngreso(LocalDate.now())
                        .stock((short) 2)
                        .ubicacion("Bodega C3")
                        .idCategoria(categoria)
                        .idEstado(estado)
                        .build()
        );

        producto4 = testEntityManager.persistFlushFind(
                Producto.builder()
                        .idProducto("PROD004")
                        .nombre("Chocolatinas Grandes")
                        .precioVenta(BigDecimal.valueOf(4000))
                        .costoCompra(BigDecimal.valueOf(2000))
                        .fechaIngreso(LocalDate.now())
                        .stock((short) 15)
                        .ubicacion("Bodega C4")
                        .idCategoria(categoria)
                        .idEstado(estadoAlternativo)
                        .build()
        );

    }

    @Test
    public void findAllByIdEstadoNot() {

        // Filtrar los productos con el id de tipo descontinuados
        int idEstado = producto4.getIdEstado().getId();
        List<Producto> productosFiltrados = productoRepositorio.findAllByIdEstadoNot(idEstado);

        // Validar que entre los productos filtados no se encuentre el producto 4
        assertFalse(productosFiltrados.contains(producto4), "No contiene el producto 4");
        assertTrue(productosFiltrados.contains(producto1));
        assertTrue(productosFiltrados.contains(producto2));
        assertTrue(productosFiltrados.contains(producto3));

        // Validar que solo hayan 3 productos en la lista de filtrados
        assertEquals(3, productosFiltrados.size());


    }

    @Test
    public void findByNombreContainingIgnoreCase() {

        // Buscar por fragmentos del nombre en distintas combinaciones de mayúsculas/minúsculas
        List<Producto> resultado1 = productoRepositorio.findByNombreContainingIgnoreCase("peluche");
        List<Producto> resultado2 = productoRepositorio.findByNombreContainingIgnoreCase("CAJA");
        List<Producto> resultado3 = productoRepositorio.findByNombreContainingIgnoreCase("punta roma");
        List<Producto> resultado4 = productoRepositorio.findByNombreContainingIgnoreCase("TIJERAS");
        List<Producto> resultado5 = productoRepositorio.findByNombreContainingIgnoreCase("De REGALO");

        // Validar que se encuentra el producto correcto en cada caso
        assertTrue(resultado1.contains(producto1), "Debe encontrar Peluche de regalo ignorando mayúsculas");
        assertTrue(resultado2.contains(producto2), "Debe encontrar Caja de regalo ignorando mayúsculas");
        assertTrue(resultado3.contains(producto3), "Debe encontrar Tijeras punta roma ignorando mayúsculas");
        assertTrue(resultado4.contains(producto3), "Debe encontrar Tijeras punta roma por 'TIJERAS'");
        assertTrue(resultado5.contains(producto1), "Debe encontrar Peluche de regalo por 'De REGALO'");
        assertTrue(resultado5.contains(producto2), "Debe encontrar Caja de regalo por 'De REGALO'");

        // Validar que cada búsqueda devuelve al menos un resultado esperado
        assertEquals(1, resultado1.size(), "Solo debe haber un producto que coincida con 'peluche'");
        assertEquals(1, resultado2.size(), "Solo debe haber un producto que coincida con 'CAJA'");
        assertEquals(1, resultado3.size(), "Solo debe haber un producto que coincida con 'punta roma'");
        assertEquals(1, resultado4.size(), "Solo debe haber un producto que coincida con 'TIJERAS'");
        assertEquals(2, resultado5.size(), "Debe haber dos productos que coincidan con 'De REGALO'");

    }

    @Test
    public void obtenerProductosPorFecha() {

        LocalDate fechaInicio = LocalDate.now().minusDays(3);
        LocalDate fechaFin = LocalDate.now().plusDays(3);

        List<Producto> productosFiltradosFecha = productoRepositorio.obtenerProductosPorFecha(fechaInicio, fechaFin);

        // Validar que los productos filtrados por las fechas establecidas existan
        assertFalse(productosFiltradosFecha.isEmpty(), "No puede estar vacio");

        // Validar que sean 4 los productos que se filtren por la fecha para este test
        assertEquals(4, productosFiltradosFecha.size(), "Tienen que aparecer 4 productos");



    }

    @Test
    public void obtenerProductoPorId() {


        // Almacenamos y consultamos el id de uno de los productos creados
        String idBuscado = producto1.getIdProducto();

        Optional<Producto> producto = productoRepositorio.obtenerProductoPorId(idBuscado);

        // Validación de que el producto si fue encontrado
        assertTrue(producto.isPresent(), "El producto existe");
        assertEquals(producto1.getNombre(), producto.get().getNombre(), "El nombre coincide");
        assertEquals(producto1.getPrecioVenta(), producto.get().getPrecioVenta(), "El precio debe coincidir");

    }
}