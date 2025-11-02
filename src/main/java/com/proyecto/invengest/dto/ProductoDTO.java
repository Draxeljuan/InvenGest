package com.proyecto.invengest.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

import java.time.LocalDate;

@Data
@AllArgsConstructor

public class ProductoDTO {
    private String idProducto;
    private int idCategoria;
    private String nombre;
    private BigDecimal precioVenta;
    private BigDecimal costoCompra;
    private LocalDate fechaIngreso;
    private short stock;
    private String ubicacion;
    private int idEstado;



    

}


