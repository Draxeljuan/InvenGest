package com.proyecto.invengest.entities;

import com.proyecto.invengest.enumeradores.TipoMovimiento;
import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


@Entity
@Getter
@Setter

@Table(name = "movimiento_inventario")
public class MovimientoInventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idMovimientoInventario;

    @ManyToOne
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto idProducto;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario idUsuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento")
    private TipoMovimiento tipoMovimiento;


    private int cantidad;

    @Temporal(TemporalType.DATE)
    @Column(name = "fecha_movimiento")
    private Date fechaMovimiento;

    private String observacion;



}
