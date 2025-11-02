package com.proyecto.invengest.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ProveedorDTO {
    private int id;
    private int idEstado;
    private String nombre;
    private String telefono;
    private String email;
    private String direccion;
    private String nit;

}
