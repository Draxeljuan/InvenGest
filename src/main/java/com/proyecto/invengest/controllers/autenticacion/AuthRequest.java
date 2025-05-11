package com.proyecto.invengest.controllers.autenticacion;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AuthRequest {
    private String nombreUsuario;
    private String contrasena;
}
