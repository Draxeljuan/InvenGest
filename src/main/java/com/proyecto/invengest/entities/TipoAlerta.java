package com.proyecto.invengest.entities;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Setter
@Getter
public class TipoAlerta {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTipo;

    private String nombre;

    @OneToMany(mappedBy = "idTipo")
    private Set<Alerta> alertas = new LinkedHashSet<>();

}
