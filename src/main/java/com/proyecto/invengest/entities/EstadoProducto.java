package com.proyecto.invengest.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Setter
@Getter
public class EstadoProducto {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_estado", nullable = false)
    private Integer idEstado;

    @Size(max = 20)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 20)
    private String nombre;

    @OneToMany(mappedBy = "idEstado")
    private Set<Producto> productos = new LinkedHashSet<>();

}
