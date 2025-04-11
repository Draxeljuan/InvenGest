package com.proyecto.invengest.dto;

import com.proyecto.invengest.enumeradores.tipoReporte;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor

public class ReporteDTO {

    private int idReporte;
    private int idUsuario;
    private String nombre;
    private tipoReporte tipo;
    private String parametros;
    private Date fechaCreacion;
    private Date ultimaEjecucion;

}
