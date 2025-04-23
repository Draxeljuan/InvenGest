package com.proyecto.invengest.entities;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;


@Entity
@Getter
@Setter

@Table(name = "detalle_venta")
public class DetalleVenta {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_detalleventa", nullable = false)
    private Integer idDetalleventa;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_producto", nullable = false)
    private Producto idProducto;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_venta", nullable = false)
    private Venta idVenta;

    @Column(name = "precio_unitario", precision = 38, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "subtotal", precision = 38, scale = 2)
    private BigDecimal subtotal;

    @NotNull
    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

}


