package com.proyecto.invengest.dto;

import com.proyecto.invengest.enumeradores.Tipoalerta;
import com.proyecto.invengest.enumeradores.leidaAlerta;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class AlertaDTO {
    private int idAlerta;
    private String idProducto;
    private Date fecha;
    private Tipoalerta tipo;
    private leidaAlerta leida;


}
