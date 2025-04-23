package com.proyecto.invengest.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity // Anotacion de Entidad que marca esta clase como una entidad JPA que representa una DB
@Getter // Metodos Getter y Setter simplificados con una anotacion
@Setter

public class Producto {


    @Id
    @Size(max = 10)
    @Column(name = "id_producto", nullable = false, length = 10)
    private String idProducto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria idCategoria;

    @Size(max = 255)
    @Column(name = "nombre")
    private String nombre;

    @Column(name = "precio_venta", precision = 38, scale = 2)
    private BigDecimal precioVenta;

    @Column(name = "costo_compra", precision = 38, scale = 2)
    private BigDecimal costoCompra;

    @NotNull
    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "stock", columnDefinition = "smallint UNSIGNED not null")
    private short stock;

    @Size(max = 255)
    @Column(name = "ubicacion")
    private String ubicacion;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_estado", nullable = false)
    private EstadoProducto idEstado;

    @OneToMany(mappedBy = "idProducto")
    private Set<Alerta> alertas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idProducto")
    private Set<DetalleVenta> detalleVentas = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idProducto")
    private Set<MovimientoInventario> movimientoInventarios = new LinkedHashSet<>();

}
