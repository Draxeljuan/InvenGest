package com.proyecto.invengest.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class TipoAlerta {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idTipo;

    private String nombre;

    @OneToMany(mappedBy = "idTipo")
    private Set<Alerta> alertas = new LinkedHashSet<>();

}
