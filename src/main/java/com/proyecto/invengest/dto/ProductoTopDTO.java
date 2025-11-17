package com.proyecto.invengest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
public class ProductoTopDTO {
    private String idProducto;
    private String nombre;
    private Integer cantidad;
    private BigDecimal acumulado;

}
