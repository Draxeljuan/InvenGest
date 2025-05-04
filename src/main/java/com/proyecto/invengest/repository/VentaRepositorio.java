package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.Producto;
import com.proyecto.invengest.entities.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface VentaRepositorio extends JpaRepository<Venta, Integer> {
    @Query("SELECT v FROM Venta v JOIN FETCH v.detalleVentas WHERE v.idVenta = :idVenta")
    Venta findVentaConDetalles(@Param("idVenta") int idVenta);

    // Obtener productos más vendidos en un rango de fechas
    @Query("SELECT d.idProducto.idProducto, d.idProducto.nombre, SUM(d.cantidad) " +
            "FROM DetalleVenta d " +
            "JOIN Venta v ON d.idVenta.idVenta = v.idVenta " + // Accedemos correctamente al ID de la venta
            "WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY d.idProducto.idProducto, d.idProducto.nombre")
    List<Object[]> obtenerProductosMasVendidos(@Param("fechaInicio") LocalDate fechaInicio,
                                               @Param("fechaFin") LocalDate fechaFin);



    // Obtener todas las ventas dentro de un rango de fechas
    @Query("SELECT v FROM Venta v WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<Venta> obtenerVentasPorFecha(@Param("fechaInicio") LocalDate fechaInicio,
                                      @Param("fechaFin") LocalDate fechaFin);


    // Obtener el total de ingresos en un rango de fechas
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v " +
            "WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin")
    double obtenerIngresosTotales(@Param("fechaInicio") LocalDate fechaInicio,
                                  @Param("fechaFin") LocalDate fechaFin);

    // Obtener el Numero de Clientes en un rango de fechas
    @Query("SELECT COUNT(DISTINCT v.idCliente) FROM Venta v WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin")
    int obtenerNumeroClientesUnicos(@Param("fechaInicio") LocalDate fechaInicio, @Param("fechaFin") LocalDate fechaFin);

}
