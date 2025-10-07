package com.proyecto.invengest.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Cliente {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente", nullable = false)
    private Integer idCliente;

    @Size(max = 20)
    @NotNull
    @Column(name = "primer_nombre", nullable = false, length = 20)
    private String primerNombre;

    @Size(max = 20)
    @Column(name = "segundo_nombre", length = 20)
    private String segundoNombre;

    @Size(max = 20)
    @Column(name = "primer_apellido", length = 20)
    private String primerApellido;

    @Size(max = 20)
    @Column(name = "segundo_apellido", length = 20)
    private String segundoApellido;

    @OneToMany(mappedBy = "idCliente")
    private Set<Venta> ventas = new LinkedHashSet<>();

}
