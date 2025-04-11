package com.proyecto.invengest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;


@Data
@AllArgsConstructor
public class DetalleVentaDTO {

    private String idProducto; // ID del producto desde la base de datos
    private int cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}
