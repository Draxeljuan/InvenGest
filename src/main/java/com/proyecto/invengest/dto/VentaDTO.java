package com.proyecto.invengest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class VentaDTO {

    private Integer idVenta;
    private Integer idUsuario;
    private LocalDate fecha;
    private Integer idCliente;
    private BigDecimal total;
    private List<DetalleVentaDTO> detalles;  // Detalles de la venta

}
