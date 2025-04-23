package com.proyecto.invengest.auth;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class AuthRequest {
    private String nombreUsuario;
    private String contrasena;
}
