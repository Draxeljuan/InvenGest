package com.proyecto.invengest.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter

public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Anotación que genera el id_categoria
    private int idCategoria;

    private String nombre;

    private String descripcion;

    @OneToMany(mappedBy = "categoria") // Anotación que indica relacion uno a mucho con Producto
    // mappedBy = "categoria" hace referencia al atributo categoria en Producto
    private List<Producto> productos;
}
