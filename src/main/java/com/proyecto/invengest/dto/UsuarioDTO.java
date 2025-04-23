package com.proyecto.invengest.dto;

import com.proyecto.invengest.entities.RolUsuario;
import com.proyecto.invengest.enumeradores.rolUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;
import java.time.Instant;

@Data
@AllArgsConstructor

public class UsuarioDTO {

    private int idUsuario;
    private String nombre;
    private String email;
    private String telefono;
    private Instant ultimoAcceso;
    private String rolUsuario;
    private String nombreUsuario;


}
