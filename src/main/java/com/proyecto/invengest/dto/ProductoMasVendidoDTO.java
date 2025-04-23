package com.proyecto.invengest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductoMasVendidoDTO {

    private String idProducto;
    private String nombreProducto;
    private int cantidadVendida;

}
