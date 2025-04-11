package com.proyecto.invengest.repository;

import com.proyecto.invengest.entities.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface VentaRepositorio extends JpaRepository<Venta, Integer> {
    @Query("SELECT v FROM Venta v JOIN FETCH v.detalles WHERE v.idVenta = :idVenta")
    Venta findVentaConDetalles(@Param("idVenta") int idVenta);

    // 🔹 Obtener productos más vendidos en un rango de fechas
    @Query("SELECT d.producto.nombre, SUM(d.cantidad) FROM DetalleVenta d " +
            "WHERE d.venta.fecha BETWEEN :fechaInicio AND :fechaFin " +
            "GROUP BY d.producto.nombre " +
            "HAVING SUM(d.cantidad) >= :minVentas")
    List<Object[]> obtenerProductosMasVendidos(@Param("fechaInicio") Date fechaInicio,
                                               @Param("fechaFin") Date fechaFin,
                                               @Param("minVentas") int minVentas);


    // 🔹 Obtener todas las ventas dentro de un rango de fechas
    @Query("SELECT v FROM Venta v WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<Venta> obtenerVentasPorFecha(@Param("fechaInicio") Date fechaInicio,
                                      @Param("fechaFin") Date fechaFin);


    // 🔹 Obtener el total de ingresos en un rango de fechas
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v " +
            "WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin")
    double obtenerIngresosTotales(@Param("fechaInicio") Date fechaInicio,
                                  @Param("fechaFin") Date fechaFin);
}
