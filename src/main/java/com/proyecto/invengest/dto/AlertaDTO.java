package com.proyecto.invengest.dto;


import com.proyecto.invengest.enumeradores.leidaAlerta;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;


@Data
@AllArgsConstructor
public class AlertaDTO {
    private int idAlerta;
    private String idProducto;
    private LocalDate fecha;
    private int tipo;
    private leidaAlerta leida;



}
