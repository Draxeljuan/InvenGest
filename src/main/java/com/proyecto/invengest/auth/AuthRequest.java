package com.proyecto.invengest.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter

public class AuthRequest {
    private String nombreUsuario;
    private String contrasena;
}
