package com.proyecto.invengest.dto;

import com.proyecto.invengest.entities.TipoReporte;
import com.proyecto.invengest.enumeradores.tipoReporte;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;


@Data
@AllArgsConstructor

public class ReporteDTO {

    private int idReporte;
    private int fkUsuario;
    private String nombre;
    private TipoReporte fkTipo;
    private String parametros;
    private String contenido;
    private LocalDate fechaCreacion;
    private LocalDate ultimaEjecucion;

}
