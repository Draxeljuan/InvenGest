package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class VentaRepositorioTest {

    @Autowired
    VentaRepositorio ventaRepositorio;

    @Autowired
    TestEntityManager testEntityManager;

    private Venta venta;

    private final LocalDate fechaInicio = LocalDate.now().minusDays(1);
    private final LocalDate fechaFin = LocalDate.now().plusDays(3);

    @BeforeEach
    void setUp() {

        // Dependencias necesairas para Ventas

        RolUsuario rolusuario = testEntityManager.persistFlushFind(
                RolUsuario.builder()
                        .nombre("Rol de prueba")
                        .build()
        );

        Usuario usuario =  testEntityManager.persistFlushFind(
                Usuario.builder()
                        .nombre("Nombre test")
                        .apellido("Apellido test")
                        .idRol(rolusuario)
                        .nombreUsuario("Usuario test")
                        .contrasena("contrasena test")
                        .build()
        );

        Cliente cliente = testEntityManager.persistFlushFind(
                Cliente.builder()
                        .primerNombre("Cliente nombre test")
                        .build()
        );

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

        Producto producto = testEntityManager.persistFlushFind(
                Producto.builder()
                        .idProducto("PROD001")
                        .nombre("Producto de prueba")
                        .precioVenta(BigDecimal.valueOf(15000))
                        .costoCompra(BigDecimal.valueOf(10000))
                        .fechaIngreso(LocalDate.now())
                        .stock((short) 10)
                        .ubicacion("Bodega A1")
                        .idCategoria(categoria)
                        .idEstado(estado)
                        .build()
        );

        venta = testEntityManager.persistFlushFind(
                Venta.builder()
                        .idUsuario(usuario)
                        .fecha(LocalDate.now())
                        .idCliente(cliente)
                        .total(BigDecimal.valueOf(15000))
                        .build()
        );

        DetalleVenta detalleVenta = testEntityManager.persistFlushFind(
                DetalleVenta.builder()
                        .idProducto(producto)
                        .idVenta(venta)
                        .idVenta(venta)
                        .precioUnitario(producto.getPrecioVenta())
                        .cantidad(1)
                        .subtotal(BigDecimal.valueOf(15000))
                        .build()
        );

    }

    @Test
    public void findVentaConDetallesFound() {
        Venta ventaencontrada = ventaRepositorio.findVentaConDetalles(venta.getIdVenta());

        assertNotNull(ventaencontrada);
        assertEquals(venta.getIdVenta(), ventaencontrada.getIdVenta());
        assertFalse(ventaencontrada.getDetalleVentas().isEmpty());
        assertEquals(1, ventaencontrada.getDetalleVentas().size());
    }

    @Test
    public void obtenerProductosMasVendidos() {

        List<Object[]> productosmasvend = ventaRepositorio.obtenerProductosMasVendidos(fechaInicio, fechaFin);

        //  Validaciones

        assertNotNull(productosmasvend);
        assertFalse(productosmasvend.isEmpty(), "Debe retornar al menos un producto vendido");

        // Tomar el primer arreglo que retorna el metodo para la prueba
        Object[] arreglo = productosmasvend.get(0);

        // Estructura para comparar con el arreglo obtenido
        String idProducto = (String) arreglo[0];
        String nombreProducto = (String) arreglo[1];
        Long cantidadProducto = (Long) arreglo[2];

        // Validar resultados

        assertEquals("PROD001", idProducto);
        assertEquals("Producto de prueba", nombreProducto);
        assertEquals(1, cantidadProducto);




    }

    @Test
    public void obtenerVentasPorFecha() {
        List<Venta> ventasEncontradas = ventaRepositorio.obtenerVentasPorFecha(fechaInicio, fechaFin);

        // Asegurar que ventas no sean null o empty
        assertNotNull(ventasEncontradas, "No debe ser nula la lista de ventas");
        assertFalse(ventasEncontradas.isEmpty(), "Debe de haber al menos un venta en el rango de fechas establecido");

        // Validar resultado de la consulta
        assertEquals(1, ventasEncontradas.size(), "Debe de haber una venta exactamente");

        // Se extrae la información encontrada por la consulta y se compara con la original
        Venta ventaEncontrada = ventasEncontradas.get(0);

        assertEquals(venta.getIdVenta(), ventaEncontrada.getIdVenta(), "Debe coincidir el ID");
        assertEquals(venta.getFecha(), ventaEncontrada.getFecha(), "Debe coincidir la fecha");
        assertEquals(venta.getTotal(), ventaEncontrada.getTotal(), "Debe coincidir el total");

    }

    @Test
    public void obtenerIngresosTotales() {

        double ingresosRegistrados = ventaRepositorio.obtenerIngresosTotales(fechaInicio, fechaFin);

        // Asegurarse que los ingresos registrados no sean invalidos para este test
        assertFalse(ingresosRegistrados <= 0, "Ingresos no pueden ser 0 o negativos");

        // Validar Ingresos

        assertEquals(15000, ingresosRegistrados, "Ingresos deben ser iguales");

    }

    @Test
    public void obtenerNumeroClientesUnicos() {

        int clientesPorFecha = ventaRepositorio.obtenerNumeroClientesUnicos(fechaInicio, fechaFin);

        // Asegurar que los clientes no pueden ser menores o iguales a 0 en este test
        assertFalse(clientesPorFecha <= 0, "No pueden ser 0o negativos los clientes");

        // Validar número de clientes
        assertEquals(1, clientesPorFecha, "Debe ser igual a 1 para este test");


    }
}