package com.proyecto.invengest.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.proyecto.invengest.enumeradores.EstadoProducto;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

import java.util.Date;


@Data


public class ProductoDTO {
    private String idProducto;
    private int idCategoria;
    private String nombre;
    private BigDecimal precioVenta;
    private BigDecimal costoCompra;
    private short stock;
    private String ubicacion;
    @Enumerated(EnumType.STRING)
    private EstadoProducto estado;


    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fechaIngreso;


    
    public ProductoDTO(String idProducto, int idCategoria, String nombre, BigDecimal precioVenta, BigDecimal costoCompra, short stock, String ubicacion, EstadoProducto estado, Date fechaIngreso) {
        this.idProducto = idProducto;
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.precioVenta = precioVenta;
        this.costoCompra = costoCompra;
        this.stock = stock;
        this.ubicacion = ubicacion;
        this.estado = estado;
        this.fechaIngreso = new Date();
        this.fechaIngreso.setTime(System.currentTimeMillis());

    }
    

}


