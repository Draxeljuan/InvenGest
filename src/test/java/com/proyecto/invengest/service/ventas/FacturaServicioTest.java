package com.proyecto.invengest.service.ventas;

import com.proyecto.invengest.dto.DetalleFacturaDTO;
import com.proyecto.invengest.dto.FacturaDTO;
import com.proyecto.invengest.entities.Cliente;
import com.proyecto.invengest.entities.DetalleVenta;
import com.proyecto.invengest.entities.Producto;
import com.proyecto.invengest.entities.Venta;
import com.proyecto.invengest.repository.VentaRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class FacturaServicioTest {

    @Mock
    private VentaRepositorio ventaRepositorio;

    @InjectMocks
    private FacturaServicio facturaServicio;

    private Venta venta;
    private Cliente cliente;
    private Producto producto;
    private DetalleVenta detalleVenta;

    @BeforeEach
    void setUp() {

        cliente = Cliente.builder()
                .idCliente(1)
                .primerNombre("Juan")
                .segundoNombre("Carlos")
                .primerApellido("Pérez")
                .segundoApellido("Gómez")
                .build();

        producto = Producto.builder()
                .idProducto("PROD001")
                .nombre("Cable HDMI")
                .build();

        detalleVenta = DetalleVenta.builder()
                .idProducto(producto)
                .cantidad(2)
                .precioUnitario(BigDecimal.valueOf(25_000))
                .subtotal(BigDecimal.valueOf(50_000))
                .build();

        venta = Venta.builder()
                .idVenta(100)
                .fecha(LocalDate.of(2025, 10, 6))
                .idCliente(cliente)
                .detalleVentas(Set.of(detalleVenta))
                .total(BigDecimal.valueOf(50_000))
                .build();

    }

    @Test
    public void generarFacturaDesdeVenta() {
        // Cuando se busque una venta en el repositorio se retorna la creada
        when(ventaRepositorio.findVentaConDetalles(100)).thenReturn(venta);
        // Se llama al servicio de generar facturas de ventas
        FacturaDTO factura = facturaServicio.generarFacturaDesdeVenta(100);
        // Se valida el resultado obtenido
        assertEquals(100, factura.getIdVenta());
        assertEquals("Juan", factura.getPrimerNombreCliente());
        assertEquals(1, factura.getDetalles().size());
        assertEquals("Cable HDMI", factura.getDetalles().get(0).getNombreProducto());
    }
    @Test
    public void generarFacturaDesdeVentaNoExiste() {
        // Si se consulta una factura inexistente se retorna null
        when(ventaRepositorio.findVentaConDetalles(999)).thenReturn(null);
        // Se valida la excepción obtenida al consultar una factura inexistente
        assertThrows(RuntimeException.class, () -> facturaServicio.generarFacturaDesdeVenta(999));
    }

    @Test
    public void generarFacturaPDF() {
        // Se establecen los campos para una factura con su detalle respectivo
        DetalleFacturaDTO detalleDTO = new DetalleFacturaDTO("Cable HDMI", (short) 2, BigDecimal.valueOf(25_000), BigDecimal.valueOf(50_000));
        FacturaDTO factura = new FacturaDTO(100, LocalDate.of(2025, 10, 6), "Juan", "Carlos", "Pérez", "Gómez", BigDecimal.valueOf(50_000), List.of(detalleDTO));
        // Se pasan los campos al servicio para generar una factura
        byte[] pdfBytes = facturaServicio.generarFacturaPDF(factura);
        // Se valida que el servicio retorne datos
        // Como es un test se valida que se genere solo la info
        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);

    }

    @Test
    public void generarFacturaPDFNoExiste() {
        // Se valida la excepción obtenida cuando se intenta generar una factura
        // cuando esta no existe o es null
        assertThrows(RuntimeException.class, () -> facturaServicio.generarFacturaPDF(null));

    }
}