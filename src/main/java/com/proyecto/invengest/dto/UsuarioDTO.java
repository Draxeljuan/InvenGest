package com.proyecto.invengest.dto;

import com.proyecto.invengest.enumeradores.rolUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;

@Data
@AllArgsConstructor

public class UsuarioDTO {

    private int idUsuario;
    private String nombre;
    private String email;
    private String telefono;
    private Timestamp ultimoAcceso;
    private rolUsuario rol;
    private String nombreUsuario;


}
