package com.proyecto.invengest.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Table(name = "proveedor")
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_proveedor", nullable = false)
    private Integer id;

    @NotNull
    @Column(name = "id_estado", nullable = false)
    private Integer idEstado;

    @Size(max = 150)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @Size(max = 20)
    @Column(name = "telefono", length = 20)
    private String telefono;

    @Size(max = 100)
    @Column(name = "email", length = 100)
    private String email;

    @Size(max = 255)
    @Column(name = "direccion")
    private String direccion;

    @Size(max = 50)
    @Column(name = "nit", length = 50)
    private String nit;

    @OneToMany(mappedBy = "idProveedor")
    private Set<Producto> productos = new LinkedHashSet<>();

}