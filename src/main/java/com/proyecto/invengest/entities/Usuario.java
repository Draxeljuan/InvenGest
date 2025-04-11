package com.proyecto.invengest.entities;


import com.proyecto.invengest.enumeradores.rolUsuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.sql.Timestamp;



@Entity
@Getter
@Setter

public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idUsuario;

    private String nombre;

    private String email;

    private String telefono;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ultimo_acceso")
    private Timestamp ultimoAcceso;

    @Enumerated(EnumType.STRING)
    private rolUsuario rol;


    @Column(name = "nombre_usuario")
    private String nombreUsuario;

    private String contrasena;




}
