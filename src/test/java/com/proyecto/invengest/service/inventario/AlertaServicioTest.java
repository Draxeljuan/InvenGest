package com.proyecto.invengest.service.inventario;

import com.proyecto.invengest.dto.AlertaDTO;
import com.proyecto.invengest.entities.*;
import com.proyecto.invengest.enumeradores.leidaAlerta;
import com.proyecto.invengest.exceptions.AlertaNoEncontradaException;
import com.proyecto.invengest.repository.AlertaRepositorio;
import com.proyecto.invengest.repository.ProductoRepositorio;
import com.proyecto.invengest.repository.TipoAlertaRepositorio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlertaServicioTest {

    @Mock
    private AlertaRepositorio alertaRepositorio;
    @Mock
    private ProductoRepositorio productoRepositorio;
    @Mock
    private TipoAlertaRepositorio tipoAlertaRepositorio;
    @InjectMocks
    private AlertaServicio alertaServicio;

    private Producto producto;
    private Alerta alerta;
    private TipoAlerta tipoStockBajo;
    private TipoAlerta tipoSinStock;

    @BeforeEach
    void setUp() {
        tipoStockBajo = TipoAlerta.builder()
                .idTipo(1)
                .nombre("Stock Bajo")
                .build();
        tipoSinStock = TipoAlerta.builder()
                .idTipo(2)
                .nombre("Sin Stock")
                .build();

        Categoria categoria = Categoria.builder()
                .idCategoria(1)
                .nombre("Papeleria")
                .build();

        EstadoProducto estado = EstadoProducto.builder()
                .id(2)
                .nombre("Stock Bajo")
                .build();

        producto = Producto.builder()
                .idProducto("PROD001")
                .nombre("Pliego Cartulina")
                .stock((short) 5)
                .fechaIngreso(LocalDate.now())
                .idCategoria(categoria)
                .idEstado(estado)
                .build();

        alerta = Alerta.builder()
                .idAlerta(100)
                .idProducto(producto)
                .idTipo(tipoStockBajo)
                .fecha(LocalDate.now())
                .leida(leidaAlerta.no_visto)
                .build();
    }

    @Test
    public void listarAlertas() {

        // Se crea un producto con alto stock
        Producto productoAltoStock = Producto.builder().idProducto("PROD002").stock((short) 15).build();
        Alerta alertaAlta = Alerta.builder().idProducto(productoAltoStock).build();
        // Cuando se ejecute el metodo se pasaran las 2 posibles alertas al servicio
        when(alertaRepositorio.findAll()).thenReturn(List.of(alerta, alertaAlta));
        // El servicio filtra entre las alertas que considera validas y las almacena
        // en resultado
        List<AlertaDTO> resultado = alertaServicio.listarAlertas();
        //Validamos los resultados
        assertEquals(1, resultado.size(), "solo debe haber una alerta");
        assertEquals("PROD001", resultado.get(0).getIdProducto(), "Deben coincidir los ID");
    }

    @Test
    void listarAlertasConProducto() {

        // Al listar las alertas retorna la creada por nosotros
        when(alertaRepositorio.findAll()).thenReturn(List.of(alerta));
        // en un map se listan las alertas mediante el servicio listarAlertasConProducto
        List<Map<String, Object>> resultado = alertaServicio.listarAlertasConProducto();
        // Se validan los resultados
        assertEquals(1, resultado.size(), "Solo tiene que haber una alerta");
        Map<String, Object> alertaMap = resultado.get(0);
        assertEquals("PROD001", alertaMap.get("idProducto"));
        assertEquals("Pliego Cartulina", alertaMap.get("nombreProducto"));
        assertEquals(leidaAlerta.no_visto, alertaMap.get("leida"));
    }

    @Test
    public void limpiarAlertasInnecesarias() {

        // Se asigna un stock suficiente para el producto creado
        producto.setStock((short) 15);
        when(alertaRepositorio.findAll()).thenReturn(List.of(alerta));
        when(productoRepositorio.obtenerProductoPorId("PROD001")).thenReturn(Optional.of(producto));

        // Se valida que se elimine la alerta tras modificar el stock
        alertaServicio.limpiarAlertasInnecesarias();

        verify(alertaRepositorio).deleteById(100);

    }

    @Test
    public void obtenerAlerta() {

        // Se consulta una alerta por id y se retorna la alerta creada
        when(alertaRepositorio.findById(100)).thenReturn(Optional.of(alerta));
        AlertaDTO dto = alertaServicio.obtenerAlerta(100);
        assertEquals(100, dto.getIdAlerta());
    }

    @Test
    public void obtenerAlertaNoExiste() {
        // Se consulta una alerta que no existe para validar la excepcion
        when(alertaRepositorio.findById(999)).thenReturn(Optional.empty());
        assertThrows(AlertaNoEncontradaException.class, () -> alertaServicio.obtenerAlerta(999));
    }

    @Test
    public void generarAlerta_StockBajo() {

        // El test genera una alerta para el producto inicializado
        // primero ajusta las consultas al repositorio relacionadas al producto
        when(alertaRepositorio.findByIdProductoAndLeida(producto, leidaAlerta.no_visto)).thenReturn(Optional.empty());
        when(tipoAlertaRepositorio.findById(1)).thenReturn(Optional.of(tipoStockBajo));
        // Luego genera la alerta y valida si existe en el repositorio temporal
        alertaServicio.generarAlertaStock(producto);

        verify(alertaRepositorio).save(ArgumentMatchers.<Alerta>any());
    }

    @Test
    public void generarAlerta_StockInexistente(){

        // Se construye un tipo de alerta sin stock
        TipoAlerta tipoAlerta = TipoAlerta.builder().idTipo(2).nombre("Sin Stock").build();
        producto.setStock((short) 0); // Se baja a 0 el stock del producto
        alerta.setIdTipo(TipoAlerta.builder().idTipo(1).build()); // La alerta actual esta en bajo stock

        when(alertaRepositorio.findByIdProductoAndLeida(producto, leidaAlerta.no_visto)).thenReturn(Optional.of(alerta));
        when(tipoAlertaRepositorio.findById(2)).thenReturn(Optional.of(tipoAlerta));

        // Cuando se acceda al servicio de generar alerta
        // Se pasa el producto ahora sin stock y se cambia el tipo de alerta
        alertaServicio.generarAlertaStock(producto);

        // Se valida el cambio
        verify(alertaRepositorio).save(alerta);
    }

    @Test
    public void eliminarAlertaExistente() {
        // Declaramos que existe una alerta de id 100 cuando se consulte
        when(alertaRepositorio.existsById(100)).thenReturn(true);

        alertaServicio.eliminarAlerta(100);
        // El servicio elimina la alerta y se valida el resultado
        verify(alertaRepositorio).deleteById(100);
    }
    @Test
    public void eliminarAlertaInexistente() {
        // Declaramos que cuando se consulte una alerta inexistente retorne false
        when(alertaRepositorio.existsById(999)).thenReturn(false);
        // Se valida la excepción obtenida
        assertThrows(AlertaNoEncontradaException.class, () -> alertaServicio.eliminarAlerta(999));
    }
}