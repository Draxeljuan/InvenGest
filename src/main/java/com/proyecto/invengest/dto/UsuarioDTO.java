package com.proyecto.invengest.dto;


import lombok.AllArgsConstructor;
import lombok.Data;


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
