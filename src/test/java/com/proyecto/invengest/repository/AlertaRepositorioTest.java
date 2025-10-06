package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.*;
import com.proyecto.invengest.enumeradores.leidaAlerta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class AlertaRepositorioTest {

    @Autowired
    AlertaRepositorio alertaRepositorio;

    @Autowired
    TestEntityManager testEntityManager;

    private Producto producto;
    private TipoAlerta tipoAlerta;
    private Alerta alerta;


    // Todo lo que este dentro de este metodo se ejecutara antes de
    // las pruebas unitarias
    @BeforeEach
    void setUp() {

        // Dependencias requeridas por Producto
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

        // Creamos un producto de prueba
        producto = testEntityManager.persistFlushFind(
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

        // Creamos un tipo de alerta de prueba
        tipoAlerta = testEntityManager.persistFlushFind(
                TipoAlerta.builder()
                        .nombre("Tipo de alerta")
                        .build()
        );

        // Creamos una alerta asociada al producto
        alerta = testEntityManager.persistFlushFind(
                Alerta.builder()
                        .idProducto(producto)
                        .fecha(LocalDate.now())
                        .idTipo(tipoAlerta)
                        .leida(leidaAlerta.no_visto)
                        .build()
        );
    }


    @Test
    public void findByIdProductoAndLeidaFound () {
        Optional<Alerta> encontrada = alertaRepositorio.findByIdProductoAndLeida(producto, leidaAlerta.no_visto);

        assertTrue(encontrada.isPresent(), "La alerta fue encontrada");
        assertEquals(alerta.getIdAlerta(),encontrada.get().getIdAlerta());

    }

}