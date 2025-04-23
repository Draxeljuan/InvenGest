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
public class FacturaDTO {
    private int idVenta;
    private LocalDate fecha;
    private String primerNombreCliente;
    private String segundoNombreCliente;
    private String primerApellidoCliente;
    private String segundoApellidoCliente;
    private BigDecimal total;
    private List<DetalleFacturaDTO> detalles;
}
