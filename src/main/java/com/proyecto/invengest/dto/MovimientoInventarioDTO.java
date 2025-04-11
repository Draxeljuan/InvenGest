package com.proyecto.invengest.dto;

import com.proyecto.invengest.entities.Producto;
import com.proyecto.invengest.entities.Usuario;
import com.proyecto.invengest.enumeradores.TipoMovimiento;

import lombok.Data;

import java.util.Date;

@Data

public class MovimientoInventarioDTO {

    private int idMovimientoInventario;
    private String idProducto;
    private int idUsuario;
    private TipoMovimiento tipoMovimiento;
    private int cantidad;
    private Date fechaMovimiento;
    private String observacion;

    public MovimientoInventarioDTO(int idMovimientoInventario, Usuario idUsuario, Producto idProducto, TipoMovimiento tipoMovimiento, int cantidad, Date fechaMovimiento, String observacion) {
        this.idMovimientoInventario = idMovimientoInventario;
        this.idUsuario = idUsuario.getIdUsuario();
        this.idProducto = idProducto.getIdProducto();
        this.tipoMovimiento = tipoMovimiento;
        this.cantidad = cantidad;
        this.fechaMovimiento = fechaMovimiento;
        this.observacion = observacion;
    }
}
