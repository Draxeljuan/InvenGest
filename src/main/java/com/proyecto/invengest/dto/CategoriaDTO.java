package com.proyecto.invengest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CategoriaDTO {
    private int idCategoria;
    private String nombre;
    private String descripcion;
}
