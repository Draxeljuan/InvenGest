package com.proyecto.invengest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
public class VentaDTO {

    private int idVenta;
    private int idUsuario;
    private String nombreCliente;
    private String apellidoCliente;
    private BigDecimal total;
    private Date fecha;
    private List<DetalleVentaDTO> detalles;



}
