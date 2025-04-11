package com.proyecto.invengest.entities;


import com.proyecto.invengest.enumeradores.tipoReporte;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter

public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idReporte;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    private String nombre;

    @Enumerated(EnumType.STRING)
    private tipoReporte tipo;


    @Column(columnDefinition = "TEXT") // Guardara un JSON con parámetros
    private String parametros;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_creacion")
    private Date fechaCreacion;

    @Temporal(TemporalType.DATE)
    @Column(name = "ultima_ejecucion")
    private Date ultimaEjecucion;

}
