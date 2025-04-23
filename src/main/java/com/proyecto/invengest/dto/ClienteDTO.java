package com.proyecto.invengest.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClienteDTO {

    private Integer idCliente;

    @NotNull(message = "El primer nombre no puede estar vacío")
    @Size(max = 20, message = "El primer nombre no puede exceder los 20 caracteres")
    private String primerNombre;

    @Size(max = 20, message = "El segundo nombre no puede exceder los 20 caracteres")
    private String segundoNombre;

    @NotNull(message = "El primer apellido no puede estar vacío")
    @Size(max = 20, message = "El primer apellido no puede exceder los 20 caracteres")
    private String primerApellido;

    @Size(max = 20, message = "El segundo apellido no puede exceder los 20 caracteres")
    private String segundoApellido;
}
