package com.proyecto.invengest.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;


import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Usuario {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;

    @Size(max = 30)
    @NotNull
    @Column(name = "nombre", nullable = false, length = 30)
    private String nombre;

    @Size(max = 30)
    @NotNull
    @Column(name = "apellido", nullable = false, length = 30)
    private String apellido;

    @Size(max = 30)
    @Column(name = "email", length = 30)
    private String email;

    @Size(max = 12)
    @Column(name = "telefono", length = 12)
    private String telefono;

    @Column(name = "ultimo_acceso")
    private Instant ultimoAcceso;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_rol", nullable = false)
    private RolUsuario idRol;

    @Size(max = 30)
    @NotNull
    @Column(name = "nombre_usuario", nullable = false, length = 30)
    private String nombreUsuario;

    @Size(max = 255)
    @NotNull
    @Column(name = "contrasena", nullable = false)
    private String contrasena;

    @OneToMany(mappedBy = "idUsuario")
    private Set<MovimientoInventario> movimientoInventarios = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUsuario")
    private Set<Reporte> reportes = new LinkedHashSet<>();

    @OneToMany(mappedBy = "idUsuario")
    private Set<Venta> ventas = new LinkedHashSet<>();

}
