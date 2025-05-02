package com.proyecto.invengest.dto;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstadoProductoDTO {
    private Integer idEstado;

    @NotNull(message = "El nombre del estado no puede estar vacío")
    @Size(max = 20, message = "El nombre del estado no puede superar los 20 caracteres")
    private String nombre;
}
