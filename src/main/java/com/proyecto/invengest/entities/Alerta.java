package com.proyecto.invengest.entities;


import com.proyecto.invengest.enumeradores.Tipoalerta;
import com.proyecto.invengest.enumeradores.leidaAlerta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter

public class Alerta {

    @Id // Llave primaria idAlerta
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idAlerta;

    @ManyToOne // Relacion muchas alertas a un producto
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto producto;

    // tipo de dato DATE
    @Temporal(TemporalType.DATE)
    private Date fecha;
    // tipo de dato ENUM
    @Enumerated(EnumType.STRING)
    private Tipoalerta tipo;

    @Enumerated(EnumType.STRING)
    private leidaAlerta leida;




}
