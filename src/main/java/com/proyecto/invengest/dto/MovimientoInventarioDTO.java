package com.proyecto.invengest.dto;

import com.proyecto.invengest.entities.TipoMovimiento;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class MovimientoInventarioDTO {

    private Integer idMovimientoInventario;
    private String idProducto;
    private Integer idUsuario;
    private Integer idMovimiento;
    private int cantidad;
    private LocalDate fechaMovimiento;
    private String observacion;


}
