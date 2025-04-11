package com.proyecto.invengest.entities;


import com.proyecto.invengest.enumeradores.EstadoProducto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Entity // Anotacion de Entidad que marca esta clase como una entidad JPA que representa una DB
@Getter // Metodos Getter y Setter simplificados con una anotacion
@Setter

public class Producto {

    @Id // Anotacion para identificar una llave primaria
    private String idProducto; // Al ser un Varchar en la DB lo dejamos como String

    @ManyToOne // Anotacion que indica Relacion muchos a uno Entre Producto y Categoria
    @JoinColumn(name = "id_categoria", nullable = false)
    // Anotacion que define la columna en la base de datos que se usara como clave foranea
    // La columna no puede ser null
    private Categoria categoria;

    private String nombre;

    private BigDecimal precioVenta;

    private BigDecimal costoCompra;

    @Temporal(TemporalType.DATE) // Anotación que indica que el tipo de dato es una Fecha
    private Date fechaIngreso;

    private short stock;

    private String ubicacion;

    @Enumerated(EnumType.STRING) // Anotación que indica que el estado es un ENUM
    private EstadoProducto estado;



}
